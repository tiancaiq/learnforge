package com.learnforge.trade.service;

import com.learnforge.trade.domain.dto.OrderDelayQueryDTO;
import com.learnforge.trade.domain.dto.PayApplyFormDTO;
import com.learnforge.trade.domain.vo.PayChannelVO;

import java.util.List;

public interface IPayService {
    List<PayChannelVO> queryPayChannels();

    String applyPayOrder(PayApplyFormDTO payApply);

    void queryPayResult(OrderDelayQueryDTO message);
}
