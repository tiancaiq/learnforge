package com.learnforge.learning.mapper;

import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.learning.domain.po.LearningRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-14
 */
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {

    List<IdAndNumDTO> countLearnedSections(@Param("userId") Long userId,
                                           @Param("begin") LocalDateTime begin,
                                           @Param("end") LocalDateTime end);
}
