package com.learnforge.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.course.CatalogueClient;
import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.api.dto.course.CataSimpleInfoDTO;
import com.learnforge.api.dto.course.CourseFullInfoDTO;
import com.learnforge.api.dto.course.CourseSimpleInfoDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.domain.query.PageQuery;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.*;
import com.learnforge.learning.domain.po.LearningLesson;
import com.learnforge.learning.domain.po.LearningRecord;
import com.learnforge.learning.domain.vo.LearningLessonVO;
//import com.learnforge.learning.domain.vo.LearningPlanPageVO;
//import com.learnforge.learning.domain.vo.LearningPlanVO;
import com.learnforge.learning.domain.enums.LessonStatus;
import com.learnforge.learning.domain.enums.PlanStatus;
import com.learnforge.learning.domain.vo.LearningPlanPageVO;
import com.learnforge.learning.domain.vo.LearningPlanVO;
import com.learnforge.learning.mapper.LearningLessonMapper;
import com.learnforge.learning.mapper.LearningRecordMapper;
import com.learnforge.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>

 * </p>
 *

 * @since 2022-12-02
 */
@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    private final CourseClient courseClient;
    private final CatalogueClient catalogueClient;
    private final LearningRecordMapper recordMapper;

    @Override
    @Transactional
    public void addUserLessons(Long userId, List<Long> courseIds) {

        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(courseIds);
        if (CollUtils.isEmpty(cInfoList)) {

            log.error("Course information was not found; lessons were not created");
            return;
        }

        List<LearningLesson> list = new ArrayList<>(cInfoList.size());
        for (CourseSimpleInfoDTO cInfo : cInfoList) {
            LearningLesson lesson = new LearningLesson();

            Integer validDuration = cInfo.getValidDuration();
            if (validDuration != null && validDuration > 0) {
                LocalDateTime now = LocalDateTime.now();
                lesson.setCreateTime(now);
                lesson.setExpireTime(now.plusMonths(validDuration));
            }

            lesson.setUserId(userId);
            lesson.setCourseId(cInfo.getId());
            list.add(lesson);
        }

        saveBatch(list);
    }

    @Override
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {

        Long userId = UserContext.getUser();

        // select * from learning_lesson where user_id = #{userId} order by latest_learn_time limit 0, 5
        Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId, userId) // where user_id = #{userId}
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);


        List<LearningLessonVO> list = new ArrayList<>(records.size());

        for (LearningLesson r : records) {

            LearningLessonVO vo = BeanUtils.copyBean(r, LearningLessonVO.class);

            CourseSimpleInfoDTO cInfo = cMap.get(r.getCourseId());
            vo.setCourseName(cInfo.getName());
            vo.setCourseCoverUrl(cInfo.getCoverUrl());
            vo.setSections(cInfo.getSectionNum());
            list.add(vo);
        }
        return PageDTO.of(page, list);
    }

    private Map<Long, CourseSimpleInfoDTO> queryCourseSimpleInfoList(List<LearningLesson> records) {

        Set<Long> cIds = records.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());

        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(cIds);
        if (CollUtils.isEmpty(cInfoList)) {

            throw new BadRequestException("Course not found");
        }

        Map<Long, CourseSimpleInfoDTO> cMap = cInfoList.stream()
                .collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        return cMap;
    }

    @Override
    public LearningLessonVO queryMyCurrentLesson() {

        Long userId = UserContext.getUser();

        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .orderByDesc(LearningLesson::getLatestLearnTime)
                .last("limit 1")
                .one();
        if (lesson == null) {
            return null;
        }

        LearningLessonVO vo = BeanUtils.copyBean(lesson, LearningLessonVO.class);

        CourseFullInfoDTO cInfo = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if (cInfo == null) {
            throw new BadRequestException("Course not found");
        }
        vo.setCourseName(cInfo.getName());
        vo.setCourseCoverUrl(cInfo.getCoverUrl());
        vo.setSections(cInfo.getSectionNum());

        Integer courseAmount = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .count();
        vo.setCourseAmount(courseAmount);

        List<CataSimpleInfoDTO> cataInfos =
                catalogueClient.batchQueryCatalogue(CollUtils.singletonList(lesson.getLatestSectionId()));
        if (!CollUtils.isEmpty(cataInfos)) {
            CataSimpleInfoDTO cataInfo = cataInfos.get(0);
            vo.setLatestSectionName(cataInfo.getName());
            vo.setLatestSectionIndex(cataInfo.getCIndex());
        }
        return vo;
    }

    @Override
    public LearningLessonVO queryLessonByCourseId(Long courseId) {

        Long userId = UserContext.getUser();

        LearningLesson lesson = getOne(buildUserIdAndCourseIdWrapper(userId, courseId));
        if (lesson == null) {
            return null;
        }

        return BeanUtils.copyBean(lesson, LearningLessonVO.class);
    }

    @Override
    public void deleteCourseFromLesson(Long userId, Long courseId) {

        if (userId == null) {
            userId = UserContext.getUser();
        }

        remove(buildUserIdAndCourseIdWrapper(userId, courseId));
    }

    @Override
    public Integer countLearningLessonByCourse(Long courseId) {
        // select count(1) from xx where course_id = #{cc} AND status in (0, 1, 2)
        return lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .in(LearningLesson::getStatus,
                        LessonStatus.NOT_BEGIN.getValue(),
                        LessonStatus.LEARNING.getValue(),
                        LessonStatus.FINISHED.getValue())
                .count();
    }

    @Override
    public Long isLessonValid(Long courseId) {

        Long userId = UserContext.getUser();
        if (userId == null) {
            return null;
        }

        LearningLesson lesson = getOne(buildUserIdAndCourseIdWrapper(userId, courseId));
        if (lesson == null) {
            return null;
        }
        return lesson.getId();
    }

    @Override
    public LearningLesson queryByUserAndCourseId(Long userId, Long courseId) {
        return getOne(buildUserIdAndCourseIdWrapper(userId, courseId));
    }

    @Override
    public LearningLesson queryByUserIdAndCourseId(Long userId, Long courseId) {
        return getOne(buildUserIdAndCourseIdWrapper(userId, courseId));
    }

    @Override
    public void createLearningPlans(Long courseId, Integer freq) {
        Long userId = UserContext.getUser();
        //1 query lesson
        LearningLesson lesson = queryByUserIdAndCourseId(userId, courseId);
        if (lesson == null) {
            throw new BadRequestException("lesson not found");
        }
        //2. update
        LearningLesson l = new LearningLesson();
        l.setId(lesson.getId());
        l.setWeekFreq(freq);
        if (lesson.getPlanStatus() == PlanStatus.NO_PLAN){
            l.setPlanStatus(PlanStatus.PLAN_RUNNING);
        }
        updateById(l);

    }

    @Override
    public LearningPlanPageVO queryMyPlans(PageQuery query) {
        LearningPlanPageVO result = new LearningPlanPageVO();
        //get current user
        Long userId = UserContext.getUser();

        // get starting time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = DateUtils.getWeekBeginTime(LocalDate.from(now));
        LocalDateTime end = DateUtils.getWeekEndTime(LocalDate.from(now));
        // query total stats
        Integer weekFinished = recordMapper.selectCount(new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getFinished, true)
                .gt(LearningRecord::getFinishTime, begin)
                .lt(LearningRecord::getFinishTime, end)

        );
        result.setWeekFinished(weekFinished);

        Integer weekTotalPlan = getBaseMapper().queryTotalPlan(userId);
        result.setWeekTotalPlan(weekTotalPlan);
        // query page data
        //
        Page<LearningLesson> p = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING)
                .in(LearningLesson::getStatus, LessonStatus.NOT_BEGIN, LessonStatus.LEARNING)
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = p.getRecords();
        //4.2 quert course info by course
        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);
        // 4.3 record each session in week
        List<IdAndNumDTO> list = recordMapper.countLearnedSections(userId,begin, end);
        Map<Long, Integer> countMap = IdAndNumDTO.toMap(list);

        List<LearningPlanVO> voList = new ArrayList<>(records.size());
        for (LearningLesson r : records) {
            // copy to vo
            LearningPlanVO vo = BeanUtils.copyBean(r, LearningPlanVO.class);
            //course info
            CourseSimpleInfoDTO cInfo = cMap.get(r.getCourseId());
            if (cInfo == null) {
                vo.setCourseName(cInfo.getName());
                vo.setSections(cInfo.getSectionNum());
            }
            // get learned session in week
            vo.setWeekLearnedSections( countMap.getOrDefault(r.getId(), 0));
            voList.add(vo);
        }
        return result.pageInfo(p.getTotal(), p.getTotal(), voList);
    }


