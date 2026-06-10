package com.learnforge.exam.mapper;

import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.exam.domain.po.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Question Mapper interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
public interface QuestionMapper extends BaseMapper<Question> {

    List<IdAndNumDTO> countQuestionOfCreater(@Param("createrIds") List<Long> createrIds);

}
