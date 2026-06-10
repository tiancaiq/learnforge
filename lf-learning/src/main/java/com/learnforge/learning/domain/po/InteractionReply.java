package com.learnforge.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interaction_reply")
@AllArgsConstructor
@NoArgsConstructor
public class InteractionReply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**

     */
    private Long questionId;

    /**

     */
    private Long answerId;

    /**

     */
    private Long userId;

    /**

     */
    private String content;

    /**

     */
    private Long targetUserId;

    /**

     */
    private Long targetReplyId;

    /**

     */
    private Integer replyTimes;

    /**

     */
    private Integer likedTimes;

    /**

     */
    private Boolean hidden;

    /**

     */
    private Boolean anonymity;

    /**

     */
    private LocalDateTime createTime;

    /**

     */
    private LocalDateTime updateTime;


}