//    @Override
//    public LearningPlanPageVO queryMyPlans(PageQuery query) {
//        return null;
//    }

    //    @Override
//    public void createLearningPlan(Long courseId, Integer freq) {

//        Long userId = UserContext.getUser();

//        LearningLesson lesson = queryByUserAndCourseId(userId, courseId);
//        AssertUtils.isNotNull(lesson, "Course not found");

//        LearningLesson l = new LearningLesson();
//        l.setId(lesson.getId());
//        l.setWeekFreq(freq);
//        if(lesson.getPlanStatus() == PlanStatus.NO_PLAN) {
//            l.setPlanStatus(PlanStatus.PLAN_RUNNING);
//        }
//        updateById(l);
//    }
//
//    @Override
//    public LearningPlanPageVO queryMyPlans(PageQuery query) {
//        LearningPlanPageVO result = new LearningPlanPageVO();

//        Long userId = UserContext.getUser();

//        LocalDate now = LocalDate.now();
//        LocalDateTime begin = DateUtils.getWeekBeginTime(now);
//        LocalDateTime end = DateUtils.getWeekEndTime(now);


//        Integer weekFinished = recordMapper.selectCount(new LambdaQueryWrapper<LearningRecord>()
//                .eq(LearningRecord::getUserId, userId)
//                .eq(LearningRecord::getFinished, true)
//                .gt(LearningRecord::getFinishTime, begin)
//                .lt(LearningRecord::getFinishTime, end)
//        );
//        result.setWeekFinished(weekFinished);

