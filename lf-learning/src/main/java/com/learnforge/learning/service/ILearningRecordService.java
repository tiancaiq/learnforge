package com.learnforge.learning.service;

import com.learnforge.api.dto.leanring.LearningLessonDTO;
import com.learnforge.learning.domain.dto.LearningRecordFormDTO;
import com.learnforge.learning.domain.po.LearningRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-14
 */
public interface ILearningRecordService extends IService<LearningRecord> {

    LearningLessonDTO queryLearningRecordByCourse(Long courseId);

    void addLearningRecord(LearningRecordFormDTO formDTO);
}
