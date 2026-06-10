package com.learnforge.pay.tasks;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.domain.po.RefundOrder;
import com.learnforge.pay.service.IRefundOrderService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundOrderCheckTask {

    private final IRefundOrderService refundOrderService;

    @XxlJob("refundOrderCheckHandler")
    public void checkRefundOrderStatus() {
        // 1. Get Shard Information
        int index = XxlJobHelper.getShardIndex() + 1;
        String jobParam = XxlJobHelper.getJobParam();
        int size = StringUtils.isNumeric(jobParam) ? Integer.parseInt(jobParam) : 10;
        // 2. Query Refund Orders to Process
        PageDTO<RefundOrder> result = refundOrderService.queryRefundingOrderByPage(index, size);
        if (result.isEmpty()) {
            return;
        }
        // 3. Check Each Refund Order Individually
        for (RefundOrder refundOrder : result.getList()) {
            try {
                refundOrderService.checkRefundOrder(refundOrder);
            } catch (Exception e) {
                log.error("Processing Refund Order Status Exception:", e);
            }
        }
    }
}
