package com.learnforge.message.service.impl;

import com.learnforge.api.dto.sms.SmsInfoDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.AssertUtils;
import com.learnforge.common.utils.MarkedRunnable;
import com.learnforge.message.constants.MessageErrorInfo;
import com.learnforge.message.domain.po.MessageTemplate;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.po.SmsThirdPlatform;
import com.learnforge.message.service.IMessageTemplateService;
import com.learnforge.message.service.INoticeTemplateService;
import com.learnforge.message.service.ISmsService;
import com.learnforge.message.service.ISmsThirdPlatformService;
import com.learnforge.message.thirdparty.ISmsHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements ISmsService {

    @Resource
    private Map<String, ISmsHandler> smsHandlers;
    private final Executor asyncSmsExecutor;
    private final ISmsThirdPlatformService platformService;
    private final INoticeTemplateService noticeTemplateService;
    private final IMessageTemplateService messageTemplateService;

    @Override
    public void sendMessageByTemplate(NoticeTemplate noticeTemplate, List<UserDTO> users) {
        // 1. Get user phone number
        Set<String> phones = users.stream().map(UserDTO::getCellPhone).collect(Collectors.toSet());
        // 2. Organize data, announcement is default parameterless
        SmsInfoDTO info = new SmsInfoDTO();
        info.setPhones(phones);
        info.setTemplateCode(noticeTemplate.getCode());
        // 3. Send
        sendMessage(info);
    }

    @Override
    public void sendMessage(SmsInfoDTO smsInfoDTO) {
        // 1. Get notification template information
        String code = smsInfoDTO.getTemplateCode();
        NoticeTemplate noticeTemplate = noticeTemplateService.queryByCode(code);
        AssertUtils.isNotNull(noticeTemplate, MessageErrorInfo.NOTICE_TEMPLATE_NOT_EXISTS);
        AssertUtils.isTrue(noticeTemplate.getIsSmsTemplate(), MessageErrorInfo.NOTICE_NOT_MESSAGE_TEMPLATE);
        // 2. Query SMS template
        List<MessageTemplate> messageTemplates = messageTemplateService.queryByNoticeTemplateId(noticeTemplate.getId());
        AssertUtils.isNotEmpty(messageTemplates, MessageErrorInfo.NOTICE_NOT_MESSAGE_TEMPLATE);

        // 3. Sort and filter templates by platform priority
        List<MessageTemplate> sortedTemplates =  sortMessageTemplate(messageTemplates);
        if (sortedTemplates.isEmpty()) {
            throw new CommonException(MessageErrorInfo.NO_SUITABLE_TEMPLATE);
        }
        // 4. Send SMS based on template
        for (MessageTemplate template : sortedTemplates) {
            try {
                ISmsHandler smsHandler = smsHandlers.get(template.getPlatformCode());
                smsHandler.send(smsInfoDTO, template);
                return;
            } catch (Exception e) {
                log.error("SMS sending failed, platform {}, reason {}, retry later", template.getPlatformCode(), e.getMessage(), e);
            }
        }
        log.error("SMS sending failed, all platforms have been tried, give up sending");
    }

    private List<MessageTemplate> sortMessageTemplate(List<MessageTemplate> messageTemplates) {
        // 1. Query platform collection, sort by priority
        List<SmsThirdPlatform> platforms = platformService.queryAllPlatform();
        AssertUtils.isNotEmpty(platforms, MessageErrorInfo.PLATFORM_IS_EMPTY);
        // 2. Organize map with platform code as key and template as value
        Map<String, MessageTemplate> mtMap = messageTemplates.stream()
                .collect(Collectors.toMap(MessageTemplate::getPlatformCode, m -> m));
        // 3. Sort data
        List<MessageTemplate> list = new ArrayList<>(messageTemplates.size());
        for (SmsThirdPlatform platform : platforms) {
            MessageTemplate mt = mtMap.get(platform.getCode());
            if (mt != null) {
                list.add(mt);
            }
        }
        return list;
    }

    @Override
    public void sendMessageAsync(SmsInfoDTO smsInfoDTO) {
        asyncSmsExecutor.execute(
                new MarkedRunnable(() -> this.sendMessage(smsInfoDTO))
        );
    }
}
