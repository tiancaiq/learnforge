package com.learnforge.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("points_board_season")
public class PointsBoardSeason implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**

     */
    @TableField("name")
    private String name;

    /**

     */
    @TableField("begin_time")
    private LocalDate beginTime;

    /**

     */
    @TableField("end_time")
    private LocalDate endTime;


}
