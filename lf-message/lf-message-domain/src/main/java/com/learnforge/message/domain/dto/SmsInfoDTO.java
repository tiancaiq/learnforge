package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(description = "SMS sending parameters")
public class SmsInfoDTO {
    @ApiModelProperty("Template code")
    private String templateCode;
    @ApiModelProperty("Phone number")
    private Iterable<String> phones;
    @ApiModelProperty("Template parameters")
    private Map<String, String> templateParams;
}
