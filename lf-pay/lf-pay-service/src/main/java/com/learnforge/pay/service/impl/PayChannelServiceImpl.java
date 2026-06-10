package com.learnforge.pay.service.impl;

import com.learnforge.common.utils.BeanUtils;
import com.learnforge.pay.sdk.dto.PayChannelDTO;
import com.learnforge.pay.domain.po.PayChannel;
import com.learnforge.pay.mapper.PayChannelMapper;
import com.learnforge.pay.service.IPayChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Payment Channel Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Service
public class PayChannelServiceImpl extends ServiceImpl<PayChannelMapper, PayChannel> implements IPayChannelService {

    @Override
    public Long addPayChannel(PayChannelDTO channelDTO) {
        // 1. Attribute Conversion
        PayChannel payChannel = BeanUtils.toBean(channelDTO, PayChannel.class);
        // 2. Save
        save(payChannel);
        return payChannel.getId();
    }

    @Override
    public void updatePayChannel(PayChannelDTO channelDTO) {
        // 1. Attribute Conversion
        PayChannel payChannel = BeanUtils.toBean(channelDTO, PayChannel.class);
        // 2. Save
        updateById(payChannel);
    }
}
