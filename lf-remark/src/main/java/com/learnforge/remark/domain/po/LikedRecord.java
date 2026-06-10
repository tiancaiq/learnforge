package com.learnforge.remark.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * Like Record Table
 * </p>
 *
 * @author luke
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("liked_record")
public class LikedRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary Key ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * User id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * Liked Business ID
     */
    @TableField("biz_id")
    private Long bizId;

    /**
     * Liked Business Type
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * Creation Time
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
