package com.learnforge.message.service;

import com.learnforge.api.dto.sms.SmsInfoDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.message.domain.po.NoticeTemplate;

import java.util.List;

public interface ISmsService {
    void sendMessageByTemplate(NoticeTemplate noticeTemplate, List<UserDTO> users);

    void sendMessage(SmsInfoDTO smsInfoDTO);

    void sendMessageAsync(SmsInfoDTO smsInfoDTO);
}
