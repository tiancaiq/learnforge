package com.learnforge.pay.sdk.client;

import com.learnforge.pay.sdk.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("pay-service")
public interface PayClient {
    /**
     * Query payment channels
     * @return payment channel list
     */
    @GetMapping("/pay-channels/list")
    List<PayChannelDTO> listAllPayChannels();
    /**
     * Scan code payment application payment order, return payment URL address, used for generating QR code
     *
     * @param payApplyDTO parameter information for payment order application
     * @return payment link, needs to be generated into QR code by frontend
     */
    @PostMapping("/pay-orders")
    String applyPayOrder(@RequestBody PayApplyDTO payApplyDTO);

    /**
     * Query payment result by business order id
     *
     * @param bizOrderId business order id
     * @return payment result
     */
    @GetMapping("/pay-orders/{bizOrderId}/status")
    PayResultDTO queryPayResult(@PathVariable("bizOrderId") Long bizOrderId);

    /**
     * Refund application interface
     *
     * @param refundApplyDTO refund parameters
     * @return refund result
     */
    @PostMapping("/refund-orders")
    RefundResultDTO applyRefund(@RequestBody RefundApplyDTO refundApplyDTO);

    /**
     * Query refund result
     *
     * @param bizRefundOrderId order id to refund
     * @return refund result
     */
    @GetMapping("/refund-orders/{bizRefundOrderId}/status")
    RefundResultDTO queryRefundResult(@PathVariable("bizRefundOrderId") Long bizRefundOrderId);
}
