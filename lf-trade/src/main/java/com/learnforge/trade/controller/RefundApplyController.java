package com.learnforge.trade.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.trade.domain.dto.ApproveFormDTO;
import com.learnforge.trade.domain.dto.RefundCancelDTO;
import com.learnforge.trade.domain.dto.RefundFormDTO;
import com.learnforge.trade.domain.query.RefundApplyPageQuery;
import com.learnforge.trade.domain.vo.RefundApplyPageVO;
import com.learnforge.trade.domain.vo.RefundApplyVO;
import com.learnforge.trade.service.IRefundApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * Refund Application Frontend Controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Api(tags = "Refund Related Interfaces")
@RequiredArgsConstructor
@RestController
@RequestMapping("/refund-apply")
public class RefundApplyController {

    private final IRefundApplyService refundApplyService;

    @ApiOperation("Refund Application")
    @PostMapping
    public void applyRefund(@Valid @RequestBody RefundFormDTO refundFormDTO) {
        refundApplyService.applyRefund(refundFormDTO);
    }

    @ApiOperation("Approve Refund Application")
    @PutMapping("/approval")
    public void approveRefundApply(@Valid @RequestBody ApproveFormDTO approveDTO){
        refundApplyService.approveRefundApply(approveDTO);
    }

    @ApiOperation("Cancel Refund Application")
    @PutMapping("/cancel")
    public void cancelRefundApply(@Valid @RequestBody RefundCancelDTO cancelDTO){
        refundApplyService.cancelRefundApply(cancelDTO);
    }

    @ApiOperation("Page Query Refund Applications")
    @GetMapping("/page")
    public PageDTO<RefundApplyPageVO> queryRefundApplyByPage(RefundApplyPageQuery pageQuery){
        return refundApplyService.queryRefundApplyByPage(pageQuery);
    }

    @ApiOperation("Query Refund Details by ID")
    @GetMapping("/{id}")
    public RefundApplyVO queryRefundDetailById(@ApiParam("Refund ID") @PathVariable("id") Long id){
        return refundApplyService.queryRefundDetailById(id);
    }

    @ApiOperation("Query Refund Details by Sub Order ID")
    @GetMapping("/detail/{id}")
    public RefundApplyVO queryRefundDetailByDetailId(@ApiParam("Sub Order ID") @PathVariable("id") Long detailId){
        return refundApplyService.queryRefundDetailByDetailId(detailId);
    }

    @ApiOperation("Query Next Pending Approval Refund Application")
    @GetMapping("/next")
    public RefundApplyVO nextRefundApplyToApprove(){
        return refundApplyService.nextRefundApplyToApprove();
    }
}
