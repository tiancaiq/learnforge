package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Course category
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Course category id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Classification name
     */
    private String name;

    /**
     * Parent Category ID, Parent ID of Primary Category is 0
     */
    private Long parentId;

    /**
     * Category Level, 1,2,3: Represents Primary Category, Secondary Category, Tertiary Category
     */
    private Integer level;

    /**
     * Priority of Same Level Directory, Smaller Number Means Higher Priority, Can Be Repeated
     */
    private Integer priority;

    /**
     * Course Category Status, 1: Normal, 0: Disabled
     */
    private Integer status;

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

    @TableLogic
    private Integer deleted;


}
