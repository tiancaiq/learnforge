package com.learnforge.pay.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.learnforge.pay.third.model.PayStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Payment order
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_order")
public class PayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Business order number
     */
    private Long bizOrderNo;

    /**
     * Payment order number
     */
    private Long payOrderNo;

    /**
     * Payment user id
     */
    private Long bizUserId;

    /**
     * Payment channel id
     */
    private String payChannelCode;

    /**
     * Payment amount, unit is cents
     */
    private Integer amount;

    /**
     * Payment type, 1: h5, 2: Mini program, 3: Official account, 4: QR code scan
     */
    private Integer payType;

    /**
     * Payment status, 0: Pending submission, 1: Pending payment, 2: Payment succeeded, 3: Payment timeout or canceled
     */
    private Integer status;

    /**
     * Extension field, used to pass fields for different channels to handle separately
     */
    private String expandJson;

    /**
     * Business-side callback interface
     */
    private String notifyUrl;

    /**
     * Business-side callback count
     */
    private Integer notifyTimes;

    /**
     * Callback status, 0: Pending callback, 1: Callback succeeded, 2: Callback failed
     */
    private Integer notifyStatus;

    /**
     * Third-party returned business code
     */
    private String resultCode;

    /**
     * Third-party returned prompt message
     */
    private String resultMsg;

    /**
     * Payment successful time
     */
    private LocalDateTime paySuccessTime;

    /**
     * Payment timeout time
     */
    private LocalDateTime payOverTime;

    /**
     * Payment QR code
     */
    private String qrCodeUrl;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;

    /**
     * Creator
     */
    private Long creater;

    /**
     * Updater
     */
    private Long updater;

    /**
     * Logical Deletion
     */
    @TableLogic
    private Boolean deleted;


    public boolean success(){
        return PayStatus.TRADE_SUCCESS.equalsValue(status);
    }

    public boolean closed(){
        return PayStatus.TRADE_CLOSED.equalsValue(status);
    }

    public boolean waitBuyerPay(){
        return PayStatus.WAIT_BUYER_PAY.equalsValue(status);
    }

    public boolean notCommit(){
        return PayStatus.NOT_COMMIT.equalsValue(status);
    }
}
