package com.learnforge.trade.handler;

import com.learnforge.trade.domain.po.RefundApply;
import com.learnforge.trade.service.IRefundApplyService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RefundJobHandler {

    private final IRefundApplyService refundApplyService;

    @XxlJob("refundRequestJobHandler")
    public void handleRefundRequest(){
        // 1. Get shard information as page number, query up to 2 items per page to avoid frequent refund applications
        int index = XxlJobHelper.getShardIndex() + 1;
        int size = 2;
        // 2. Pagination query for approved refund applications
        List<RefundApply> list = refundApplyService.queryApplyToSend(index, size);
        // 3. Loop process refund applications
        for (RefundApply refundApply : list) {
            // 3.1. Check refund status, whether the refund has ended
            boolean refundFinished = refundApplyService.checkRefundStatus(refundApply);
            if(refundFinished){
                continue;
            }
            // 3.2. Send refund application
            refundApplyService.sendRefundRequest(refundApply);
        }
    }
}
