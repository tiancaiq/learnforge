package com.learnforge.pay.third.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pre-Order Response Result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrepayResponse {
    /**
     * Is Success
     */
    private boolean success;
    /**
     * Payment Link, Used for QR Code Generation
     */
    private String payUrl;
    /**
     * Response Status Code
     */
    private String code;
    /**
     * Response message
     */
    private String msg;
    /**
     * Response Details
     */
    private String detail;
}
