package com.learnforge.trade.controller;

import com.learnforge.trade.domain.dto.PayApplyFormDTO;
import com.learnforge.trade.domain.vo.PayChannelVO;
import com.learnforge.trade.service.IPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Payment-related interfaces")
@RestController
@RequestMapping("pay")
@RequiredArgsConstructor
public class PayController {

    private final IPayService payService;

    @PostMapping("/order")
    @ApiOperation(value = "Payment Application, Return Payment QR Code URL")
    public String applyPayOrder(@RequestBody PayApplyFormDTO payApply) {
        return payService.applyPayOrder(payApply);
    }

    @GetMapping("/channels")
    @ApiOperation("Get Payment Channel List Interface")
    public List<PayChannelVO> queryPayChannels() {
        return payService.queryPayChannels();
    }

}