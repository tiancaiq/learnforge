package com.learnforge.message.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.dto.SmsThirdPlatformDTO;
import com.learnforge.message.domain.dto.SmsThirdPlatformFormDTO;
import com.learnforge.message.domain.query.SmsThirdPlatformPageQuery;
import com.learnforge.message.service.ISmsThirdPlatformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Third-party cloud communication platform frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Api(tags = "SMS platform management interface")
@RestController
@RequestMapping("/sms-platforms")
@RequiredArgsConstructor
public class SmsThirdPlatformController {

    private final ISmsThirdPlatformService smsThirdPlatformService;

    @PostMapping
    @ApiOperation("Add SMS platform information")
    public Long saveSmsThirdPlatform(@RequestBody SmsThirdPlatformFormDTO smsThirdPlatformDTO){
        return smsThirdPlatformService.saveSmsThirdPlatform(smsThirdPlatformDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update SMS platform information")
    public void updateSmsThirdPlatform(
            @RequestBody SmsThirdPlatformFormDTO smsThirdPlatformDTO,
            @ApiParam(value = "SMS platform id", example = "1") @PathVariable("id") Long id){
        smsThirdPlatformDTO.setId(id);
        smsThirdPlatformService.updateSmsThirdPlatform(smsThirdPlatformDTO);
    }

    @GetMapping
    @ApiOperation("Page query SMS platform information")
    public PageDTO<SmsThirdPlatformDTO> querySmsThirdPlatforms(SmsThirdPlatformPageQuery pageQuery){
        return smsThirdPlatformService.querySmsThirdPlatforms(pageQuery);
    }

    @GetMapping("/{id}")
    @ApiOperation("Query SMS platform information by id")
    public SmsThirdPlatformDTO querySmsThirdPlatform(
            @ApiParam(value = "SMS platform id", example = "1") @PathVariable("id") Long id){
        return smsThirdPlatformService.querySmsThirdPlatform(id);
    }
}
