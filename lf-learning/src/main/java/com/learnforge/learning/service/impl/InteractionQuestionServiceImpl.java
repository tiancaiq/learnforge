package com.learnforge.learning.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnforge.api.cache.CategoryCache;
import com.learnforge.api.client.course.CatalogueClient;
import com.learnforge.api.client.course.CategoryClient;
import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.client.search.SearchClient;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.course.CataSimpleInfoDTO;
import com.learnforge.api.dto.course.CourseSimpleInfoDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.learning.domain.dto.QuestionFormDTO;
import com.learnforge.learning.domain.po.InteractionQuestion;
import com.learnforge.learning.domain.po.InteractionReply;
import com.learnforge.learning.domain.query.QuestionAdminPageQuery;
import com.learnforge.learning.domain.query.QuestionPageQuery;
import com.learnforge.learning.domain.vo.QuestionAdminVO;
import com.learnforge.learning.domain.vo.QuestionVO;
import com.learnforge.learning.mapper.InteractionQuestionMapper;
import com.learnforge.learning.service.IInteractionQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.learning.service.IInteractionReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-25
 */
@Service
@RequiredArgsConstructor
public class InteractionQuestionServiceImpl extends ServiceImpl<InteractionQuestionMapper, InteractionQuestion> implements IInteractionQuestionService {

    private final IInteractionReplyService replyService;
    private final UserClient userClient;
    private final CourseClient courseClient;
    private final SearchClient searchClient;
    private final CatalogueClient catalogueClient;
    private final CategoryClient categoryClient;
    private final CategoryCache categoryCache;


    @Override
    public void saveQuestion(QuestionFormDTO questionDTO) {
        // get user id

        Long userId = UserContext.getUser();

        InteractionQuestion question = BeanUtils.copyBean(questionDTO, InteractionQuestion.class);
        question.setUserId(userId);


        save(question);
    }

