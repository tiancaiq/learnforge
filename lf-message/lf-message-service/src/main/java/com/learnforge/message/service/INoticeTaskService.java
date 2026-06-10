package com.learnforge.message.service;

import com.learnforge.message.domain.dto.NoticeTaskDTO;
import com.learnforge.message.domain.dto.NoticeTaskFormDTO;
import com.learnforge.message.domain.query.NoticeTaskPageQuery;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.po.NoticeTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * System announcement task table, which can be delayed or sent periodically service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
public interface INoticeTaskService extends IService<NoticeTask> {

    Long saveNoticeTask(NoticeTaskFormDTO noticeTaskFormDTO);

    void handleTask(NoticeTask noticeTask);

    void updateNoticeTask(NoticeTaskFormDTO noticeTaskFormDTO);

    PageDTO<NoticeTaskDTO> queryNoticeTasks(NoticeTaskPageQuery pageQuery);

    NoticeTaskDTO queryNoticeTask(Long id);

    PageDTO<NoticeTask> queryTodoNoticeTaskByPage(int pageNo, int size);
}
