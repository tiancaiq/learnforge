package com.learnforge.message.handler;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.message.domain.po.NoticeTask;
import com.learnforge.message.service.INoticeTaskService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoticeJobHandler {

    private final INoticeTaskService taskService;

    @XxlJob("publishNoticeJob")
    public void publishNotice(){
        // 1. Get shard parameters as pagination parameters for data query, shard count + 1 as page number, jobParam as page size, default size is 10
        int index = XxlJobHelper.getShardIndex() + 1;
        String jobParam = XxlJobHelper.getJobParam();
        int size = StringUtils.isNumeric(jobParam) ? Integer.parseInt(jobParam) : 10;
        // 2. Query pending tasks: tasks with expired publish time and not yet completed
        PageDTO<NoticeTask> page = taskService.queryTodoNoticeTaskByPage(index, size);
        if (page.isEmpty()) {
            // No pending tasks, end directly
            return;
        }
        // 3. There are pending tasks, proceed
        List<NoticeTask> list = page.getList();
        for (NoticeTask noticeTask : list) {
            taskService.handleTask(noticeTask);
        }
    }
}
