package com.learnforge.message.service;

import com.learnforge.message.domain.dto.SmsThirdPlatformDTO;
import com.learnforge.message.domain.dto.SmsThirdPlatformFormDTO;
import com.learnforge.message.domain.query.SmsThirdPlatformPageQuery;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.po.SmsThirdPlatform;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Third-party cloud communication platform service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
public interface ISmsThirdPlatformService extends IService<SmsThirdPlatform> {

    List<SmsThirdPlatform> queryAllPlatform();

    Long saveSmsThirdPlatform(SmsThirdPlatformFormDTO thirdPlatformDTO);

    void updateSmsThirdPlatform(SmsThirdPlatformFormDTO thirdPlatformDTO);

    PageDTO<SmsThirdPlatformDTO> querySmsThirdPlatforms(SmsThirdPlatformPageQuery query);

    SmsThirdPlatformDTO querySmsThirdPlatform(Long id);
}
