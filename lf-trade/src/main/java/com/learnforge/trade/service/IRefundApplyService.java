package com.learnforge.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;
import com.learnforge.trade.domain.dto.ApproveFormDTO;
import com.learnforge.trade.domain.dto.RefundCancelDTO;
import com.learnforge.trade.domain.dto.RefundFormDTO;
import com.learnforge.trade.domain.po.RefundApply;
import com.learnforge.trade.domain.query.RefundApplyPageQuery;
import com.learnforge.trade.domain.vo.RefundApplyPageVO;
import com.learnforge.trade.domain.vo.RefundApplyVO;

import java.util.List;

/**
 * <p>
 * Refund application service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
public interface IRefundApplyService extends IService<RefundApply> {

    List<RefundApply> queryByDetailId(Long id);

    void applyRefund(RefundFormDTO refundFormDTO);

    PageDTO<RefundApplyPageVO> queryRefundApplyByPage(RefundApplyPageQuery pageQuery);

    RefundApplyVO queryRefundDetailById(Long id);

    RefundApplyVO nextRefundApplyToApprove();

    void approveRefundApply(ApproveFormDTO approveDTO);

    void cancelRefundApply(RefundCancelDTO cancelDTO);

    RefundApplyVO queryRefundDetailByDetailId(Long id);

    void handleRefundResult(RefundResultDTO refundResult);

    List<RefundApply> queryApplyToSend(int page, int size);

    void sendRefundRequest(RefundApply refundApply);

    boolean checkRefundStatus(RefundApply refundApply);
}