//        Integer weekTotalPlan = getBaseMapper().queryTotalPlan(userId);
//        result.setWeekTotalPlan(weekTotalPlan);

//


//        Page<LearningLesson> p = lambdaQuery()
//                .eq(LearningLesson::getUserId, userId)
//                .eq(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING)
//                .in(LearningLesson::getStatus, LessonStatus.NOT_BEGIN, LessonStatus.LEARNING)
//                .page(query.toMpPage("latest_learn_time", false));
//        List<LearningLesson> records = p.getRecords();
//        if (CollUtils.isEmpty(records)) {
//            return result.emptyPage(p);
//        }

//        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);

//        List<IdAndNumDTO> list = recordMapper.countLearnedSections(userId, begin, end);
//        Map<Long, Integer> countMap = IdAndNumDTO.toMap(list);

//        List<LearningPlanVO> voList = new ArrayList<>(records.size());
//        for (LearningLesson r : records) {

//            LearningPlanVO vo = BeanUtils.copyBean(r, LearningPlanVO.class);

//            CourseSimpleInfoDTO cInfo = cMap.get(r.getCourseId());
//            if (cInfo != null) {
//                vo.setCourseName(cInfo.getName());
//                vo.setSections(cInfo.getSectionNum());
//            }

//            vo.setWeekLearnedSections(countMap.getOrDefault(r.getId(), 0));
//            voList.add(vo);
//        }
//        return result.pageInfo(p.getTotal(), p.getPages(), voList);
//    }
//
    private LambdaQueryWrapper<LearningLesson> buildUserIdAndCourseIdWrapper(Long userId, Long courseId) {
        LambdaQueryWrapper<LearningLesson> queryWrapper = new QueryWrapper<LearningLesson>()
                .lambda()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId);
        return queryWrapper;
    }
}
