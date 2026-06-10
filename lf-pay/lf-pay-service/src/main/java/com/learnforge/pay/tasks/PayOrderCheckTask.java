package com.learnforge.pay.tasks;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.domain.po.PayOrder;
import com.learnforge.pay.service.IPayOrderService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderCheckTask {

    private final IPayOrderService payOrderService;

    @XxlJob("payOrderCheckHandler")
    public void checkPayOrderStatus() {
        // 1. Get Shard Information
        int index = XxlJobHelper.getShardIndex() + 1;
        String jobParam = XxlJobHelper.getJobParam();
        int size = StringUtils.isNumeric(jobParam) ? Integer.parseInt(jobParam) : 10;
        // 2. Query Payment Orders to Process
        PageDTO<PayOrder> result = payOrderService.queryPayingOrderByPage(index, size);
        if (result.isEmpty()) {
            return;
        }
        // 3. Check Each Payment Order Individually
        for (PayOrder payOrder : result.getList()) {
            try {
                payOrderService.checkPayOrder(payOrder);
            } catch (Exception e) {
                log.error("Processing Order Payment Status Exception:", e);
            }
        }
    }
}
