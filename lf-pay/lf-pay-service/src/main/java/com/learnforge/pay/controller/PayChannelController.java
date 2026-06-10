package com.learnforge.pay.controller;


import com.learnforge.common.utils.BeanUtils;
import com.learnforge.pay.sdk.dto.PayChannelDTO;
import com.learnforge.pay.service.IPayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * Payment channel frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Api(tags = "Payment-related interfaces")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay-channels")
public class PayChannelController {

    private final IPayChannelService channelService;

    @ApiOperation("Query payment channel list")
    @GetMapping("/list")
    public List<PayChannelDTO> listAllPayChannels(){
        return BeanUtils.copyList(channelService.list(), PayChannelDTO.class);
    }

    @ApiOperation("Add payment channel")
    @PostMapping
    public Long addPayChannel(@Valid @RequestBody PayChannelDTO channelDTO){
        return channelService.addPayChannel(channelDTO);
    }

    @ApiOperation("Modify payment channel")
    @PutMapping("/{id}")
    public void updatePayChannel(
            @ApiParam("Payment channel id") @PathVariable("id") Long id,
            @RequestBody PayChannelDTO channelDTO){
        channelDTO.setId(id);
        channelService.updatePayChannel(channelDTO);
    }
}
