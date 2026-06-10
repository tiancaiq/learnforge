package com.learnforge.pay.third.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PayStatusResponse {
    private boolean success;
    /**
     * Business Status Code
     */
    private String code;
    /**
     * Business Message
     */
    private String msg;
    /**
     * Payment Order Number
     */
    private String payOrderNo;
    /**
     * Payment Status: Reference:
     * @see PayStatus
     */
    private Integer payStatus;
    /**
     * Transaction Amount of the Order
     */
    private Integer totalAmount;
    /**
     * Payment successful time
     */
    private LocalDateTime successTime;
}
