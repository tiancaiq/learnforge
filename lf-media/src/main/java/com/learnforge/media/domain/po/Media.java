package com.learnforge.media.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.media.enums.FileStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Media table, mainly video files
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media")
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id")
    private Long id;

    /**
     * File unique identifier in cloud, for example: 387702302659783576
     */
    private String fileId;

    /**
     * File name
     */
    private String filename;

    /**
     * Media playback address
     */
    private String mediaUrl;

    /**
     * Media cover address
     */
    private String coverUrl;

    /**
     * Video duration, unit seconds
     */
    private Float duration;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Status: 1-uploading, 2-uploaded
     */
    private FileStatus status;
    /**
     * Media size, unit bytes
     */
    private Long size;
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
