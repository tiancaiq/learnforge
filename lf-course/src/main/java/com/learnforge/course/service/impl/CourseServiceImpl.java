package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.exam.ExamClient;
import com.learnforge.api.client.learning.LearningClient;
import com.learnforge.api.client.trade.TradeClient;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.api.dto.course.*;
import com.learnforge.api.dto.leanring.LearningLessonDTO;
import com.learnforge.api.dto.leanring.LearningRecordDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.*;
import com.learnforge.course.constants.CourseErrorInfo;
import com.learnforge.course.constants.CourseStatus;
import com.learnforge.course.constants.RedisContants;
import com.learnforge.course.domain.dto.CoursePageQuery;
import com.learnforge.course.domain.dto.CourseSimpleInfoListDTO;
import com.learnforge.course.domain.po.Category;
import com.learnforge.course.domain.po.Category3PO;
import com.learnforge.course.domain.po.Course;
import com.learnforge.course.domain.po.CourseTeacher;
import com.learnforge.course.domain.vo.*;
import com.learnforge.course.mapper.CourseDraftMapper;
import com.learnforge.course.mapper.CourseMapper;
import com.learnforge.course.mapper.CourseTeacherMapper;
import com.learnforge.course.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Draft Course Service Implementation Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private CourseDraftMapper courseDraftMapper;

    @Autowired
    private ICourseDraftService courseDraftService;

    @Autowired
    private RabbitMqHelper rabbitMqHelper;

    @Autowired
    private ICourseCatalogueService courseCatalogueService;

    @Autowired
    private ICourseTeacherService courseTeacherService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TradeClient tradeClient;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private LearningClient learningClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void updateStatus(Long id, Integer status) {
        //1. Assemble data
        Course course = new Course();
        course.setId(id);
        course.setStatus(status);
        course.setUpdateTime(LocalDateTime.now());
        //2. Update data
        int result = baseMapper.updateById(course);
        //3. Judge if the direct count is 1
        if (result != 1) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    public CourseDTO getCourseDTOById(Long id) {
        // 1. Query Course Information
        Course course = baseMapper.selectById(id);
        //1.1 Check for null
        if (course == null) {
            return null;
        }
        //2. Teacher query conditions
        LambdaQueryWrapper<CourseTeacher> queryWrapper =
                Wrappers.lambdaQuery(CourseTeacher.class)
                        .eq(CourseTeacher::getCourseId, id)
                        .orderBy(true, false, CourseTeacher::getCIndex)
                        .last(true, "limit 1");
        // 2. Query teacher
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);

        // 3. Course data packaging
        CourseDTO courseDTO = BeanUtils.toBean(course, CourseDTO.class);
        //3.1. First-level course category
        courseDTO.setCategoryIdLv1(course.getFirstCateId());
        //3.2. Second-level course category
        courseDTO.setCategoryIdLv2(course.getSecondCateId());
        //3.3. Third-level course category
        courseDTO.setCategoryIdLv3(course.getThirdCateId());
        //3.4. Media resource information
        courseDTO.setDuration(course.getMediaDuration());
        //3.5. Course publish time
        courseDTO.setPublishTime(course.getCreateTime());
        //3.6. Course segment count
        courseDTO.setSections(course.getSectionNum());
        //3.7. First teacher of the course
        if (CollUtils.isNotEmpty(courseTeachers)) {
            courseDTO.setTeacher(courseTeachers.get(0).getTeacherId());
        } else {
            courseDTO.setTeacher(0L);
        }

        // 4. Statistics course sales
        Map<Long, Integer> peoNumOfCourseMap = tradeClient.countEnrollNumOfCourse(CollUtils.singletonList(id));
        if (CollUtils.isNotEmpty(peoNumOfCourseMap)) {
            courseDTO.setSold(peoNumOfCourseMap.getOrDefault(id, 0));
        }
        //5. Return data
        return courseDTO;

    }

    @Override
    public void delete(Long id) {
        //1. Delete draft information
        courseDraftService.delete(id);
        //2. Send delete draft mq
        rabbitMqHelper.send(MqConstants.Exchange.COURSE_EXCHANGE, MqConstants.Key.COURSE_DELETE_KEY, id);
    }

    @Override
    public List<CourseSimpleInfoDTO> getSimpleInfoList(CourseSimpleInfoListDTO courseSimpleInfoListDTO) {
        //1. Course query conditions
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                        .in(CollUtils.isNotEmpty(courseSimpleInfoListDTO.getThirdCataIds()),
                                Course::getThirdCateId, courseSimpleInfoListDTO.getThirdCataIds())
                        .in(CollUtils.isNotEmpty(courseSimpleInfoListDTO.getIds()),
                                Course::getId, courseSimpleInfoListDTO.getIds());
        //2. Query course
        List<Course> courses = baseMapper.selectList(queryWrapper);
        //3. Course information conversion
        return BeanUtils.copyList(courses, CourseSimpleInfoDTO.class);
    }

    @Override
    public List<Course> queryByCategoryIdAndLevel(Long categoryId, Integer level) {
        //1. Course basic information query conditions
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                        .eq(level == 1, Course::getFirstCateId, categoryId) //Primary Course Category
                        .eq(level == 2, Course::getSecondCateId, categoryId) //Secondary Course Category
                        .eq(level == 3, Course::getThirdCateId, categoryId);//Third-level course category
        //2. Query course basic information
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public NameExistVO checkName(String name, Long id) {
        //1. Formal course same name course count
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                        .eq(Course::getName, name)
                        .last(id != null, " and id !=" + id);
        //2. Statistics count
        Integer num = baseMapper.selectCount(queryWrapper);
        if (num > 0) {
            return NameExistVO.EXISTED;
        }
        //3. Statistics draft course same name count
        return courseDraftService.checkName(name, id);
    }

    @Override
    public List<Long> queryExists(List<Long> idList, List<Integer> statusList) {
        //1. Get course ID with specified status query condition
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                        .in(Course::getId, idList)
                        .in(Course::getStatus, statusList);
        //2. Query course by condition
        List<Course> courses = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courses)) {
            return null;
        }
        //3. Assemble data
        return courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> queryCourseIdByName(String name) {
        // 1. Query conditions
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                .like(Course::getName, name);
        // 2. Query data
        List<Course> courses = baseMapper.selectList(queryWrapper);
        // 3. Convert to course ID list
        return courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
    }


    @Override
    public CourseAndSectionVO queryCourseAndCatalogById(Long courseId) {
        // 1. Get current user
        Long userId = UserContext.getUser();
        // 2. Query course details
        CourseFullInfoDTO course = getInfoById(courseId, true, true);
        if (course == null) {
            return null;
        }
        // 3. Organize VO
        CourseAndSectionVO vo = new CourseAndSectionVO();
        vo.setId(courseId);
        vo.setName(course.getName());
        vo.setSections(course.getSectionNum());
        vo.setCoverUrl(course.getCoverUrl());
        // 4. Query teacher information
        List<UserDTO> teachers = userClient.queryUserByIds(course.getTeacherIds());
        if (CollUtils.isNotEmpty(teachers)) {
            UserDTO teacher = teachers.get(0);
            vo.setTeacherName(teacher.getName());
            vo.setTeacherIcon(teacher.getIcon());
        }
        // 5. Assemble Section Information
        List<CatalogueDTO> catas = course.getChapters();
        List<ChapterVO> chapters = new ArrayList<>(catas.size());
        for (CatalogueDTO c : catas) {
            ChapterVO cv = new ChapterVO();
            cv.setId(c.getId());
            cv.setName(c.getName());
            cv.setIndex(c.getIndex());
            cv.setMediaDuration(c.getMediaDuration());
            List<SectionVO> sections = BeanUtils.copyToList(c.getSections(), SectionVO.class);
            cv.setSections(sections);
            chapters.add(cv);
        }
        vo.setChapters(chapters);
        // 6. Query Learning Progress
        if (learningClient == null) {
            return vo;
        }
        // 6.1. Query Learning Records
        LearningLessonDTO lessonDTO = learningClient.queryLearningRecordByCourse(courseId);
        if (lessonDTO == null) {
            // No course schedule information found, indicating a free trial, return directly
            return vo;
        }
        vo.setLessonId(lessonDTO.getId());
        if (CollUtils.isEmpty(lessonDTO.getRecords())) {
            // Course schedule information exists, but no learning records, no need to handle progress, return directly
            return vo;
        }
        List<LearningRecordDTO> records = lessonDTO.getRecords();
        // 6.2. Retrieve the most recent learning record. Since the query is sorted by learning time, the first record is the most recent section record
        Long latestSectionId = lessonDTO.getLatestSectionId();
        if(latestSectionId == null) {
            latestSectionId = records.get(0).getSectionId();
        }
        vo.setLatestSectionId(latestSectionId);
        // 6.3. Process the record into a map
        Map<Long, LearningRecordDTO> rMap = records.stream()
                .collect(Collectors.toMap(LearningRecordDTO::getSectionId, r -> r));
        // 6.4. Fill learning progress into the chapter
        for (ChapterVO chapter : vo.getChapters()) {
            for (SectionVO section : chapter.getSections()) {
                LearningRecordDTO r = rMap.get(section.getId());
                if (r == null) continue;
                section.setFinished(r.getFinished());
                section.setMoment(r.getMoment());
            }
        }
        return vo;
    }

    @Override
    public List<SubNumAndCourseNumDTO> countSubjectNumAndCourseNumOfTeacher(List<Long> teacherIds) {
        // 1. Statistics
        // 1.1. Teacher ID and Course Count (Published, Unpublished, Expired)
        Map<Long, Integer> teacherIdAndCourseNumMap =
                IdAndNumDTO.toMap(baseMapper.countCourseNumOfTeacher(teacherIds));
        // 1.2. Pending Publication
        Map<Long, Integer> teacherIdAndCourseNumMap2 =
                IdAndNumDTO.toMap(courseDraftMapper.countCourseNumOfTeacher(teacherIds));
        // 1.3. Statistics on the number of questions a teacher has created
        Map<Long, Integer> teacherIdAndSubjectNumMap = examClient.countSubjectNumOfTeacher(teacherIds);

        // 2. Traverse Teacher IDs
        List<SubNumAndCourseNumDTO> subNumAndCourseNumDTOS = new ArrayList<>();
        for (Long teacherId : teacherIds) {
            subNumAndCourseNumDTOS.add(new SubNumAndCourseNumDTO(
                    //2.1. Set Teacher ID
                    teacherId,
                    //2.2. Set Teacher Course Count
                    NumberUtils.null2Zero(teacherIdAndCourseNumMap.get(teacherId)) +
                            NumberUtils.null2Zero(teacherIdAndCourseNumMap2.get(teacherId)),
                    //2.3. Set Teacher Question Count
                    NumberUtils.null2Zero(teacherIdAndSubjectNumMap.get(teacherId))));
        }
        return subNumAndCourseNumDTOS;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int courseFinished() {
        //1. Completion Course Query Conditions
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                        .le(Course::getPurchaseEndTime, LocalDateTime.now())
                        .in(Course::getStatus,
                                List.of(CourseStatus.DOWN_SHELF.getStatus(),
                                        CourseStatus.SHELF.getStatus()));

        //1.2. Query Completed Courses
        List<Course> courses = baseMapper.selectList(queryWrapper);
        //1.3. Check for Completed Courses
        if (CollUtils.isEmpty(courses)) {
            return 0;
        }
        //2. Assemble data
        List<Course> updateCourses = new ArrayList<>();
        for (Course course : courses) {
            Course updateCourse = new Course();
            //2.1. Set Course ID
            updateCourse.setId(course.getId());
            //2.2. Set Course Status - Completed
            updateCourse.setStatus(CourseStatus.FINISHED.getStatus());
            updateCourses.add(updateCourse);
        }
        //3. Batch Complete Courses
        updateBatchById(updateCourses);
        //4. Send Course Completion MQ
        sendFinishedCourse(courses);
        //5. Clean Drafts
        for (Course course: courses){
            courseDraftService.delete(course.getId());
        }

        return updateCourses.size();
    }

    @Override
    @Cacheable(cacheNames = RedisContants.Formatter.STATISTICS_COURSE_NUM_CATE)
    public Map<Long, Integer> countCourseNumOfCategory() {
        //1. Statistics on Course Categories with Published and Completed Courses
        Map<Long, Integer> nomalCourseNumOfCategory =
                countNomalCourseNumOfCategory();
        //2. Statistics on Course Categories with Pending and Unpublished Courses
        Map<Long, Integer> draftCourseNumOfCategory =
                courseDraftService.countCourseNumOfCategory();
        //3. Aggregate the Two Sets of Data
        return CollUtils.union(nomalCourseNumOfCategory, draftCourseNumOfCategory);
    }

    @Override
    @Cacheable(cacheNames = RedisContants.Formatter.CATEGORY_ID_LIST_HAVE_COURSE)
    public List<Long> getCategoryIdListWithCourse() {
        // 1. Query conditions
        List<Category3PO> category3s = baseMapper.queryCategoryIdWithCourse();
        // 1.1. Null Check
        if(CollUtils.isEmpty(category3s)) {
            return new ArrayList<>();
        }
        // 2. Set Course Category ID to categoryIdList
        List<Long> categoryIdList = new ArrayList<>();
        category3s.stream().forEach(category3->{
            category3.setId(categoryIdList);
        });
        // 2.1. Deduplicate and Return Data
        return categoryIdList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Integer countCourseNumOfCategory(Long categoryId) {
        //1. Course Statistics Conditions
        LambdaQueryWrapper<Course> queryWrapper =
                Wrappers.lambdaQuery(Course.class)
                        .or().eq(Course::getFirstCateId, categoryId)
                        .or().eq(Course::getSecondCateId, categoryId)
                        .or().eq(Course::getThirdCateId, categoryId);
        //2. Statistics on Course Count
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public CourseFullInfoDTO getInfoById(Long id, boolean withCatalogue, boolean withTeachers) {
        // 1. Query Course Basic Information
        Course course = baseMapper.selectById(id);
        if (course == null) {
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_CHECK_NOT_EXISTS);
        }
        // 2. Convert to VO
        CourseFullInfoDTO courseFullInfoDTO = BeanUtils.toBean(course, CourseFullInfoDTO.class);

        // 3. Query Directory Information
        if (withCatalogue) {
            courseFullInfoDTO.setChapters(courseCatalogueService.queryCourseCatalogues(id, true));
        }
        // 4. Query teacher information
        if (withTeachers) {
            courseFullInfoDTO.setTeacherIds(courseTeacherService.getTeacherIdOfCourse(id));
        }
        return courseFullInfoDTO;
    }

    @Override
    public PageDTO<CoursePageVO> queryForPage(CoursePageQuery coursePageQuery) {
        //1. Course query conditions
        LambdaQueryWrapper<Course> queryWrapper =
                SqlWrapperUtils.toLambdaQueryWrapper(coursePageQuery, Course.class);
        //1.1. Course Conditions - Update Time
        queryWrapper.between(
                ObjectUtils.isNotEmpty(coursePageQuery.getBeginTime())
                        && ObjectUtils.isNotEmpty(coursePageQuery.getEndTime()),
                Course::getUpdateTime,
                coursePageQuery.getBeginTime(),
                coursePageQuery.getEndTime());
        //1.2. Course Query Conditions - Name
        queryWrapper.like(
                StringUtils.isNotEmpty(coursePageQuery.getKeyword()),
                Course::getName,
                coursePageQuery.getKeyword());
        //1.3. Page Query Data
        Page<Course> page = page(coursePageQuery.toMpPage(), queryWrapper);
        //1.4. Check Page Data for Null
        if (CollUtils.isEmpty(page.getRecords())) {
            return PageDTO.empty(page);
        }
        //2. Course Updater ID List
        List<Long> updaterList = page
                .getRecords()
                .stream()
                .map(Course::getUpdater)
                .collect(Collectors.toList());
        //2.1. Query User Information for Updater
        List<UserDTO> userDTOS = userClient.queryUserByIds(updaterList);
        //2.2. Convert Course Updater ID + Name Map
        Map<Long, String> updaterMap =
                CollUtils.isEmpty(updaterList) ?
                        new HashMap<>()
                        : userDTOS
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        //3. Retrieve All Course Category Information
        List<Category> list = categoryService.list();
        //3.1. Convert Course Category ID + Name Map
        Map<Long, String> categoryNameMap =
                CollUtils.isEmpty(list) ?
                        new HashMap<>()
                        : list.stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));
        //4. Course ID List
        List<Long> courseIdList = page
                .getRecords()
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        //4.1. Statistics on Course Enrollment Count Map
        Map<Long, Integer> peoNumOfCourseMap = tradeClient.countEnrollNumOfCourse(courseIdList);
        //5. Assemble Data
        return PageDTO.of(page, CoursePageVO.class, (course, coursePageVO) -> {
            //5.1. Concatenate Course Category Name
            String categories = StringUtils.format("{}/{}/{}",
                    categoryNameMap.get(course.getFirstCateId()),
                    categoryNameMap.get(course.getSecondCateId()),
                    categoryNameMap.get(course.getThirdCateId()));
            //5.2. Set Course Category Name
            coursePageVO.setCategories(categories);
            //5.3. Set Course Updater Name
            coursePageVO.setUpdaterName(updaterMap.get(course.getUpdater()));
            //5.4. Set Course Enrollment Count
            coursePageVO.setSold(NumberUtils.null2Zero(peoNumOfCourseMap.get(course.getId())));
            //5.5. Set Course Section Count
            coursePageVO.setSections(course.getSectionNum());
        });
    }

    /**
     * Async Send Course Completion MQ
     *
     * @param finishedCourse
     */
    private void sendFinishedCourse(List<Course> finishedCourse) {
        //1. Traverse and Send Course Completion MQ
        for (Course course : finishedCourse) {
            rabbitMqHelper.sendAsync(MqConstants.Exchange.COURSE_EXCHANGE,
                    MqConstants.Key.COURSE_EXPIRE_KEY,
                    course.getId());
        }
    }

    /**
     * Statistics on Course Category Published and Completed Course Count
     *
     * @return
     */
    private Map<Long, Integer> countNomalCourseNumOfCategory() {
        //1. Query conditions
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Course::getStatus,
                Arrays.asList(CourseStatus.SHELF.getStatus(), CourseStatus.FINISHED.getStatus()));
        //2. Query data
        List<Course> courses = baseMapper.selectList(queryWrapper);
        Map<Long, Integer> cateIdAndNumMap = new HashMap<>();
        //3. Statistics on Course Count per Category
        for (Course course : courses) {
            //3.1. Primary Category Count
            Integer firstCateNum = cateIdAndNumMap.get(course.getFirstCateId());
            cateIdAndNumMap.put(course.getFirstCateId(), firstCateNum == null ? 1 : firstCateNum + 1);
            //3.2. Secondary Category Count
            Integer secondCateNum = cateIdAndNumMap.get(course.getSecondCateId());
            cateIdAndNumMap.put(course.getSecondCateId(), secondCateNum == null ? 1 : secondCateNum + 1);
            //3.3. Tertiary Category Count
            Integer thirdCateNum = cateIdAndNumMap.get(course.getThirdCateId());
            cateIdAndNumMap.put(course.getThirdCateId(), thirdCateNum == null ? 1 : thirdCateNum + 1);
        }
        return cateIdAndNumMap;
    }
}
