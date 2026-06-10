package com.learnforge.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.api.dto.exam.QuestionBizDTO;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.exam.domain.po.QuestionBiz;
import com.learnforge.exam.mapper.QuestionBizMapper;
import com.learnforge.exam.service.IQuestionBizService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Question and business association table, for example, associate small section id with question id, one small section can have multiple questions Service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Service
public class QuestionBizServiceImpl extends ServiceImpl<QuestionBizMapper, QuestionBiz> implements IQuestionBizService {

    @Override
    public int countUsedTimes(Long questionId) {
        Integer count = lambdaQuery()
                .eq(QuestionBiz::getQuestionId, questionId)
                .count();
        return count == null ? 0 : count;
    }

    @Override
    public Map<Long, Integer> countUsedTimes(Set<Long> qIds) {
        // 1. Statistics on reference count
        List<IdAndNumDTO> list = baseMapper.countUsedTimes(qIds);
        // 2. Convert return
        return IdAndNumDTO.toMap(list);
    }

    @Override
    public List<QuestionBizDTO> queryQuestionIdsByBizId(Long bizId) {
        List<QuestionBiz> list = lambdaQuery()
                .eq(QuestionBiz::getBizId, bizId)
                .list();
        return BeanUtils.copyList(list, QuestionBizDTO.class);
    }

    @Override
    public List<QuestionBizDTO> queryQuestionIdsByBizIds(List<Long> bizIds) {
        if (CollUtils.isEmpty(bizIds)) {
            return CollUtils.emptyList();
        }
        List<QuestionBiz> list = lambdaQuery()
                .in(QuestionBiz::getBizId, bizIds)
                .list();
        return BeanUtils.copyList(list, QuestionBizDTO.class);
    }

    @Override
    @Transactional
    public void saveQuestionBizInfoBatch(List<QuestionBizDTO> qbs) {
        if (CollUtils.isEmpty(qbs)) {
            return;
        }
        // 1. Get business id
        Set<Long> bizIds = qbs.stream().map(QuestionBizDTO::getBizId).collect(Collectors.toSet());
        // 2. Delete old data
        remove(new LambdaQueryWrapper<QuestionBiz>().in(QuestionBiz::getBizId, bizIds));
        // 3. Insert new data
        List<QuestionBiz> list = qbs.stream()
                .map(q -> QuestionBiz.of(null, q.getBizId(), q.getQuestionId()))
                .collect(Collectors.toList());
        saveBatch(list);
    }

    @Override
    public Map<Long, Integer> queryQuestionScoresByBizIds(Iterable<Long> bizIds) {
        if (CollUtils.isEmpty(bizIds)) {
            return CollUtils.emptyMap();
        }
        // 1. Statistics on biz and corresponding question scores
        List<IdAndNumDTO> list = baseMapper.countQuestionScoresByBizIds(bizIds);
        // 2. Data processing
        return IdAndNumDTO.toMap(list);
    }
}
