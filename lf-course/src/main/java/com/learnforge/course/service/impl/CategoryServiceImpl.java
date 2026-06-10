package com.learnforge.course.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.common.constants.Constant;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.enums.CommonStatus;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.*;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.constants.CourseErrorInfo;
import com.learnforge.course.constants.CourseStatus;
import com.learnforge.course.domain.dto.CategoryAddDTO;
import com.learnforge.course.domain.dto.CategoryDisableOrEnableDTO;
import com.learnforge.course.domain.dto.CategoryListDTO;
import com.learnforge.course.domain.dto.CategoryUpdateDTO;
import com.learnforge.course.domain.po.Category;
import com.learnforge.course.domain.po.Course;
import com.learnforge.course.domain.vo.CategoryInfoVO;
import com.learnforge.course.domain.vo.CategoryVO;
import com.learnforge.course.domain.vo.SimpleCategoryVO;
import com.learnforge.course.mapper.CategoryMapper;
import com.learnforge.course.mapper.SubjectCategoryMapper;
import com.learnforge.course.service.ICategoryService;
import com.learnforge.course.service.ICourseDraftService;
import com.learnforge.course.service.ICourseService;
import com.learnforge.course.utils.CategoryDataWrapper;
import com.learnforge.course.utils.CategoryDataWrapper2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * <p>
 * Course Category Service Implementation Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-14
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SubjectCategoryMapper subjectCategoryMapper;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICourseDraftService courseDraftService;

    @Resource(name = "taskExecutor")
    private Executor taskExecutor;

    @Override
    public List<CategoryVO> list(CategoryListDTO categoryListDTO) {

        //1. Sort search conditions by priority ascending, id descending
        LambdaQueryWrapper<Category> queryWrapper =
                Wrappers.lambdaQuery(Category.class)
                        .orderByAsc(Category::getPriority)
                        .orderByDesc(Category::getUpdateTime);
        //2. Query data
        List<Category> list = super.list(queryWrapper);
        if (CollUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        //3. Get the number of courses in the course category
        Map<Long, Long> thirdCategoryNumMap = this.statisticThirdCategory();

        Map<Long, Integer> cateIdAndNumMap = courseService
                .countCourseNumOfCategory();
        //4. Assemble data using TreeDataUtils
        List<CategoryVO> categoryVOS = TreeDataUtils.parseToTree(list, CategoryVO.class,
                //4.1 Set conversion
                (category, categoryVO) -> {
                    //4.2 Set tertiary category count, course count, status description, sort
                    categoryVO.setThirdCategoryNum(NumberUtils.null2Zero(thirdCategoryNumMap.get(category.getId())).intValue());
                    categoryVO.setCourseNum(NumberUtils.null2Zero(cateIdAndNumMap.get(category.getId())));
                    categoryVO.setStatusDesc(CommonStatus.desc(category.getStatus()));
                    categoryVO.setIndex(category.getPriority());
                }, new CategoryDataWrapper2());
        //5. Filter by conditions
        if (CollUtils.isNotEmpty(categoryVOS)) {
            return fiter(categoryVOS, categoryListDTO);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(rollbackFor = {DbException.class, Exception.class})
    public void add(CategoryAddDTO categoryAddDTO) {

        //Check if the name is duplicated
        checkSameName(categoryAddDTO.getParentId(), categoryAddDTO.getName(), null);
        int level = 1; //Default primary category
        if (CourseConstants.CATEGORY_ROOT != categoryAddDTO.getParentId()) {
            //Check if the parent category exists
            Category parentCategory = this.baseMapper.selectById(categoryAddDTO.getParentId());
            if (parentCategory == null) {
                throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_PARENT_NOT_FOUND);
            }
            //Tertiary course category cannot create subcategories
            if (parentCategory.getLevel() == 3) {
                throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_CREATE_ON_THIRD);
            }
            //Category level, parent category + 1
            level = parentCategory.getLevel() + 1;
        }
        if (level > 3) {
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_ADD_OVER_THIRD_LEVEL);
        }

        //Convert request parameters to PO
        Category category = BeanUtils.copyBean(categoryAddDTO, Category.class, (dto, po) -> {
            po.setPriority(dto.getIndex());
            po.setStatus(CommonStatus.DISABLE.getValue());
        });
        //Set category level
        category.setLevel(level);
        if (this.baseMapper.insert(category) <= 0) {
            throw new DbException(null);
        }
    }

    @Override
    public CategoryInfoVO get(Long id) {
        //1. Query Data
        Category category = this.baseMapper.selectById(id);
        //1.1 Check for null
        if (category == null) {
            return new CategoryInfoVO();
        }
        //2. Data assembly
        CategoryInfoVO categoryInfoVO = BeanUtils.toBean(category, CategoryInfoVO.class);
        //2.1. Course category level
        categoryInfoVO.setCategoryLevel(category.getLevel());
        //2.2. Course Category Status Description
        categoryInfoVO.setStatusDesc(CommonStatus.desc(category.getStatus()));
        //2.3 Course Category Sequence Number
        categoryInfoVO.setIndex(category.getPriority());
        Long firstCategoryId = null;
        if (category.getLevel() == 3) {
            //2.4. Query Secondary Course Category
            Category secondCategory = this.baseMapper.selectById(category.getParentId()); //Current Secondary Directory
            //2.5. Set Secondary Course Category Name
            categoryInfoVO.setSecondCategoryName(secondCategory.getName());
            //2.6. Set Primary Course Category ID
            firstCategoryId = secondCategory.getParentId();
        } else if (category.getLevel() == 2) {
            //2.7. Set Primary Course Category ID
            firstCategoryId = category.getParentId();
        }

        if (firstCategoryId != null) {
            //2.8. Query Primary Course Category Information
            Category firstCategory = this.baseMapper.selectById(firstCategoryId);
            //2.9 Set Primary Course Category Name
            categoryInfoVO.setFirstCategoryName(firstCategory.getName());
        }
        return categoryInfoVO;
    }

    @Override
    public void delete(Long id) {
        //1. Sub-Category Query Conditions
        LambdaQueryWrapper<Category> queryWrapper =
                Wrappers.lambdaQuery(Category.class)
                        .eq(Category::getParentId, id);
        //1.1 Query Sub-Category Information
        List<Category> categories = this.baseMapper.selectList(queryWrapper);
        //1.2. Sub-Category Null Check
        if (CollectionUtil.isNotEmpty(categories)) { //Category Has Sub-Category
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_HAVE_CHILD);
        }
        //2. Query Category Information
        Category category = this.baseMapper.selectById(id);
        //2.1. Null Check
        if (category == null) {
            throw new DbException(ErrorInfo.Msg.DB_DELETE_EXCEPTION);
        }
        //3. Statistics on Number of Courses in Category
        Integer courseNum = courseService.countCourseNumOfCategory(id);
        //3.1. Null Check for Number of Courses in Category
        if (courseNum > 0) {
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_DELETE_HAVE_COURSE);
        }
        //4. Statistics on Number of Questions in Category
        int subjectNum = subjectCategoryMapper.countSubjectNum(category.getId(), category.getLevel());
        //4.1. Null Check for Number of Questions in Category
        if (subjectNum > 0) { //Course Contains Questions
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_DELETE_HAVE_SUBJECT);
        }
        //5. Delete Course
        int result = this.baseMapper.deleteById(id);
        if (result <= 0) {
            throw new DbException(CourseErrorInfo.Msg.CATEGORY_DELETE_FAILD);
        }
    }

    /**
     * Function Point:
     * 1. Enable or Disable Course, and Enable or Disable Courses in the Next Level or Next Level,
     * Linked Enable or Disable
     *
     * @param categoryDisableOrEnableDTO
     */
    @Override
    @Transactional(rollbackFor = {DbException.class, Exception.class})
    public void disableOrEnable(CategoryDisableOrEnableDTO categoryDisableOrEnableDTO) {

        //1. Get Disabled/Enabled Course Category
        Category category = baseMapper.selectById(categoryDisableOrEnableDTO.getId());
        if (category == null) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATEGORY_NOT_FOUND);
        }
        //2. Validation
        if (category.getParentId() != 0) { //Validate Parent Category
            //2.1 Enable Validation
            if (categoryDisableOrEnableDTO.getStatus() == CommonStatus.ENABLE.getValue()) {
                //2.2 Get Parent Category
                Category parentCategory = baseMapper.selectById(category.getParentId());
                if (parentCategory == null) {
                    log.error("Operation Exception, Course Category Not Found by Parent ID, parentId: {}", category.getParentId());
                    throw new BizIllegalException(ErrorInfo.Msg.OPERATE_FAILED);
                }
                //2.3 Parent Category Disabled Cannot Enable Current Category
                if (CommonStatus.DISABLE.getValue() == parentCategory.getStatus()) {
                    throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_ENABLE_CANNOT);
                }
            }
        }

        //3. Get Categories Linked to Enable/Disable
        List<Long> childCategoryIds = new ArrayList<>();
        LambdaQueryWrapper<Category> directQueryWrapper = new LambdaQueryWrapper<>();
        directQueryWrapper.eq(Category::getParentId, categoryDisableOrEnableDTO.getId());
        //3.1 Get Sub-Category List of Enabled Category
        List<Category> categories = baseMapper.selectList(directQueryWrapper);
        if (CollUtils.isNotEmpty(categories)) { //Direct Sub-Category
            //3.2 Write Sub-Category ID to childCategoryIds
            childCategoryIds.addAll(categories.stream().map(Category::getId).collect(Collectors.toList()));
        }
        //3.3 Get Sub-Category List of Sub-Category of Enabled Category
        if (CollUtils.isNotEmpty(childCategoryIds)) {
            LambdaQueryWrapper<Category> inDirectQueryWrapper = new LambdaQueryWrapper<>();
            inDirectQueryWrapper.in(Category::getParentId, childCategoryIds);
            List<Category> inDirectCategorys = baseMapper.selectList(inDirectQueryWrapper);
            if (CollUtils.isNotEmpty(inDirectCategorys)) {
                //3.4 Write Sub-Category ID List to childCategoryIds
                childCategoryIds.addAll(inDirectCategorys.stream()
                        .map(Category::getId).collect(Collectors.toList()));
            }
        }

        //4. Update Current Category, Enable/Disable
        int result = this.baseMapper.updateById(BeanUtils.toBean(categoryDisableOrEnableDTO, Category.class));
        if (result <= 0) {
            throw new BizIllegalException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
        //5. Enable or Disable Associated Course Category
        if (CollUtils.isNotEmpty(childCategoryIds)) {
            //5.1 Update Conditions
            LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.in(Category::getId, childCategoryIds);
            Category updateCategory = new Category();
            updateCategory.setStatus(categoryDisableOrEnableDTO.getStatus());
            //5.2 Update Associated Category Status
            baseMapper.update(updateCategory, updateWrapper);
        }
        //6. Course Category Disable Triggers Course Batch Deletion
        if (categoryDisableOrEnableDTO.getStatus() == CommonStatus.DISABLE.getValue()) {
            Long userId = UserContext.getUser();
            taskExecutor.execute(() -> {
                batchDownShelfCourse(category.getId(), category.getLevel(), userId);
            });

        }
    }

    @Override
    @Transactional(rollbackFor = {DbException.class, Exception.class})
    public void update(CategoryUpdateDTO categoryUpdateDTO) {
        //1. Query Update Data
        Category category = this.baseMapper.selectById(categoryUpdateDTO.getId());
        if (category == null) {
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_NOT_FOUND);
        }
        //2. Validate Name Can Be Updated
        checkSameName(category.getParentId(), categoryUpdateDTO.getName(), categoryUpdateDTO.getId());
        //3. Set Update Fields
        Category updateCategory = new Category();
        updateCategory.setId(categoryUpdateDTO.getId()); //Modify Course Category ID
        updateCategory.setPriority(categoryUpdateDTO.getIndex());
        updateCategory.setName(categoryUpdateDTO.getName());
        //4. Update
        int result = this.baseMapper.updateById(updateCategory);
        if (result <= 0) {
            throw new BizIllegalException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    public List<SimpleCategoryVO> all(Boolean admin) {
        // 1. Query Course Category ID with Courses
        List<Long> categoryIdList = admin ?
                null :courseService.getCategoryIdListWithCourse();
        // 1.1. Null Check
        if(!admin && CollUtils.isEmpty(categoryIdList)){
            return new ArrayList<>();
        }

        // 2. Ascending Query All Non-Disabled Course Categories
        LambdaQueryWrapper<Category> queryWrapper = Wrappers.lambdaQuery(Category.class)
                .eq(!admin, Category::getStatus, CommonStatus.ENABLE.getValue())
                .in(CollectionUtil.isNotEmpty(categoryIdList), Category::getId, categoryIdList)
                .orderByAsc(Category::getPriority)
                .orderByDesc(Category::getId);
        List<Category> categories = this.baseMapper.selectList(queryWrapper);

        // 3. Convert Course Category to Tree Structure
        List<SimpleCategoryVO> simpleCategoryVOS = TreeDataUtils.parseToTree(categories,
                SimpleCategoryVO.class, new CategoryDataWrapper());
        // 4. Filter Out Course Categories Without Sub-Category
        filter(simpleCategoryVOS);
        return simpleCategoryVOS;

    }

    @Override
    public Map<Long, String> getCateIdAndName() {
        List<Category> categories = this.baseMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    @Override
    public List<CategoryVO> allOfOneLevel() {
        //1. Query Data
        List<Category> list = super.list();
        if (CollUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        //2. Statistics on Number of Third-Level Categories for Each First and Second-Level Directory, Create a Redis Cache for Three Minutes
        Map<Long, Long> thirdCategoryNumMap = this.statisticThirdCategory();
        return BeanUtils.copyList(list, CategoryVO.class, (category, categoryVO) -> {
            categoryVO.setThirdCategoryNum(thirdCategoryNumMap.getOrDefault(category.getId(), 0L).intValue());
        });
    }

    @Override
    public List<Category> queryByIds(List<Long> ids) {
        if (CollUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Category> queryWrapper =
                Wrappers.lambdaQuery(Category.class)
                        .in(Category::getId, ids);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Map<Long, String> queryByThirdCateIds(List<Long> thirdCateIdList) {
        Map<Long, String> resultMap = new HashMap<>();
        //1. Validation
        // 1.1 Check if Parameters Are Empty
        if (CollUtils.isEmpty(thirdCateIdList)) {
            return resultMap;
        }
        // 1.2 Validate All Category IDs Are Third-Level Category IDs
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getLevel, 3)
                .in(Category::getId, thirdCateIdList);
        int thirdCateNum = baseMapper.selectCount(queryWrapper);
        if (!NumberUtils.equals(thirdCateNum, thirdCateIdList.size())) {
            throw new BizIllegalException(ErrorInfo.Msg.REQUEST_PARAM_ILLEGAL);
        }
        //2. Query All Categories and Convert to Map
        List<Category> categories = baseMapper.selectList(null);
        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, p -> p));
        //3. Traverse Third-Level Category IDs
        for (Long thirdCateId : thirdCateIdList) {
            //3.1 Third-Level Category
            Category thirdCategory = categoryMap.get(thirdCateId);

            //3.2 Second-Level Category
            Category secondCategory = categoryMap.get(thirdCategory.getParentId());
            //3.3 First-Level Category
            Category firstCategory = categoryMap.get(thirdCateId);
            resultMap.put(thirdCateId, StringUtils.format("{}/{}/{}",
                    firstCategory.getName(), secondCategory.getName(), thirdCategory.getName()));
        }
        return resultMap;
    }

    @Override
    public List<String> queryCourseCategorys(Course course) {
        //1. Query Course Category
        List<Category> categories = baseMapper.selectBatchIds(
                Arrays.asList(course.getFirstCateId(),
                        course.getSecondCateId(),
                        course.getThirdCateId()));
        if (CollUtils.isNotEmpty(categories)) {
            return new ArrayList<>();
        }
        Map<Long, String> categoryIdAndNameMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        //2. Assemble into List Based on Category Hierarchy
        return Arrays.asList(categoryIdAndNameMap.get(course.getFirstCateId()),
                categoryIdAndNameMap.get(course.getSecondCateId()),
                categoryIdAndNameMap.get(course.getThirdCateId()));
    }

    @Override
    public List<Long> checkCategory(Long thirdCateId) {
        //1. Query Third-Level Course Category
        Category thirdCategory = baseMapper.selectById(thirdCateId);
        //1.1 Check Third-Level Course Category Status
        if (thirdCategory.getStatus() != CommonStatus.ENABLE.getValue()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATEGORY_NOT_FOUND);
        }
        //2. Query Second-Level Course Category
        Category secondCategory = baseMapper.selectById(thirdCategory.getParentId());
        //2.1 Check Third-Level Course Category Status
        if (secondCategory.getStatus() != CommonStatus.ENABLE.getValue()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATEGORY_NOT_FOUND);
        }
        //3. Return Data
        return Arrays.asList(secondCategory.getParentId(), secondCategory.getId(), thirdCateId);
    }

    /**
     * Get List of Category IDs Without Sub-Category for First and Second-Level Categories
     * @return
     */
    private List<Long> getCateIdsWithoutChildCateId(){
        // 1. Query Data
        List<Category> list = list();
        // 1.1. Null Check
        if(CollUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        // 2. List to Map
        Map<Long, List<Category>> idAndParentIdMap = list.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        // 3. Traverse
        return list.stream()
                .filter(category -> category.getLevel() < 3 && !idAndParentIdMap.containsKey(category.getId()))
                .map(Category::getId)
                .collect(Collectors.toList());
    }


    /**
     * Validation for Duplicate Category Name When Adding or Updating
     *
     * @param parentId Parent ID
     * @param name
     * @param currentId
     */
    private void checkSameName(Long parentId, String name, Long currentId) {
        //1. Statistics on Same-Named Categories or Categories with Same Name as Parent Category in Sub-Category List of Same Parent Category
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.or().eq(true, Category::getParentId, parentId)
                .eq(Category::getName, name)
                .eq(Category::getDeleted, Constant.DATA_NOT_DELETE);
        queryWrapper.or().eq(Category::getId, parentId)
                .eq(Category::getName, name);
        //2. Statistics on List of Categories Meeting the Above Conditions
        List<Category> categories = this.baseMapper.selectList(queryWrapper);
        //3. Duplicate Category Exists in Addition
        if (currentId == null && CollectionUtil.isNotEmpty(categories)) {
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_SAME_NAME);
        }
        //4. In Update, if Same Name Exists, Check if It Is the Current Category Name
        if (CollectionUtil.isNotEmpty(categories) && categories.get(0).getId() != currentId.longValue()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_SAME_NAME);
        }
    }

    private void setThirdCategoryNum(List<CategoryVO> categoryVOList){
        // 1. Null Check
        if(CollUtils.isEmpty(categoryVOList)){
            return;
        }
        // 2.
    }

    /**
     * Statistics on Number of Third-Level Categories for Each First and Second-Level Directory
     *
     * @return
     */
    private Map<Long, Long> statisticThirdCategory() {
        Map<Long, Long> result = new HashMap<>();
        // 1. Query All Data
        List<Category> categories = baseMapper.selectList(null);
        // 1.1. Null Check
        if(CollUtils.isEmpty(categories)){
            return result;
        }

        // 2. Number of tertiary course categories under secondary classification
        Map<Long, Long> collect = categories.stream()
                .filter(category -> category.getLevel() == 3)
                .collect(Collectors.groupingBy(Category::getParentId, Collectors.counting()));
        result.putAll(collect);
        // 3. Number of tertiary course categories under primary classification
        Map<Long, List<Category>> category2Map = categories.stream()
                .filter(category -> category.getLevel() == 2)
                .collect(Collectors.groupingBy(Category::getParentId));
        // 3.1. Traverse category2Map
        for (Map.Entry<Long, List<Category>> entry : category2Map.entrySet()){
            long sum = entry.getValue()
                    .stream()
                    .map(category -> NumberUtils.null2Zero(result.get(category.getId())))
                    .collect(Collectors.summarizingLong(num -> num))
                    .getSum();
            result.put(entry.getKey(), sum);
        }
        // 4. Return Result
        return result;
    }

    /**
     * Filter course categories based on conditions
     *
     * @param categoryVOList
     * @param categoryListDTO
     * @return
     */
    private List<CategoryVO> fiter(List<CategoryVO> categoryVOList, CategoryListDTO categoryListDTO) {
        if (CollUtils.isEmpty(categoryVOList)) {
            return new ArrayList<>();
        }
        return categoryVOList.stream().
                filter(categoryVO -> filter(categoryVO, categoryListDTO))
                .collect(Collectors.toList());
    }

    /**
     * Recursively filter the queried data, current category meets condition = current category info meets condition OR has a child category that meets condition
     * 1. Validate information status and name conform to dto requirements
     * 2. Loop through subcategories, remove subcategories that do not meet conditions from the subcategory list
     * 3. Steps 1 passed + whether there are still subcategories (steps 2 removed non-compliant ones)
     *
     * @param categoryVO
     * @param categoryListDTO
     * @return Whether current category meets conditions
     */
    private boolean filter(CategoryVO categoryVO, CategoryListDTO categoryListDTO) {

        //Current category passes, or if any child category passes, then all pass
        //No filtering required
        if (StringUtils.isEmpty(categoryListDTO.getName()) && categoryListDTO.getStatus() == null) {
            return true;
        }
        boolean pass = true;
        // Status validation
        if (categoryListDTO.getStatus() != null) { //Pass if status matches query
            pass = (categoryVO.getStatus() == categoryListDTO.getStatus());
        }
        //Name validation
        if (pass && StringUtils.isNotEmpty(categoryListDTO.getName())) {//Validate name after status passes, name contains keyword passes
            pass = StringUtils.isNotEmpty(categoryVO.getName()) && categoryVO.getName().contains(categoryListDTO.getName());
        }
        //Classification info validation failed and no subcategories, current category does not meet conditions
        if (!pass && CollUtils.isEmpty(categoryVO.getChildren())) { //Inform the parent level that it did not pass
            return false;
        }
        //Traverse subcategories to check if they meet conditions
        for (int count = categoryVO.getChildren().size() - 1; count >= 0; count--) {
            CategoryVO child = categoryVO.getChildren().get(count);
            //Subcategory validation
            boolean childPass = filter(child, categoryListDTO);
            if (!childPass) { //Subcategory does not meet conditions, remove from subcategory list
                categoryVO.getChildren().remove(count);
            }
        }
        return pass || CollUtils.isNotEmpty(categoryVO.getChildren());
    }

    private void batchDownShelfCourse(Long categoryId, Integer level, Long userId) {
        //1. Set operation user id in multi-threaded environment
        UserContext.setUser(userId);
        //2. Query courses to be taken down
        List<Course> courses = courseService.queryByCategoryIdAndLevel(categoryId, level);
        if (CollUtils.isEmpty(courses)) {
            return;
        }
        //3. Traverse taken-down courses
        for (Course course : courses) {
            //4. Determine if status allows taking down
            if (!CourseStatus.SHELF.equals(course.getStatus())) {
                continue;
            }
            try {
                //5. Take down course
                courseDraftService.downShelf(course.getId());
            } catch (Exception e) {
                log.error("Course take down exception");
            }
        }
    }

    /**
     * Filter out categories without tertiary course categories
     * @param simpleCategoryVOS
     */
    private void filter(List<SimpleCategoryVO> simpleCategoryVOS){
        // 1. Null Check
        if(CollUtils.isEmpty(simpleCategoryVOS)){
            return;
        }
        // 2. Traverse category list
        for (int count = simpleCategoryVOS.size() -1; count >= 0; count--) {
            SimpleCategoryVO simpleCategoryVO = simpleCategoryVOS.get(count);
            if(simpleCategoryVO.getLevel() == 3){
                continue;
            }
            filter(simpleCategoryVO.getChildren());
            if(CollUtils.isEmpty(simpleCategoryVO.getChildren())){
                simpleCategoryVOS.remove(count);
            }
        }
    }
}
