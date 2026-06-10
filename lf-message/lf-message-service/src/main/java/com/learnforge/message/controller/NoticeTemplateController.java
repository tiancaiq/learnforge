package com.learnforge.message.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.dto.NoticeTemplateDTO;
import com.learnforge.message.domain.dto.NoticeTemplateFormDTO;
import com.learnforge.message.domain.query.NoticeTemplatePageQuery;
import com.learnforge.message.service.INoticeTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Notification template frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@RestController
@RequestMapping("/notice-templates")
@Api(tags = "Notification template management interface")
@RequiredArgsConstructor
public class NoticeTemplateController {

    private final INoticeTemplateService noticeTemplateService;

    @PostMapping
    @ApiOperation("Add notification template")
    public Long saveNoticeTemplate(@RequestBody NoticeTemplateFormDTO noticeTemplateFormDTO){
        return noticeTemplateService.saveNoticeTemplate(noticeTemplateFormDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update notification template")
    public void updateNoticeTemplate(
            @RequestBody NoticeTemplateFormDTO noticeTemplateFormDTO,
            @ApiParam(value = "Template id", example = "1") @PathVariable("id") Long id){
        noticeTemplateFormDTO.setId(id);
        noticeTemplateService.updateNoticeTemplate(noticeTemplateFormDTO);
    }

    @GetMapping
    @ApiOperation("Page query notification template")
    public PageDTO<NoticeTemplateDTO> queryNoticeTemplates(NoticeTemplatePageQuery pageQuery){
        return noticeTemplateService.queryNoticeTemplates(pageQuery);
    }

    @GetMapping("/{id}")
    @ApiOperation("Query template by id")
    public NoticeTemplateDTO queryNoticeTemplate(@ApiParam(value = "Template id", example = "1") @PathVariable("id") Long id){
        return noticeTemplateService.queryNoticeTemplate(id);
    }
}