    @Override
    public PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery query) {
        //1. para, lesson id and seection id not none
        Long courseId = query.getCourseId();
        Long sectionId = query.getSectionId();
        if (courseId == null && sectionId == null) {
            throw new BadRequestException("courseId and sectionId can not be null");
        }


        //split query
        Page<InteractionQuestion> page = lambdaQuery()
                .select(InteractionQuestion.class,info -> !info.getProperty().equals("description"))// remove the description from query
                .eq( query.getOnlyMine() != null &&  query.getOnlyMine(), InteractionQuestion::getUserId, UserContext.getUser())
                .eq(courseId != null, InteractionQuestion::getCourseId, courseId)
                .eq(sectionId != null, InteractionQuestion::getSectionId, sectionId)
                .eq(InteractionQuestion::getHidden, false)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());


        List<InteractionQuestion> records = page.getRecords();
        if(CollUtils.isEmpty(records)){
            return PageDTO.empty(page);
        }

        //3. query questioner and last question by id
        Set<Long> userIds = new HashSet<>();
        Set<Long> answerIds = new HashSet<>();
        //3.1 query questioner id and last answer id
        for (InteractionQuestion q : records) {
            if (!q.getAnonymity()){
                userIds.add(q.getUserId());
            }
            answerIds.add(q.getLatestAnswerId());
        }
        //3.2 query last answer by id
        Map<Long, InteractionReply> replyMap = new HashMap<>(answerIds.size());
        answerIds.remove(null);
        if (CollUtils.isNotEmpty(answerIds)) {
            List<InteractionReply> replies = replyService.listByIds(answerIds);

            for (InteractionReply reply : replies) {
                replyMap.put(reply.getUserId(), reply);
                if (!reply.getAnonymity()) {
                    userIds.add(reply.getUserId());
                }
            }
        }


        //3.3 query user info by id
        Map<Long, UserDTO> userMap = new HashMap<>(userIds.size());
        userIds.remove(null);
        if (CollUtils.isNotEmpty(userIds)) {
            List<UserDTO> users = userClient.queryUserByIds(userIds);
             userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, user -> user));
        }

        // vo
        List<QuestionVO> voList = new ArrayList<>(records.size());
        for (InteractionQuestion r : records) {
            QuestionVO vo = BeanUtils.copyBean(r, QuestionVO.class);
            voList.add(vo);
            if (!r.getAnonymity()) {
                UserDTO userDTO = userMap.get(r.getUserId());
                if (userDTO != null) {
                    vo.setUserName(userDTO.getUsername());
                    vo.setUserIcon(userDTO.getIcon());
                }
            }


            InteractionReply reply = replyMap.get(r.getLatestAnswerId());
            if (reply != null) {
                vo.setLatestReplyContent(reply.getContent());

                if (!reply.getAnonymity()) {
                    UserDTO user = userMap.get(reply.getUserId());
                    vo.setLatestReplyUser(user.getUsername());
                }

            }

        }
        return PageDTO.of(page, voList);
    }

    @Override
    public QuestionVO queryQuestionById(Long id) {

        // 1.query by id
        InteractionQuestion question = getById(id);

        if (question == null || question.getHidden()) {
            return null;
        }
        UserDTO user = null;
        if (!question.getAnonymity()) {
            user = userClient.queryUserById(question.getUserId());
        }

        // po to vo
        QuestionVO vo = BeanUtils.copyBean(question, QuestionVO.class);
        if (user != null) {
            vo.setUserName(user.getUsername());
            vo.setUserIcon(user.getIcon());
        }
        return vo;
    }

    @Override
    public PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query) {
        //1,course name, get course id
        List<Long> courseIds = null;
        if (StringUtils.isNotBlank(query.getCourseName())){
            courseIds = searchClient.queryCoursesIdByName(query.getCourseName());
            if(CollUtils.isEmpty(courseIds)){
                return PageDTO.empty(0L,0L);
            }
        }
        Integer status = query.getStatus();
        LocalDateTime begin = query.getBeginTime();
        LocalDateTime end = query.getEndTime();

        //2. page query
        Page<InteractionQuestion> page = lambdaQuery()
                .in(courseIds != null, InteractionQuestion::getCourseId, courseIds)
                .eq(status != null, InteractionQuestion::getStatus, status)
                .gt(begin != null, InteractionQuestion::getCreateTime, begin)
                .lt(end != null, InteractionQuestion::getCreateTime, end)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());

        List<InteractionQuestion> records = page.getRecords();
        if(CollUtils.isEmpty(records)){
            return PageDTO.empty(page);
        }

        //3. vo
        Set<Long> userIds = new HashSet<>();
        Set<Long> cIds = new HashSet<>();
        Set<Long> cataIds = new HashSet<>();
        for (InteractionQuestion q : records) {
            userIds.add(q.getUserId());
            cIds.add(q.getCourseId());
            cataIds.add(q.getSectionId());
            cataIds.add(q.getChapterId());
        }
        // hey id set
        //get user by id

        List<UserDTO> users = userClient.queryUserByIds(userIds);
        Map<Long, UserDTO> userMap = new HashMap<>(userIds.size());
        if (CollUtils.isNotEmpty(users)) {
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, user -> user));
        }
        // get course by id

        List<CourseSimpleInfoDTO> cInfos = courseClient.getSimpleInfoList(cIds);
        Map<Long, CourseSimpleInfoDTO> cInfoMap = new HashMap<>(cInfos.size());
        if (CollUtils.isNotEmpty(cInfos)) {
            cInfoMap = cInfos.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        }
        // get sect ion by id

        List<CataSimpleInfoDTO> catas = catalogueClient.batchQueryCatalogue(cataIds);
        Map<Long, String> cataMap = new HashMap<>(catas.size());
        if (CollUtils.isNotEmpty(catas)) {
            cataMap = catas.stream().collect(Collectors.toMap(CataSimpleInfoDTO::getId,CataSimpleInfoDTO::getName));
        }


        // vo
        List <QuestionAdminVO> voList = new ArrayList<>(records.size());
        for (InteractionQuestion q : records) {
            // po to vo
            QuestionAdminVO vo = BeanUtils.copyBean(q, QuestionAdminVO.class);
            voList.add(vo);

            UserDTO user = userMap.get(q.getUserId());
            if (user != null) {
                vo.setUserName(user.getUsername());
            }

            CourseSimpleInfoDTO cInfo = cInfoMap.get(q.getCourseId());
            if (cInfo != null) {
                vo.setCourseName(cInfo.getName());

                vo.setCategoryName( categoryCache.getCategoryNames(cInfo.getCategoryIds()));

            }

            vo.setChapterName(cataMap.getOrDefault(q.getChapterId(),""));
            vo.setSectionName(cataMap.getOrDefault(q.getSectionId(),""));
        }
        return PageDTO.of(page, voList);
    }
}
