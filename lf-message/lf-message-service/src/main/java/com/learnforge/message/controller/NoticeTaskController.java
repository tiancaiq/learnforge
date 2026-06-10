package com.learnforge.message.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.dto.NoticeTaskDTO;
import com.learnforge.message.domain.dto.NoticeTaskFormDTO;
import com.learnforge.message.domain.query.NoticeTaskPageQuery;
import com.learnforge.message.service.INoticeTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * System announcement task table, can be delayed or scheduled for sending announcements frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Api(tags = "SMS task management interface")
@RestController
@RequestMapping("/notice-tasks")
@RequiredArgsConstructor
public class NoticeTaskController {

    private final INoticeTaskService noticeTaskService;

    @PostMapping
    @ApiOperation("Add notification task")
    public Long saveNoticeTask(@RequestBody NoticeTaskFormDTO noticeTaskFormDTO){
        return noticeTaskService.saveNoticeTask(noticeTaskFormDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update notification task")
    public void updateNoticeTask(
            @RequestBody NoticeTaskFormDTO noticeTaskFormDTO,
            @ApiParam(value = "Task id", example = "1") @PathVariable("id") Long id){
        noticeTaskFormDTO.setId(id);
        noticeTaskService.updateNoticeTask(noticeTaskFormDTO);
    }

    @GetMapping
    @ApiOperation("Page query notification task")
    public PageDTO<NoticeTaskDTO> queryNoticeTasks(NoticeTaskPageQuery pageQuery){
        return noticeTaskService.queryNoticeTasks(pageQuery);
    }

    @GetMapping("/{id}")
    @ApiOperation("Query task by id")
    public NoticeTaskDTO queryNoticeTask(@ApiParam(value = "Task id", example = "1") @PathVariable("id") Long id){
        return noticeTaskService.queryNoticeTask(id);
    }
}
