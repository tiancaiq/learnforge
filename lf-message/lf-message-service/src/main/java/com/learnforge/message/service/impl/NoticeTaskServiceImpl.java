package com.learnforge.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.MarkedRunnable;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.message.constants.MessageErrorInfo;
import com.learnforge.message.domain.dto.NoticeTaskDTO;
import com.learnforge.message.domain.dto.NoticeTaskFormDTO;
import com.learnforge.message.domain.po.NoticeTask;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.query.NoticeTaskPageQuery;
import com.learnforge.message.enums.TemplateStatus;
import com.learnforge.message.mapper.NoticeTaskMapper;
import com.learnforge.message.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * <p>
 * System announcement task table, which can be delayed or sent periodically service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeTaskServiceImpl extends ServiceImpl<NoticeTaskMapper, NoticeTask> implements INoticeTaskService {

    private final Executor asyncNoticeExecutor;
    private final INoticeTemplateService noticeTemplateService;
    private final UserClient userClient;
    private final IPublicNoticeService publicNoticeService;
    private final IUserInboxService inboxService;
    private final ISmsService smsService;

    @Override
    public Long saveNoticeTask(NoticeTaskFormDTO noticeTaskFormDTO) {
        // 1. Save task
        NoticeTask noticeTask = BeanUtils.copyBean(noticeTaskFormDTO, NoticeTask.class);
        save(noticeTask);
        Long taskId = noticeTask.getId();
        // 2. Check if there is an execution time
        LocalDateTime pushTime = noticeTask.getPushTime();
        if(pushTime == null || pushTime.isBefore(LocalDateTime.now())){
            // No execution time, or execution time is earlier than current time, execute task immediately
            asyncNoticeExecutor.execute(new MarkedRunnable(() -> handleTask(noticeTask)));
        }
        return taskId;
    }

    @Override
    @Transactional
    public void handleTask(NoticeTask task) {
        // 1. Get the notification template that the task needs to send
        Long templateId = task.getTemplateId();
        NoticeTemplate noticeTemplate = noticeTemplateService.getById(templateId);
        if(noticeTemplate == null){
            // Template does not exist or cannot be used
            log.error("Notification task cannot be executed, template id 【{}】, reason: {}", templateId, MessageErrorInfo.NOTICE_TEMPLATE_NOT_EXISTS);
            return;
        }
        if(noticeTemplate.getStatus() != TemplateStatus.IN_SERVICE.getValue()){
            // Template does not exist or cannot be used
            log.error("Notification task cannot be executed, template id 【{}】, reason: {}", templateId, MessageErrorInfo.NOTICE_TEMPLATE_CANNOT_USE);
            return;
        }
        // 2. Get the target users for the notification
        List<UserDTO> users = null;
        if (task.getPartial()) {
            // For some users, need to query user information
            List<Long> userIds = getBaseMapper().queryTaskTargetByTaskId(task.getId());
            if(CollUtils.isNotEmpty(userIds)){
                users = userClient.queryUserByIds(userIds);
            }
        }

        // 3. Check if it is all users or partial users
        if (CollUtils.isEmpty(users)) {
            // 3.1. All users, directly store in announcement box, pull messages when user views them (pull mode)
            publicNoticeService.saveNoticeOfTemplate(noticeTemplate);
        }else{
            // 3.2. Partial users, need to write into user mailbox
            inboxService.saveNoticeToInbox(noticeTemplate, users);
            // 3.3. Check if SMS notification is needed
            if(noticeTemplate.getIsSmsTemplate()){
                // Need to send SMS notification
                smsService.sendMessageByTemplate(noticeTemplate, users);
            }
        }
        // 4. At this point, the task is completed, update task status
        boolean shouldRepeat = task.getMaxTimes() > 0;
        lambdaUpdate()
                .set(!shouldRepeat, NoticeTask::getFinished, true)
                .set(shouldRepeat, NoticeTask::getPushTime, task.getPushTime().plusMinutes(task.getInterval()))
                .setSql(shouldRepeat, "max_times = max_times - 1")
                .eq(NoticeTask::getId, task.getId())
                .update();
        task = null;
    }

    @Override
    public void updateNoticeTask(NoticeTaskFormDTO noticeTaskFormDTO) {
        NoticeTask noticeTask = BeanUtils.copyBean(noticeTaskFormDTO, NoticeTask.class);
        updateById(noticeTask);
    }

    @Override
    public PageDTO<NoticeTaskDTO> queryNoticeTasks(NoticeTaskPageQuery query) {
        // 1. Pagination conditions
        Page<NoticeTask> page = query.toMpPage();
        // 2. Filter conditions
        page = lambdaQuery()
                .eq(query.getFinished() != null, NoticeTask::getFinished, query.getFinished())
                .like(StringUtils.isNotBlank(query.getKeyword()), NoticeTask::getName, query.getKeyword())
                .ge(query.getMinPushTime() != null, NoticeTask::getPushTime, query.getMinPushTime())
                .le(query.getMaxPushTime() != null, NoticeTask::getPushTime, query.getMaxPushTime())
                .page(page);
        // 3. Data conversion
        return PageDTO.of(page, NoticeTaskDTO.class);
    }

    @Override
    public NoticeTaskDTO queryNoticeTask(Long id) {
        return BeanUtils.copyBean(getById(id), NoticeTaskDTO.class);
    }

    @Override
    public PageDTO<NoticeTask> queryTodoNoticeTaskByPage(int pageNo, int size) {
        // 1. Page query pending release tasks: not completed, release time earlier than current time
        Page<NoticeTask> page = lambdaQuery()
                .eq(NoticeTask::getFinished, false)
                .le(NoticeTask::getPushTime, LocalDateTime.now())
                .page(new Page<>(pageNo, size));
        return PageDTO.of(page);
    }
}
