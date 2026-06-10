package com.learnforge.pay.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Payment channel
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_channel")
public class PayChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Payment channel id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 50
     */
    private String name;

    /**
     * Payment channel code, used to get payment implementation
     */
    private String channelCode;

    /**
     * Channel priority, smaller number means higher priority
     */
    private Integer channelPriority;

    /**
     * Channel icon
     */
    private String channelIcon;

    /**
     * Payment channel status, 1: In use, 2: Disabled
     */
    private Integer status;

    /**
     * Creator
     */
    private Long creater;

    /**
     * Updater
     */
    private Long updater;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;


}
