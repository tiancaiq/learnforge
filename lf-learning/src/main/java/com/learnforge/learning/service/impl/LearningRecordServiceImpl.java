package com.learnforge.learning.service.impl;

import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.dto.course.CourseFullInfoDTO;
import com.learnforge.api.dto.leanring.LearningLessonDTO;
import com.learnforge.api.dto.leanring.LearningRecordDTO;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.learning.domain.dto.LearningRecordFormDTO;
import com.learnforge.learning.domain.enums.LessonStatus;
import com.learnforge.learning.domain.enums.SectionType;
import com.learnforge.learning.domain.po.LearningLesson;
import com.learnforge.learning.domain.po.LearningRecord;
import com.learnforge.learning.mapper.LearningRecordMapper;
import com.learnforge.learning.service.ILearningLessonService;
import com.learnforge.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.learning.utils.LearningRecordDelayTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-14
 */
@Service
@RequiredArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    private final ILearningLessonService lessonService;
    private final CourseClient courseClient;

    private final LearningRecordDelayTaskHandler taskHandler;

    @Override
    public LearningLessonDTO queryLearningRecordByCourse(Long courseId) {
        // 1. get login user
        Long userId = UserContext.getUser();
        //2 . query course
        LearningLesson lesson =  lessonService.queryByUserIdAndCourseId(userId,courseId);

        if (lesson==null){
            return null;
        }
        //3. query learning record
        List<LearningRecord> records = lambdaQuery()
                .eq(LearningRecord::getLessonId, lesson.getId())
                .list();
        //4. dto
        LearningLessonDTO dto = new LearningLessonDTO();
        dto.setId(lesson.getId());
        dto.setLatestSectionId(lesson.getLatestSectionId());
        dto.setRecords(BeanUtils.copyList(records, LearningRecordDTO.class));
        return dto;
    }

    @Override
    @Transactional
    public void addLearningRecord(LearningRecordFormDTO recordDTO) {
        //1. get user id
        Long userId = UserContext.getUser();

        //2. study record
        boolean finished = false;
        if (recordDTO.getSectionType() == SectionType.VIDEO) {

            finished = handleVideoRecord(userId, recordDTO);
        } else {

            finished = handleExamRecord(userId, recordDTO);
        }
        // 2.1 video
        // 2.2 test


        if (!finished) {
            // no finished section no update
            return;
        }


        // 3. lesson data

        handleLearningLessonChange(recordDTO);

    }

    private void handleLearningLessonChange(LearningRecordFormDTO recordDTO) {
        // query lesson
        LearningLesson lesson = lessonService.getById(recordDTO.getLessonId());


        boolean allLearned = false;
        //2 if new finished session

            // 3, if new finished session, then query lesson data
            CourseFullInfoDTO cInfo = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);

            if (cInfo==null){
                throw new BizIllegalException("lesson not exist");
            }
            // 4. if all lesson done learned session >= total session
            allLearned = lesson.getLearnedSections() + 1 >= cInfo.getSectionNum();

        lessonService.lambdaUpdate()
                .set(lesson.getLearnedSections() ==0, LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .set(allLearned,  LearningLesson::getStatus, LessonStatus.FINISHED.getValue())
                .setSql("learned_sections = learned_sections + 1")
                .eq(LearningLesson::getId, lesson.getId())
                .update();
    }

    private boolean handleVideoRecord(Long userId, LearningRecordFormDTO recordDTO) {

        // query old learning record
//        LearningRecord old  = lambdaQuery()
//                .eq(LearningRecord::getLessonId, recordDTO.getLessonId())
//                .eq(LearningRecord::getSectionId, recordDTO.getSectionId())
//                .one();
        LearningRecord old = queryOldRecord(recordDTO.getLessonId(),recordDTO.getSectionId());

        // check if exist
        if (old==null){
            // not exist add.
            LearningRecord record = BeanUtils.copyBean(recordDTO, LearningRecord.class);
            record.setUserId(userId);

            boolean success = save(record);

            if (!success) {
                throw new DbException("add new video record failed");
            }
            return false;
        }

        // exist
        // if finished first time
        boolean finished = !old.getFinished() && recordDTO.getMoment() * 2 >= recordDTO.getDuration();
        if(!finished){
            LearningRecord record = new LearningRecord();
            record.setId(old.getId());
            record.setFinished(old.getFinished());
            record.setMoment(recordDTO.getMoment());
            record.setLessonId(recordDTO.getLessonId());
            record.setSectionId(recordDTO.getSectionId());
            taskHandler.addLearningRecord(record);
            return false;
        }

        // update
        boolean success = lambdaUpdate()
                .set(LearningRecord::getMoment, recordDTO.getMoment())
                .set(LearningRecord::getFinished, true)
                .set(LearningRecord::getFinishTime, recordDTO.getCommitTime())
                .eq(LearningRecord::getId, old.getId())
                .update();
        if  (!success) {
            throw new DbException("add new video record failed");
        }

        // clean cache
        taskHandler.cleanRecordCache(recordDTO.getLessonId(), recordDTO.getSectionId());
        return true;
    }

    private LearningRecord queryOldRecord(
            @NotNull(message = "Lesson ID is required") Long lessonId,
            @NotNull(message = "Section ID is required") Long sectionId) {
        //1. query cache
        LearningRecord record = taskHandler.readRecordCache(lessonId, sectionId);
        if (record != null) {
            return record;
        }
        //2. hit return

        //3. not query db
                record  = lambdaQuery()
                .eq(LearningRecord::getLessonId, lessonId)
                .eq(LearningRecord::getSectionId, sectionId)
                .one();
        //4. write into cache
        taskHandler.writeRecordCache(record);

        return record;
    }

    private boolean handleExamRecord(Long userId, LearningRecordFormDTO recordDTO) {

        // DTo to po
        LearningRecord record = BeanUtils.copyBean(recordDTO, LearningRecord.class);
        record.setUserId(userId);
        record.setFinished(true);
        record.setFinishTime(record.getFinishTime());

        boolean success = save(record);

        if (!success) {
            throw new DbException("add new exam record failed");
        }
        return true;
    }
}
