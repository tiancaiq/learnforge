package com.learnforge.pay.service;

import com.learnforge.pay.sdk.dto.PayChannelDTO;
import com.learnforge.pay.domain.po.PayChannel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * Payment Channel Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
public interface IPayChannelService extends IService<PayChannel> {

    Long addPayChannel(PayChannelDTO channelDTO);

    void updatePayChannel(PayChannelDTO channelDTO);
}
