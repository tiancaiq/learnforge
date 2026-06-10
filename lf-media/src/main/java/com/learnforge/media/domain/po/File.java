package com.learnforge.media.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.media.enums.FileStatus;
import com.learnforge.media.enums.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * File table, can be a regular file, image, etc.
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key, file id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * File unique identifier in cloud, for example: aaa.jpg
     */
    @TableField("`key`")
    private String key;

    /**
     * File name at upload time
     */
    private String filename;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Status: 1-pending upload 2-uploaded, not used 3-used
     */
    private FileStatus status;
    /**
     * Status: 1-Tencent 2-Alibaba
     */
    private Platform platform;
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
    private Integer deleted;
}
