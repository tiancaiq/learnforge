package com.learnforge.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.api.dto.exam.QuestionBizDTO;
import com.learnforge.exam.domain.po.QuestionBiz;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Question and business association table, for example, associate small section id with question id, one small section can have multiple questions service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
public interface IQuestionBizService extends IService<QuestionBiz> {

    int countUsedTimes(Long questionId);

    Map<Long, Integer> countUsedTimes(Set<Long> qIds);

    List<QuestionBizDTO> queryQuestionIdsByBizId(Long bizId);

    List<QuestionBizDTO> queryQuestionIdsByBizIds(List<Long> bizIds);

    void saveQuestionBizInfoBatch(List<QuestionBizDTO> qbs);

    Map<Long, Integer> queryQuestionScoresByBizIds(Iterable<Long> bizIds);
}
