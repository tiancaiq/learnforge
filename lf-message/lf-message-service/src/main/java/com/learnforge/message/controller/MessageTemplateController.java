package com.learnforge.message.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.dto.MessageTemplateDTO;
import com.learnforge.message.domain.dto.MessageTemplateFormDTO;
import com.learnforge.message.domain.query.MessageTemplatePageQuery;
import com.learnforge.message.service.IMessageTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Third-party SMS platform template information management frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Api(tags = "SMS template management interface")
@RestController
@RequestMapping("/message-templates")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final IMessageTemplateService messageTemplateService;

    @PostMapping
    @ApiOperation("Add SMS template")
    public Long saveMessageTemplate(@RequestBody MessageTemplateFormDTO messageTemplateDTO){
        return messageTemplateService.saveMessageTemplate(messageTemplateDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update SMS template")
    public void updateMessageTemplate(
            @RequestBody MessageTemplateFormDTO messageTemplateDTO,
            @ApiParam(value = "SMS template id", example = "1") @PathVariable("id") Long id){
        messageTemplateDTO.setId(id);
        messageTemplateService.updateMessageTemplate(messageTemplateDTO);
    }

    @GetMapping
    @ApiOperation("Page query SMS template")
    public PageDTO<MessageTemplateDTO> queryMessageTemplates(MessageTemplatePageQuery pageQuery){
        return messageTemplateService.queryMessageTemplates(pageQuery);
    }

    @GetMapping("/{id}")
    @ApiOperation("Query SMS template by id")
    public MessageTemplateDTO queryMessageTemplate(@ApiParam(value = "Template id", example = "1") @PathVariable("id") Long id){
        return messageTemplateService.queryMessageTemplate(id);
    }
}
