package com.learnforge.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tj.trade")
public class TradeProperties {
    /**
     * Maximum number of courses allowed to purchase at once
     */
    private int maxCourseAmount = 10;
    /**
     * Maximum waiting time for order payment, unit minutes
     */
    private int payOrderTTLMinutes = 30;
}
