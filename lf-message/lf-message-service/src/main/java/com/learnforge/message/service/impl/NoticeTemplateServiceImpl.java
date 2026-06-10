package com.learnforge.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.message.constants.MessageErrorInfo;
import com.learnforge.message.domain.dto.MessageTemplateFormDTO;
import com.learnforge.message.domain.dto.NoticeTemplateDTO;
import com.learnforge.message.domain.dto.NoticeTemplateFormDTO;
import com.learnforge.message.domain.po.MessageTemplate;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.query.NoticeTemplatePageQuery;
import com.learnforge.message.enums.TemplateStatus;
import com.learnforge.message.mapper.NoticeTemplateMapper;
import com.learnforge.message.service.IMessageTemplateService;
import com.learnforge.message.service.INoticeTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Notification template service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Service
@RequiredArgsConstructor
public class NoticeTemplateServiceImpl extends ServiceImpl<NoticeTemplateMapper, NoticeTemplate> implements INoticeTemplateService {

    private final IMessageTemplateService messageTemplateService;

    @Override
    @Transactional
    public Long saveNoticeTemplate(NoticeTemplateFormDTO noticeTemplateFormDTO) {
        // 1. Save notification template
        NoticeTemplate noticeTemplate = BeanUtils.copyBean(noticeTemplateFormDTO, NoticeTemplate.class);
        save(noticeTemplate);
        Long id = noticeTemplate.getId();
        // 2. Check if there is an SMS template
        if(!noticeTemplate.getIsSmsTemplate()){
            return id;
        }
        // 3. Save SMS template
        List<MessageTemplateFormDTO> messageTemplateDTOs = noticeTemplateFormDTO.getMessageTemplates();
        List<MessageTemplate> messageTemplates = new ArrayList<>();
        for (MessageTemplateFormDTO dto : messageTemplateDTOs) {
            MessageTemplate template = BeanUtils.copyBean(dto, MessageTemplate.class);
            template.setTemplateId(id);
            template.setName(noticeTemplate.getName());
            template.setContent(noticeTemplate.getContent());
            messageTemplates.add(template);
        }
        messageTemplateService.saveBatch(messageTemplates);
        return id;
    }

    @Override
    @Transactional
    public void updateNoticeTemplate(NoticeTemplateFormDTO noticeTemplateDTO) {
        // 1. Query old data
        Long id = noticeTemplateDTO.getId();
        NoticeTemplate oldNT = getById(id);
        if (oldNT == null) {
            throw new BadRequestException(MessageErrorInfo.NOTICE_TEMPLATE_NOT_EXISTS);
        }
        // 2. First update notification template data
        NoticeTemplate noticeTemplate = BeanUtils.copyBean(noticeTemplateDTO, NoticeTemplate.class);
        updateById(noticeTemplate);
        // 3. Whether to delete SMS template
        List<Long> deleteMessageTemplates = noticeTemplateDTO.getDeleteMessageTemplates();
        if(CollUtils.isNotEmpty(deleteMessageTemplates)){
            messageTemplateService.removeByIds(deleteMessageTemplates);
        }
        // 4. Whether there are new or modified SMS templates
        List<MessageTemplateFormDTO> templateDTOS = noticeTemplateDTO.getMessageTemplates();
        if(CollUtils.isEmpty(templateDTOS)){
            return;
        }
        // 5. Data processing
        List<MessageTemplate> saveList = new ArrayList<>();
        List<MessageTemplate> updateList = new ArrayList<>();
        // 5.1. First query which platforms have SMS templates for the current notification template
        List<MessageTemplate> list = messageTemplateService.queryByNoticeTemplateId(id);
        Map<String, Long> mtMap = null;
        if(CollUtils.isNotEmpty(list)){
            mtMap = list.stream().collect(Collectors.toMap(MessageTemplate::getPlatformCode, MessageTemplate::getId));
        }
        // 5.2. Check if it is a new or modified SMS template
        noticeTemplate = getById(id);
        for (MessageTemplateFormDTO dto : templateDTOS) {
            MessageTemplate template = BeanUtils.copyBean(dto, MessageTemplate.class);
            template.setTemplateId(id);
            template.setName(noticeTemplate.getName());
            template.setContent(noticeTemplate.getContent());
            if(template.getId() == null){
                // Check if the current SMS template already exists
                if(mtMap != null && mtMap.containsKey(template.getPlatformCode())){
                    // If it already exists, change to update
                    template.setId(mtMap.get(template.getPlatformCode()));
                    updateList.add(template);
                }else {
                    // If it does not exist, add it
                    saveList.add(template);
                }
            }else{
                updateList.add(template);
            }
        }
        // 5.3. Add SMS template
        if(CollUtils.isNotEmpty(saveList)) {
            messageTemplateService.saveBatch(saveList);
        }
        // 5.4. Update SMS, template
        if(CollUtils.isNotEmpty(updateList)) {
            messageTemplateService.updateBatchById(updateList);
        }
    }

    @Override
    public PageDTO<NoticeTemplateDTO> queryNoticeTemplates(NoticeTemplatePageQuery query) {
        // 1. Pagination conditions
        Page<NoticeTemplate> page = query.toMpPage();
        // 2. Filter conditions
        page = lambdaQuery()
                .eq(query.getStatus() != null, NoticeTemplate::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getKeyword()), NoticeTemplate::getName, query.getKeyword())
                .page(page);
        // 3. Data conversion
        return PageDTO.of(page, NoticeTemplateDTO.class);
    }

    @Override
    public NoticeTemplateDTO queryNoticeTemplate(Long id) {
        return BeanUtils.copyBean( getById(id), NoticeTemplateDTO.class);
    }

    @Override
    public NoticeTemplate queryByCode(String code) {
        return lambdaQuery()
                .eq(NoticeTemplate::getCode, code)
                .eq(NoticeTemplate::getStatus, TemplateStatus.IN_SERVICE.getValue())
                .one();
    }
}
