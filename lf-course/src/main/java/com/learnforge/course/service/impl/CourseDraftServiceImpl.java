package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.exam.ExamClient;
import com.learnforge.api.client.learning.LearningClient;
import com.learnforge.api.client.trade.TradeClient;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.course.CourseDTO;
import com.learnforge.api.dto.course.CoursePurchaseInfoDTO;
import com.learnforge.api.dto.exam.QuestionBizDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.*;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.constants.CourseErrorInfo;
import com.learnforge.course.constants.CourseStatus;
import com.learnforge.course.domain.dto.CourseBaseInfoSaveDTO;
import com.learnforge.course.domain.dto.CoursePageQuery;
import com.learnforge.course.domain.po.*;
import com.learnforge.course.domain.vo.CourseBaseInfoVO;
import com.learnforge.course.domain.vo.CoursePageVO;
import com.learnforge.course.domain.vo.CourseSaveVO;
import com.learnforge.course.domain.vo.NameExistVO;
import com.learnforge.course.mapper.*;
import com.learnforge.course.service.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Draft Course Service Implementation Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
@Service
public class CourseDraftServiceImpl extends ServiceImpl<CourseDraftMapper, CourseDraft> implements ICourseDraftService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private CourseContentDraftMapper courseContentDraftMapper;

    @Autowired
    private CourseContentMapper courseContentMapper;

    @Autowired
    private ValidatorFactory validatorFactory;

    @Autowired
    private ICourseCatalogueDraftService courseCatalogueDraftService;

    @Autowired
    private ICourseTeacherDraftService courseTeacherDraftService;

    @Autowired
    private CourseCatalogueDraftMapper courseCatalogueDraftMapper;

    @Autowired
    private CourseTeacherDraftMapper courseTeacherDraftMapper;

    @Autowired
    private CourseCataSubjectDraftMapper courseCataSubjectDraftMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RabbitMqHelper rabbitMqHelper;

    @Autowired
    private TradeClient tradeClient;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private LearningClient learningClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public CourseSaveVO save(CourseBaseInfoSaveDTO courseBaseInfoSaveDTO) {
        List<Long> categoryIdList = null;
        Course course = null;
        //1. Data validation
        if (courseBaseInfoSaveDTO.getId() == null) {
            //1.1 New Data Call Data Validator
            ViolationUtils.process(validatorFactory.getValidator().validate(courseBaseInfoSaveDTO));
            //1.1.2 Validate Course Category
            categoryIdList = categoryService.checkCategory(courseBaseInfoSaveDTO.getThirdCateId());
        } else {
            //1.2 Unlisted Course Validation
            course = courseMapper.selectById(courseBaseInfoSaveDTO.getId());
            if (course == null) {
                //1.2.1 Unlisted Course Validation Request Parameters
                ViolationUtils.process(validatorFactory.getValidator().validate(courseBaseInfoSaveDTO));
                //1.2.2 Same Name Course Null Check
                checkSameCourse(courseBaseInfoSaveDTO.getId(), courseBaseInfoSaveDTO.getName());
                //1.2.3 Validate Course Category
                categoryIdList = categoryService.checkCategory(courseBaseInfoSaveDTO.getThirdCateId());
            }
        }

        CourseDraft courseDraft = new CourseDraft();
        //2. Data Packaging
        //2.1.content Data Packaging Course Introduction, Course Details, Target Audience
        CourseContentDraft courseContentDraft = new CourseContentDraft();
        courseContentDraft.setCourseIntroduce(courseBaseInfoSaveDTO.getIntroduce());
        courseContentDraft.setCourseDetail(courseBaseInfoSaveDTO.getDetail());
        courseContentDraft.setUsePeople(courseBaseInfoSaveDTO.getUsePeople());
        //2.2. Course Cover and Course Unlisting Time
        courseDraft.setCoverUrl(courseBaseInfoSaveDTO.getCoverUrl());
        courseDraft.setPurchaseEndTime(courseBaseInfoSaveDTO.getPurchaseEndTime());
        //2.3. Unlisted Data Packaging, Listed Courses Cannot Modify Fields
        if (course == null) {
            //2.3.1. Course Price
            courseDraft.setPrice(NumberUtils.null2Zero(courseBaseInfoSaveDTO.getPrice()));
            //2.3.2. Course Validity Period
            courseDraft.setValidDuration(courseBaseInfoSaveDTO.getValidDuration());
            //2.3.3. Course Status
            courseDraft.setStatus(CourseStatus.NO_UP_SHELF.getStatus());
            //2.3.4. Primary Course Category ID
            courseDraft.setFirstCateId(categoryIdList.get(0));
            //2.3.5. Secondary Course Category ID
            courseDraft.setSecondCateId(categoryIdList.get(1));
            //2.3.6. Tertiary Course Category ID
            courseDraft.setThirdCateId(categoryIdList.get(2));
            //2.3.7. Sales Mode
            courseDraft.setFree(courseBaseInfoSaveDTO.getFree() ? 1 : 0);
            //2.3.8. Course Name
            courseDraft.setName(courseBaseInfoSaveDTO.getName());
        }

        //3. Operations
        if (courseBaseInfoSaveDTO.getId() == null) {
            //3.1. Add Course Draft
            //3.1.1. New Generated Course ID
            Long id = IdWorker.getId();
            //3.1.2. Set Course ID
            courseContentDraft.setId(id);
            courseDraft.setId(id);
            //3.1.3. Set Course Edit Progress
            courseDraft.setStep(CourseConstants.CourseStep.BASE_INFO);
            //3.1.4. Insert Course Draft
            baseMapper.insert(courseDraft);
            //3.1.5. Insert Course Draft Content
            courseContentDraftMapper.insert(courseContentDraft);
        } else {
            //3.2. Edit Course Draft
            //3.2.1. Set Course ID
            courseContentDraft.setId(courseBaseInfoSaveDTO.getId());
            courseDraft.setId(courseBaseInfoSaveDTO.getId());
            //3.2.2. Update Course Draft
            baseMapper.updateById(courseDraft);
            //3.2.3. Update Course Draft Content
            courseContentDraftMapper.updateById(courseContentDraft);
        }
        //4. Return Course New DTO
        return CourseSaveVO
                .builder()
                .id(courseDraft.getId())
                .build();
    }

    @Override
    public CourseBaseInfoVO getCourseBaseInfo(Long id, Boolean see) {

        CourseBaseInfoVO courseBaseInfoVO = null;
        if (see) {
            //1. Query Course Information
            Course course = courseMapper.selectById(id);
            if (course != null) {
                //1.1. Query Course Corresponding Enrollment and Purchase Count and Refund Count
                CoursePurchaseInfoDTO coursePurchaseInfoDTO = tradeClient.getPurchaseInfoOfCourse(id);
                //1.2. Assemble Data
                courseBaseInfoVO = BeanUtils.toBean(course, CourseBaseInfoVO.class);
                //1.3. Query Course Content
                CourseContent courseContent = courseContentMapper.selectById(id);
                //1.4. Set Course Rating
                courseBaseInfoVO.setCoureScore(NumberUtils.div(NumberUtils.null2Zero(course.getScore()) * 1.0, 10, 2));
                //1.5. Set Enrollment Count
                courseBaseInfoVO.setEnrollNum(coursePurchaseInfoDTO.getEnrollNum());
                //1.6. Set Learning Count
                courseBaseInfoVO.setStudyNum(learningClient.countLearningLessonByCourse(id));
                //1.7. Set Refund Count
                courseBaseInfoVO.setRefundNum(coursePurchaseInfoDTO.getRefundNum());
                //1.8. Set Actual Paid Amount
                courseBaseInfoVO.setRealPayAmount(coursePurchaseInfoDTO.getRealPayAmount());
                //1.9. Set Course Details
                courseBaseInfoVO.setDetail(courseContent.getCourseDetail());
                //1.10. Set Course Introduction
                courseBaseInfoVO.setIntroduce(courseContent.getCourseIntroduce());
                //1.11. Set Course Target Audience
                courseBaseInfoVO.setUsePeople(courseContent.getUsePeople());
                //1.12. Set Total Section Count
                courseBaseInfoVO.setCataTotalNum(course.getSectionNum());
            }
        }
        //2. Query Draft Information
        if (courseBaseInfoVO == null) {
            //2.1. Query Draft Course Information
            CourseDraft courseDraft = baseMapper.selectById(id);
            //2.2. Has Draft Course Information
            if (courseDraft != null) {
                //2.3. Assemble Course Information
                courseBaseInfoVO = BeanUtils.toBean(courseDraft, CourseBaseInfoVO.class);
                //2.4. Query Course Content Information
                CourseContentDraft courseContentDraft = courseContentDraftMapper.selectById(id);
                //2.5. Set Course Details
                courseBaseInfoVO.setDetail(courseContentDraft.getCourseDetail());
                //2.6. Set Course Introduction
                courseBaseInfoVO.setIntroduce(courseContentDraft.getCourseIntroduce());
                //2.7. Target Audience
                courseBaseInfoVO.setUsePeople(courseContentDraft.getUsePeople());
                //2.8. Number of Course Chapters
                courseBaseInfoVO.setCataTotalNum(courseDraft.getSectionNum());
                //2.9. Set Course Rating
                courseBaseInfoVO.setCoureScore(0d);
                //2.10. Set Enrollment Count
                courseBaseInfoVO.setEnrollNum(0);
                //2.11. Set Learning Count
                courseBaseInfoVO.setStudyNum(0);
                //2.12. Set Refund Count
                courseBaseInfoVO.setRefundNum(0);
                //2.13. Set Actual Payment Amount
                courseBaseInfoVO.setRealPayAmount(0);
            }
        }
        if(courseBaseInfoVO == null){
            return new CourseBaseInfoVO();
        }

        //3. Query Creator and Updater Names
        List<UserDTO> userDTOS = userClient.queryUserByIds(
                Arrays.asList(courseBaseInfoVO.getCreater(), courseBaseInfoVO.getUpdater())
                        .stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
        if (CollUtils.isNotEmpty(userDTOS)) {
            //3.1. Creator and Updater to ID+Name Mapping
            Map<Long, String> operatorMap = userDTOS
                    .stream()
                    .collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
            //3.2. Set Creator Name
            courseBaseInfoVO.setCreaterName(operatorMap.get(courseBaseInfoVO.getCreater()));
            //3.3. Set Updater Name
            courseBaseInfoVO.setUpdaterName(operatorMap.get(courseBaseInfoVO.getUpdater()));
        }

        //4. Course Category Information
        List<Category> categories = categoryService.queryByIds(
                Arrays.asList(courseBaseInfoVO.getFirstCateId(),
                        courseBaseInfoVO.getSecondCateId(),
                        courseBaseInfoVO.getThirdCateId()));
        if (CollUtils.isNotEmpty(categories)) {
            //4.1. Category ID and Name Relationship
            Map<Long, String> categoryIdAndName = categories
                    .stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
            //4.2. Set Course Category Name
            courseBaseInfoVO.setCateNames(
                    StringUtils.format("{}/{}/{}",
                            categoryIdAndName.get(courseBaseInfoVO.getFirstCateId()),
                            categoryIdAndName.get(courseBaseInfoVO.getSecondCateId()),
                            categoryIdAndName.get(courseBaseInfoVO.getThirdCateId()))
            );
        }
        return courseBaseInfoVO;
    }

    @Override
    public void updateStep(Long id, Integer step) {
        //1. Query Course Draft
        CourseDraft courseDraft = baseMapper.selectById(id);
        CourseDraft updateCourseDraft = new CourseDraft();
        updateCourseDraft.setId(id);
        updateCourseDraft.setCVersion(courseDraft.getCVersion() + 1);
        //2. Set Course Steps, Course Steps Can Only Proceed Forward
        if (courseDraft.getStep() < step) {
            updateCourseDraft.setStep(step);
        }else {
            updateCourseDraft.setStep(courseDraft.getStep());
        }
        //3. Set Lesson Count, Save Directory and Save Question in Two Steps for Modification
        if (CourseConstants.CourseStep.CATALOGUE == step ||
                CourseConstants.CourseStep.SUBJECT == step) {
            //3.1. Saving Questions and Saving Directory Both Modify Lesson Count
            updateCourseDraft.setSectionNum(courseCatalogueDraftService.totalSectionNums(id));
        }
        //4. Update Course Status
        baseMapper.updateById(updateCourseDraft);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void upShelf(Long id) {
        // 1. Information Retrieval
        //1.1. Retrieve Draft Course Information for Publishing
        CourseDraft courseDraft = baseMapper.selectById(id);
        //1.2. Retrieve Course Information
        Course course = courseMapper.selectById(id);
        boolean isFirstUpShelf = (course == null);

        // 2. Course Validation
        checkBeforeUpShelf(id);

        //3. Calculate Total Video Duration for Each Chapter
        Map<Long, Integer> mediaDurations = courseCatalogueDraftService.calculateMediaDuration(id);
        //3.1. Calculate Total Course Video Duration
        int totalMediaDuration = mediaDurations
                .values()
                .stream()
                .mapToInt(p -> p)
                .sum();


        //4. Draft Information Publish to Production Environment
        //4.1. Course Teacher Information
        courseTeacherDraftService.copyToShelf(id, isFirstUpShelf);
        //4.2. Question Information Publish
        courseCatalogueDraftService.copySubjectToShelf(id, isFirstUpShelf);
        //4.3. Directory Information Publish
        courseCatalogueDraftService.copyToShelf(id, isFirstUpShelf);
        //4.4. Assemble Basic Course Information and Course Content Information
        CourseContentDraft courseContentDraft = courseContentDraftMapper.selectById(id);
        CourseContent courseContent = BeanUtils.toBean(courseContentDraft, CourseContent.class);
        Course courseToShelf = BeanUtils.toBean(courseDraft, Course.class);
        //4.4.1. Total Course Video Duration
        courseToShelf.setMediaDuration(totalMediaDuration);
        //4.4.2. Course Validity Month Count
        courseToShelf.setValidDuration(courseDraft.getValidDuration());
        //4.4.3. Course Publish Time
        courseToShelf.setPublishTime(DateUtils.now());
        //4.4.4. Set Course Status to Published
        courseToShelf.setStatus(CourseStatus.SHELF.getStatus());
        //4.4.5. Course Publish Count
        int publishTimes = (course == null) ?
                1 : NumberUtils.null2Zero(course.getPublishTimes()) + 1;
        courseToShelf.setPublishTimes(publishTimes);
        // 4.4.6. Rating
        courseToShelf.setScore((int)(Math.random() * 10) + 40);

        //4.5. First Publish
        if (isFirstUpShelf) {
            //4.5.1. Insert Course Content Information
            int result = courseContentMapper.insert(courseContent);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.5.2. Insert Course Basic Information
            result = courseMapper.insert(courseToShelf);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.5.1. Delete Draft Course Basic Information
            baseMapper.deleteById(id);
            //4.5.2. Delete Draft Course Content Information
            courseContentDraftMapper.deleteById(id);
        } else {
            //4.6. Re-Publish
            //4.6.1. Update Formal Course Content Information
            int result = courseContentMapper.updateById(courseContent);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.6.2. Update Formal Course Basic Information
            result = courseMapper.updateVariableById(courseToShelf);
            if (result <= 0) {
                throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
            }
            //4.6.3. Delete Draft Course Basic Information
            baseMapper.deleteById(id);
            //4.6.4. Delete Draft Course Content Information
            courseContentDraftMapper.deleteById(id);

        }
        //5. Course Publish MQ
        rabbitMqHelper.send(MqConstants.Exchange.COURSE_EXCHANGE, MqConstants.Key.COURSE_UP_KEY, id);
    }

    @Override
    public void checkBeforeUpShelf(Long id) {
        //1. Retrieve Draft Course Information for Publishing
        CourseDraft courseDraft = baseMapper.selectById(id);
        //1.1. Retrieve Course Information
        Course course = courseMapper.selectById(id);
        //2. Course Validation
        //2.1. Course Publish Idempotency Validation
        if (courseDraft == null && course != null) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_UP_SHELF_AREADY);
        }
        //2.2. Courses Without ID Cannot Be Published
        if (courseDraft == null && course == null) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_UP_SHELF_NOT_FOUND_COURSE);

        }
        //2.3. Incomplete Draft Information Cannot Be Published
        if (courseDraft.getStep() != CourseConstants.CourseStep.TEACHER) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_UP_SHELF_INFO_INCOMPLETE);
        }
        //Course
        //2.4. Already Published or Completed Courses Cannot Be Published
        if (course != null && course.getStatus() != CourseStatus.DOWN_SHELF.getStatus()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_UP_SHELF_STATE_WRONG);
        }
        //2.5. Validate Course End Time
        if(courseDraft.getPurchaseEndTime().isBefore(DateUtils.now())){
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_UP_SHELF_PURCHASE_ILLEGAL);
        }
        //2.6. First Publish Validation Logic
        if (course == null) {
            //2.5.1. Count Same-Named Courses
            int sameNameNum = courseMapper.countSameName(courseDraft.getName());
            //2.5.2. Same-Named Courses Cannot Be Published
            if (sameNameNum > 0) {
                throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_NAME_EXISTS);
            }
        }
        //2.7. Validate Course Directory
        courseCatalogueDraftService.checkCataInfoImplated(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void downShelf(Long id) {
        //1. Query Course Basic Information
        Course course = courseService.getById(id);
        //1.1. Course Status Judgment
        if (course == null || !course.getStatus().equals(CourseStatus.SHELF.getStatus())) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_DOWN_SHELF_FAILD);
        }
        //2. Update Course Status First
        courseService.updateStatus(id, CourseStatus.DOWN_SHELF.getStatus());
        //3. Copy Course Basic Information and Content Information to Draft
        baseMapper.insertFromCourse(id);
        //4. Copy Course Content to Draft
        courseContentDraftMapper.insertFromCourseContent(id);
        //5. Copy Directory Content to Draft
        courseCatalogueDraftMapper.insertFromCourseCatalogue(id);
        //6. Copy Course Questions to Draft
        copySubject2Draft(id);
        //7. Copy Course Teacher to Draft
        courseTeacherDraftMapper.insertFromCourseTeacher(id);
        //8. Offline MQ Broadcast
        rabbitMqHelper.send(MqConstants.Exchange.COURSE_EXCHANGE, MqConstants.Key.COURSE_DOWN_KEY, id);
    }

    @GlobalTransactional
    public void copySubject2Draft(Long courseId) {
        // 1. Query Course Related Subsection Information
        List<Long> sectionIds = courseCatalogueDraftMapper.getSectionIdByCourseId(courseId);
        if (CollUtils.isEmpty(sectionIds)) {
            log.error("Course Subsection Data is Empty");
            return;
        }
        // 2. Query Question Relationships
        List<QuestionBizDTO> qbs = examClient.queryQuestionIdsByBizIds(sectionIds);
        if (CollUtils.isEmpty(qbs)) {
            return;
        }
        List<CourseCataSubjectDraft> list = qbs.stream().map(q -> new CourseCataSubjectDraft()
                .setCourseId(courseId).setCataId(q.getBizId()).setSubjectId(q.getQuestionId())
        ).collect(Collectors.toList());
        // 3. Save to Draft Table
        courseCataSubjectDraftMapper.batchInsert(list);
    }

    @Override
    public CourseDTO getCourseDTOById(Long id) {
        //1. Query Course Draft Basic Information
        CourseDraft courseDraft = baseMapper.selectById(id);
        //1.1. Null Check
        if (courseDraft == null) {
            return new CourseDTO();
        }
        //2. Query course teacher list and take the first item
        LambdaQueryWrapper<CourseTeacherDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseTeacherDraft.class)
                .eq(CourseTeacherDraft::getCourseId, id)
                .orderBy(true, false, CourseTeacherDraft::getCIndex)
                .last(true, "limit 1");
        //2.1. Query course teacher information
        CourseTeacherDraft courseTeacherDraft = courseTeacherDraftMapper.selectOne(queryWrapper);
        //3. Assemble data
        CourseDTO courseDTO = BeanUtils.toBean(courseDraft, CourseDTO.class);
        //3.1. Set course category, first-level, second-level, third-level course category
        courseDTO.setCategoryIdLv1(courseDraft.getFirstCateId());
        courseDTO.setCategoryIdLv2(courseDraft.getSecondCateId());
        courseDTO.setCategoryIdLv3(courseDraft.getThirdCateId());
        //3.2. Set total video playback duration of the course
        courseDTO.setDuration(courseDraft.getMediaDuration());
        //3.3. Set total number of course segments
        courseDTO.setSections(courseDraft.getSectionNum());
        //3.4. Set course teacher ID
        if (courseTeacherDraft != null) {
            courseDTO.setTeacher(courseTeacherDraft.getTeacherId());
        } else {
            courseDTO.setTeacher(0L);
        }

        return courseDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void delete(Long id) {
        //1. Delete course draft
        baseMapper.deleteById(id);
        //2. Delete course content draft
        courseContentDraftMapper.deleteById(id);
        //3. Delete course question relationship draft
        courseCataSubjectDraftMapper.deleteByCourseId(id);
        //4. Delete course directory draft
        courseCatalogueDraftMapper.deleteByCourseId(id, Arrays.asList(
                CourseConstants.CataType.CHAPTER,
                CourseConstants.CataType.SECTION,
                CourseConstants.CataType.PRATICE
        ));
        //5. Delete course teacher relationship draft
        courseTeacherDraftMapper.deleteByCourseId(id);
    }

    @Override
    public PageDTO<CoursePageVO> queryForPage(CoursePageQuery coursePageQuery) {
        //1. Course draft pagination query conditions
        LambdaQueryWrapper<CourseDraft> queryWrapper =
                SqlWrapperUtils.toLambdaQueryWrapper(coursePageQuery, CourseDraft.class);
        //1.1 Course query conditions - update time
        queryWrapper.between(
                ObjectUtils.isNotEmpty(coursePageQuery.getBeginTime()) &&
                        ObjectUtils.isNotEmpty(coursePageQuery.getEndTime()),
                CourseDraft::getUpdateTime,
                coursePageQuery.getBeginTime(),
                coursePageQuery.getEndTime());
        //1.2 Course query conditions - search keyword
        queryWrapper.like(StringUtils.isNotEmpty(coursePageQuery.getKeyword()),
                CourseDraft::getName, coursePageQuery.getKeyword());
        //1.3. Pagination query data
        Page<CourseDraft> page = page(coursePageQuery.toMpPage(), queryWrapper);
        //1.4. Pagination query result null check
        if (CollUtils.isEmpty(page.getRecords())) {
            return PageDTO.empty(page);
        }
        //2. Assemble data query
        //2.1. Course updater ID list
        List<Long> updaterList = page.getRecords().stream()
                .map(CourseDraft::getUpdater)
                .collect(Collectors.toList());
        //2.2. Query updater user information
        List<UserDTO> userDTOS = userClient.queryUserByIds(updaterList);
        //2.3. Convert updater user ID + name mapping relationship
        Map<Long, String> updaterMap =
                CollUtils.isEmpty(updaterList) ?
                        new HashMap<>() : userDTOS.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        //2.4. Query course category list
        List<Category> list = categoryService.list();
        //2.5. Convert course category ID + name mapping relationship
        Map<Long, String> categoryNameMap =
                CollUtils.isEmpty(list) ?
                        new HashMap<>() : list.stream().collect(Collectors.toMap(Category::getId, Category::getName));
        //2.6. Course ID list
        List<Long> courseIdList = page.getRecords().stream().map(CourseDraft::getId).collect(Collectors.toList());
        //2.7. Statistics course enrollment number map
        Map<Long, Integer> peoNumOfCourseMap = tradeClient.countEnrollNumOfCourse(courseIdList);
        //3. Data packaging
        return PageDTO.of(page, CoursePageVO.class, (course, coursePageVO) -> {
            //3.1. Concatenate course category
            String categories = StringUtils.format("{}/{}/{}",
                    categoryNameMap.get(course.getFirstCateId()),
                    categoryNameMap.get(course.getSecondCateId()),
                    categoryNameMap.get(course.getThirdCateId()));
            //3.2. Set course category
            coursePageVO.setCategories(categories);
            //3.3. Set course updater
            coursePageVO.setUpdaterName(updaterMap.get(course.getUpdater()));
            //3.4. Set course enrollment number
            coursePageVO.setSold(NumberUtils.null2Zero(peoNumOfCourseMap.get(course.getId())));
            //3.5. Set total course hours
            coursePageVO.setSections(course.getSectionNum());
        });
    }

    @Override
    public NameExistVO checkName(String name, Long id) {
        //1. Course draft same name query conditions
        LambdaQueryWrapper<CourseDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseDraft.class)
                        .eq(CourseDraft::getName, name)
                        .last(id != null, " and id !=" + id);
        //2. Statistics same name course count
        Integer num = baseMapper.selectCount(queryWrapper);
        //3. Return same name course VO
        return new NameExistVO(num > 0);
    }

    @Override
    public List<Long> queryExists(List<Long> idList) {
        //1. Query draft course basic information list
        List<CourseDraft> courses = baseMapper.selectBatchIds(idList);
        //1.1. Draft course information list null check
        if (CollUtils.isEmpty(courses)) {
            return null;
        }
        //2. Assemble data
        return courses.stream()
                .map(CourseDraft::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> countCourseNumOfCategory() {
        //1. Query all course drafts
        List<CourseDraft> courses = baseMapper.selectList(null);
        Map<Long, Integer> cateIdAndNumMap = new HashMap<>();
        //2. Traverse and count the number of courses in each course category
        for (CourseDraft course : courses) {
            //2.1. Count number of first-level course category courses
            Integer firstCateNum = cateIdAndNumMap.get(course.getFirstCateId());
            cateIdAndNumMap.put(course.getFirstCateId(), firstCateNum == null ? 1 : firstCateNum + 1);
            //2.2. Count number of second-level course category courses
            Integer secondCateNum = cateIdAndNumMap.get(course.getSecondCateId());
            cateIdAndNumMap.put(course.getSecondCateId(), secondCateNum == null ? 1 : secondCateNum + 1);
            //2.3. Count number of third-level course category courses
            Integer thirdCateNum = cateIdAndNumMap.get(course.getThirdCateId());
            cateIdAndNumMap.put(course.getThirdCateId(), thirdCateNum == null ? 1 : thirdCateNum + 1);
        }
        return cateIdAndNumMap;
    }

    private void checkSameCourse(Long id, String name) {
        //1. Query formal environment for same name course
        int countSameNameNum = courseMapper.countSameName(name);
        //1.1. Same name course data check for 0
        if (countSameNameNum > 0) { //Name already exists, perform double validation on submission
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_NAME_EXISTS);
        }
        //2. Query formal environment for same name course
        countSameNameNum = baseMapper.countByNameAndId(name, id);
        //2.1. Same name course data check for 0
        if (countSameNameNum > 0) {
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_NAME_EXISTS);
        }
    }
}
