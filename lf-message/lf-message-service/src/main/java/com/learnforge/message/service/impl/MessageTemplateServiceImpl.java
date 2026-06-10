package com.learnforge.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.message.domain.dto.MessageTemplateDTO;
import com.learnforge.message.domain.dto.MessageTemplateFormDTO;
import com.learnforge.message.domain.query.MessageTemplatePageQuery;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.message.domain.po.MessageTemplate;
import com.learnforge.message.mapper.MessageTemplateMapper;
import com.learnforge.message.service.IMessageTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Third-party SMS platform signature and template information service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements IMessageTemplateService {

    @Override
    public List<MessageTemplate> queryByNoticeTemplateId(Long templateId) {
        return lambdaQuery()
                .eq(MessageTemplate::getTemplateId, templateId)
                .list();
    }

    @Override
    public Long saveMessageTemplate(MessageTemplateFormDTO messageTemplateDTO) {
        // 1. Data conversion
        MessageTemplate messageTemplate = BeanUtils.copyBean(messageTemplateDTO, MessageTemplate.class);
        // 2. Add new
        save(messageTemplate);
        return messageTemplate.getId();
    }

    @Override
    public void updateMessageTemplate(MessageTemplateFormDTO messageTemplateDTO) {
        // 1. Data conversion
        MessageTemplate messageTemplate = BeanUtils.copyBean(messageTemplateDTO, MessageTemplate.class);
        // 2. Add new
        updateById(messageTemplate);
    }

    @Override
    public PageDTO<MessageTemplateDTO> queryMessageTemplates(MessageTemplatePageQuery query) {
        // 1. Pagination conditions
        Page<MessageTemplate> page = query.toMpPage();
        // 2. Filter conditions
        page = lambdaQuery()
                .eq(query.getStatus() != null, MessageTemplate::getStatus, query.getStatus())
                .eq(query.getThirdPlatformId() != null, MessageTemplate::getPlatformCode, query.getThirdPlatformId())
                .like(StringUtils.isNotBlank(query.getKeyword()), MessageTemplate::getName, query.getKeyword())
                .page(page);
        // 3. Data conversion
        return PageDTO.of(page, MessageTemplateDTO.class);
    }

    @Override
    public MessageTemplateDTO queryMessageTemplate(Long id) {
        return BeanUtils.copyBean( getById(id), MessageTemplateDTO.class);
    }
}
