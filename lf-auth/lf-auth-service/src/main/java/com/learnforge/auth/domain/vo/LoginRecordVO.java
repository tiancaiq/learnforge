package com.learnforge.auth.domain.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoginRecordVO {
    /**
     * User id
     */
    private Long userId;

    /**
     * Login time
     */
    private LocalDateTime loginTime;

    /**
     * Logout time
     */
    private LocalDateTime logoutTime;

    /**
     * Login date
     */
    private LocalDate loginDate;

    /**
     * Login duration, unit is seconds
     */
    private Long duration;

    /**
     * IP address
     */
    private String ipv4;
}
