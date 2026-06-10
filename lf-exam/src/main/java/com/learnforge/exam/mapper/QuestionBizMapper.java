package com.learnforge.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.exam.domain.po.QuestionBiz;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Question and business association table, for example, associate small section id with question id, one small section can have multiple questions Mapper interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
public interface QuestionBizMapper extends BaseMapper<QuestionBiz> {

    List<IdAndNumDTO> countQuestionScoresByBizIds(@Param("bizIds") Iterable<Long> bizIds);

    List<IdAndNumDTO> countUsedTimes(@Param("qIds") Iterable<Long> qIds);
}
