package com.learnforge.api.dto.sms;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(description = "SMS sending parameters")
public class SmsInfoDTO {
    private String templateCode;
    private Iterable<String> phones;
    private Map<String, String> templateParams;
}
