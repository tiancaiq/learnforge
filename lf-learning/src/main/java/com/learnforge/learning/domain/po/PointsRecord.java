package com.learnforge.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.learnforge.learning.domain.enums.PointsRecordType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("points_record")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**

     */
    @TableField("user_id")
    private Long userId;

    /**

     */
    @TableField("type")
    private PointsRecordType type;

    /**

     */
    @TableField("points")
    private Integer points;

    /**

     */
    @TableField("create_time")
    private LocalDateTime createTime;


}
