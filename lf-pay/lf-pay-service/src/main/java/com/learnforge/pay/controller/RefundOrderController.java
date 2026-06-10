package com.learnforge.pay.controller;


import com.learnforge.pay.sdk.dto.RefundApplyDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;
import com.learnforge.pay.service.IRefundOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Refund order frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@RestController
@RequestMapping("/refund-orders")
@RequiredArgsConstructor
@Api(tags = "Payment-related interfaces")
public class RefundOrderController {

    private final IRefundOrderService refundOrderService;

    @PostMapping
    @ApiOperation("Refund application interface")
    public RefundResultDTO applyRefund(@RequestBody RefundApplyDTO refundApplyDTO) {
        return refundOrderService.applyRefund(refundApplyDTO);
    }

    @GetMapping("{bizRefundOrderId}/status")
    @ApiOperation("Query refund result")
    public RefundResultDTO queryRefundResult(
            @ApiParam("Business-side refund sub-order id") @PathVariable("bizRefundOrderId") Long bizRefundOrderId) {
        return refundOrderService.queryRefundResult(bizRefundOrderId);
    }

/*    @GetMapping("{bizRefundOrderId}/detail")
    @ApiOperation("Query refund details")
    public RefundResultDTO queryRefundDetail(
            @ApiParam("Business-side refund sub-order id") @PathVariable("bizRefundOrderId") Long bizRefundOrderId) {
        return refundOrderService.queryRefundDetail(bizRefundOrderId);
    }*/
}
