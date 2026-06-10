package com.learnforge.pay.controller;


import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.pay.sdk.constants.PayErrorInfo;
import com.learnforge.pay.sdk.constants.PayType;
import com.learnforge.pay.sdk.dto.PayApplyDTO;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.pay.service.IPayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Payment order frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Api(tags = "Payment-related interfaces")
@RestController
@RequestMapping("/pay-orders")
@RequiredArgsConstructor
public class PayOrderController {

    private final IPayOrderService payOrderService;

    @ApiOperation("Scan code payment application payment order, return payment URL address, used for generating QR code")
    @PostMapping
    public String applyPayOrder(@RequestBody PayApplyDTO payApplyDTO){
        if(!PayType.NATIVE.equalsValue(payApplyDTO.getPayType())){
            throw new BadRequestException(PayErrorInfo.INVALID_PAY_TYPE);
        }
        return payOrderService.applyPayOrder(payApplyDTO);
    }

    @ApiOperation("Query payment result by business order id")
    @GetMapping("/{bizOrderId}/status")
    public PayResultDTO queryPayResult(
            @ApiParam("Business order id") @PathVariable("bizOrderId") Long bizOrderId
    ){
        return payOrderService.queryPayResult(bizOrderId);
    }
}
