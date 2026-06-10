package com.learnforge.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tj.message")
public class MessageProperties {
    /**
     * Maximum validity period of notification, default 1 month
     */
    private Integer noticeTtlMonths = 1;
    /**
     * Maximum validity period of private message, default 6 months
     */
    private Integer messageTtlMonths = 6;
}
