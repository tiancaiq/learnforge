package com.learnforge.auth.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.auth.domain.dto.PrivilegeDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Privilege table, including menu privilege and access path privilege
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`privilege`")
public class Privilege implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId
    private Long id;

    /**
     * Menu id
     */
    private Long menuId;

    /**
     * Description
     */
    private String intro;

    /**
     * API privilege request method
     */
    private String method;

    /**
     * API privilege request path
     */
    private String uri;

    /**
     * Is internal interface
     */
    private Boolean internal;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;

    /**
     * Creator id
     */

    private Long creater;

    /**
     * Updater id
     */

    private Long updater;

    /**
     * Department id
     */
    private Long depId;

    /**
     * Logical deletion, default 0
     */
    private Integer deleted;

    public Privilege() {
    }

    public Privilege(PrivilegeDTO dto) {
        this.id = dto.getId();
        this.menuId = dto.getMenuId();
        this.intro = dto.getIntro();
        this.method = dto.getMethod();
        this.uri = dto.getUri();
        this.internal = dto.getInternal();
    }

    public PrivilegeDTO toDTO(){
        PrivilegeDTO dto = new PrivilegeDTO();
        dto.setId(id);
        dto.setMenuId(menuId);
        dto.setIntro(intro);
        dto.setMethod(method);
        dto.setUri(uri);
        dto.setInternal(internal);
        return dto;
    }
}
