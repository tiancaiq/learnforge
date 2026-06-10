package com.learnforge.search.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * User Interest Table, Save Interested Secondary Category ID
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interests")
public class Interests implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary Key, Corresponding User ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * Interested Secondary Category ID, Separated by Commas, e.g.: 120,220,330
     */
    private String interests;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;


}
