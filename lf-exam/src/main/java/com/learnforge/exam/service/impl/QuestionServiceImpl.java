package com.learnforge.exam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.cache.CategoryCache;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.api.dto.exam.QuestionDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.constants.Constant;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.exam.domain.dto.QuestionFormDTO;
import com.learnforge.exam.domain.po.Question;
import com.learnforge.exam.domain.po.QuestionBiz;
import com.learnforge.exam.domain.po.QuestionDetail;
import com.learnforge.exam.domain.query.QuestionPageQuery;
import com.learnforge.exam.domain.vo.QuestionDetailVO;
import com.learnforge.exam.domain.vo.QuestionPageVO;
import com.learnforge.exam.mapper.QuestionMapper;
import com.learnforge.exam.service.IQuestionBizService;
import com.learnforge.exam.service.IQuestionDetailService;
import com.learnforge.exam.service.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.exam.constants.ExamErrorInfo.QUESTION_NOT_EXISTS;

/**
 * <p>
 * Question Service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

    private final IQuestionDetailService detailService;
    private final IQuestionBizService bizService;
    private final UserClient userClient;
    private final CategoryCache categoryCache;

    @Override
    @Transactional
    public void addQuestion(QuestionFormDTO questionDTO) {
        // 1. Save question information
        Question question = BeanUtils.copyBean(questionDTO, Question.class);
        List<Long> cateIds = questionDTO.getCateIds();
        if (cateIds.size() < 3) {
            throw new BadRequestException("Question must be associated with third-level category");
        }
        question.setCateId1(cateIds.get(0));
        question.setCateId2(cateIds.get(1));
        question.setCateId3(cateIds.get(2));
        save(question);

        // 2. Save details
        QuestionDetail detail = new QuestionDetail()
                .setId(question.getId())
                .setAnalysis(questionDTO.getAnalysis())
                .setAnswer(questionDTO.getAnswer())
                .setOptions(questionDTO.getOptions());
        detailService.save(detail);
    }

    @Override
    public void updateQuestion(QuestionFormDTO questionDTO) {
        // 1. Save question information
        Question question = BeanUtils.copyBean(questionDTO, Question.class);
        List<Long> cateIds = questionDTO.getCateIds();
        if (CollUtils.isNotEmpty(cateIds) && cateIds.size() == 3) {
            question.setCateId1(cateIds.get(0));
            question.setCateId2(cateIds.get(1));
            question.setCateId3(cateIds.get(2));
        }
        updateById(question);

        // 2. Save details
        QuestionDetail detail = new QuestionDetail()
                .setId(question.getId())
                .setAnalysis(questionDTO.getAnalysis())
                .setAnswer(questionDTO.getAnswer())
                .setOptions(questionDTO.getOptions());
        detailService.updateById(detail);
    }

    @Override
    public void deleteQuestionById(Long id) {
        // 1. Query whether there is an association between question and business
        int usedTimes = bizService.countUsedTimes(id);
        if (usedTimes > 0) {
            throw new BadRequestException("Question is in use, cannot be deleted");
        }
        // 2. Delete Question
        removeById(id);
        // 3. Delete details
        detailService.removeById(id);
    }

    @Override
    public PageDTO<QuestionPageVO> queryQuestionByPage(QuestionPageQuery query) {
        // 1. Page search
        Page<Question> page = lambdaQuery()
                .eq(query.getDifficulty() != null, Question::getDifficulty, query.getDifficulty())
                .eq(query.getCreater() != null, Question::getCreater, query.getCreater())
                .in(CollUtils.isNotEmpty(query.getTypes()), Question::getType, query.getTypes())
                .in(CollUtils.isNotEmpty(query.getCateIds()), Question::getCateId3, query.getCateIds())
                .like(StringUtils.isNotBlank(query.getKeyword()), Question::getName, query.getKeyword())
                .page(query.toMpPage(Constant.DATA_FIELD_NAME_UPDATE_TIME, false));
        // 2. Check for null
        List<Question> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        // 3. Query VO information, including: reference count, questioner information
        Set<Long> qIds = new HashSet<>();
        Set<Long> uIds = new HashSet<>();
        for (Question record : records) {
            qIds.add(record.getId());
            uIds.add(record.getUpdater());
        }
        // 3.1. Statistics reference count
        Map<Long, Integer> countMap = bizService.countUsedTimes(qIds);
        // 3.2. Query user
        Map<Long, UserDTO> userMap = new HashMap<>(uIds.size());
        if (CollUtils.isNotEmpty(uIds)) {
            List<UserDTO> users = userClient.queryUserByIds(uIds);
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
        }
        // 4. Process VO
        List<QuestionPageVO> list = new ArrayList<>(records.size());
        for (Question r : records) {
            // 4.1. Convert to VO
            QuestionPageVO v = BeanUtils.toBean(r, QuestionPageVO.class);
            list.add(v);
            // 4.2. Get user
            UserDTO u = userMap.get(r.getUpdater());
            v.setUpdater(u == null ? "" : u.getName());
            // 4.3. Categorize
            v.setCategories(categoryCache.getCategoryNameList(List.of(r.getCateId1(), r.getCateId2(), r.getCateId3())));
            // 4.4. Reference count
            v.setUseTimes(countMap.getOrDefault(r.getId(), 0));
        }
        return PageDTO.of(page, list);
    }

    @Override
    public QuestionDetailVO queryQuestionDetailById(Long id) {
        // 1. Query question
        Question q = getById(id);
        if (q == null) {
            throw new BadRequestException(QUESTION_NOT_EXISTS);
        }
        // 2. Query details
        QuestionDetail detail = detailService.getById(id);
        if (detail == null) {
            throw new BadRequestException(QUESTION_NOT_EXISTS);
        }
        // 3. Query question submitter
        UserDTO u = userClient.queryUserById(q.getCreater());
        // 4. Convert VO
        QuestionDetailVO v = BeanUtils.copyBean(q, QuestionDetailVO.class);
        // 4.1. Details
        v.setOptions(detail.getOptions());
        v.setAnalysis(detail.getAnalysis());
        v.setAnswer(detail.getAnswer());
        // 4.2. User
        v.setUpdater(u == null ? "" : u.getName());
        // 4.3. Categorize
        v.setCategories(categoryCache.getCategoryNameList(List.of(q.getCateId1(), q.getCateId2(), q.getCateId3())));
        // 4.4. Reference count
        v.setUseTimes(bizService.countUsedTimes(id));
        return v;
    }

    @Override
    public List<QuestionDTO> queryQuestionByIds(List<Long> ids) {
        // 1. Query question collection
        List<Question> questions = listByIds(ids);
        if (CollUtils.isEmpty(questions)) {
            return CollUtils.emptyList();
        }

        // 2. Query details
        List<QuestionDetail> details = detailService.listByIds(ids);
        if (details == null || questions.size() != details.size()) {
            throw new BadRequestException(QUESTION_NOT_EXISTS);
        }
        Map<Long, QuestionDetail> detailMap = details.stream().collect(Collectors.toMap(QuestionDetail::getId, d -> d));

        // 3. Data conversion
        List<QuestionDTO> list = new ArrayList<>(questions.size());
        for (Question q : questions) {
            // 3.1. Convert to VO
            QuestionDTO d = BeanUtils.toBean(q, QuestionDTO.class);
            list.add(d);
            // 3.2. Get details
            QuestionDetail detail = detailMap.get(q.getId());
            d.setOptions(detail.getOptions());
            d.setAnalysis(detail.getAnalysis());
            d.setAnswer(detail.getAnswer());
        }
        return list;
    }

    @Override
    public Map<Long, Integer> countQuestionNumOfCreater(List<Long> createrIds) {
        // 1. Statistics
        List<IdAndNumDTO> list = baseMapper.countQuestionOfCreater(createrIds);
        // 2. Process result
        return IdAndNumDTO.toMap(list);
    }

    @Override
    public List<QuestionDTO> queryQuestionByBizId(Long bizId) {
        // 1. Query intermediate table
        List<QuestionBiz> list = bizService.lambdaQuery()
                .eq(QuestionBiz::getBizId, bizId)
                .list();
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 2. Get question id
        List<Long> ids = list.stream().map(QuestionBiz::getQuestionId).collect(Collectors.toList());
        // 3. Query data collection
        return queryQuestionByIds(ids);
    }

    @Override
    public Boolean checkNameValid(String name) {
        return lambdaQuery()
                .eq(Question::getName, name)
                .count()<=0;
    }

    @Override
    public Map<Long, Integer> queryQuestionScores(List<Long> ids) {
        // 1. Query question by id
        List<Question> questions = listByIds(ids);
        // 2. Check for null
        if (CollUtils.isEmpty(questions)) {
            return CollUtils.emptyMap();
        }
        return questions.stream().collect(Collectors.toMap(Question::getId, Question::getScore));
    }
}
