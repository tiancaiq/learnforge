package com.learnforge.user.service.impl;

import com.learnforge.message.domain.enums.SmsTemplate;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.RandomUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.message.api.client.AsyncSmsClient;
import com.learnforge.message.domain.dto.SmsInfoDTO;
import com.learnforge.user.service.ICodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.learnforge.api.constants.SmsConstants.VERIFY_CODE_PARAM_NAME;
import static com.learnforge.common.constants.ErrorInfo.Msg.INVALID_VERIFY_CODE;
import static com.learnforge.user.constants.UserConstants.USER_VERIFY_CODE_KEY;
import static com.learnforge.user.constants.UserConstants.USER_VERIFY_CODE_TTL;

@Slf4j
@Service
public class CodeServiceImpl implements ICodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AsyncSmsClient asyncSmsClient;

    @Override
    public void sendVerifyCode(String phone) {
        String key = USER_VERIFY_CODE_KEY + phone;
        // 1. Check if code exists
        String code = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(code)){
            // 2. Generate random verification code
            code = RandomUtils.randomNumbers(4);
            // 3. Save to Redis
            stringRedisTemplate.opsForValue()
                    .set(USER_VERIFY_CODE_KEY + phone, code, USER_VERIFY_CODE_TTL);

        }
        // 4. Send SMS
        log.debug("Send SMS Verification Code: {}", code);
        SmsInfoDTO info = new SmsInfoDTO();
        info.setPhones(CollUtils.singletonList(phone));
        info.setTemplateCode(SmsTemplate.VERIFY_CODE.toString());
        Map<String, String> params = new HashMap<>(1);
        params.put(VERIFY_CODE_PARAM_NAME, code);
        info.setTemplateParams(params);
        asyncSmsClient.sendMessage(info);
    }

    @Override
    public void verifyCode(String phone, String code) {
        String cacheCode = stringRedisTemplate.opsForValue().get(USER_VERIFY_CODE_KEY + phone);
        if (!StringUtils.equals(cacheCode, code)) {
            // Verification code error
            throw new BadRequestException(INVALID_VERIFY_CODE);
        }
    }
}
