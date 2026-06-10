package com.learnforge.message.service;

import com.learnforge.message.domain.dto.MessageTemplateDTO;
import com.learnforge.message.domain.dto.MessageTemplateFormDTO;
import com.learnforge.message.domain.query.MessageTemplatePageQuery;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.po.MessageTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Third-party SMS platform signature and template information service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
public interface IMessageTemplateService extends IService<MessageTemplate> {

    List<MessageTemplate> queryByNoticeTemplateId(Long id);

    Long saveMessageTemplate(MessageTemplateFormDTO messageTemplateDTO);

    void updateMessageTemplate(MessageTemplateFormDTO messageTemplateDTO);

    PageDTO<MessageTemplateDTO> queryMessageTemplates(MessageTemplatePageQuery pageQuery);

    MessageTemplateDTO queryMessageTemplate(Long id);
}
