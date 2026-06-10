package com.learnforge.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("learning_record")
public class LearningRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**

     */
    @TableField("lesson_id")
    private Long lessonId;

    /**

     */
    @TableField("section_id")
    private Long sectionId;

    /**

     */
    @TableField("user_id")
    private Long userId;

    /**

     */
    @TableField("moment")
    private Integer moment;

    /**

     */
    @TableField("finished")
    private Boolean finished;

    /**

     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**

     */
    @TableField("finish_time")
    private LocalDateTime finishTime;

    /**

     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
