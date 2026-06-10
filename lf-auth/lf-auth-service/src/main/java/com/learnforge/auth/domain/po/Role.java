package com.learnforge.auth.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.learnforge.api.dto.auth.RoleDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Role table
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role")
@NoArgsConstructor
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId
    private Long id;

    /**
     * Role code, for example: admin
     */
    private String code;

    /**
     * Role name
     */
    private String name;

    /**
     * Role type: 0-fixed role (non-selectable), 1-custom role
     */
    private RoleType type;

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

    public Role(RoleDTO dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public RoleDTO toDTO(){
        RoleDTO dto = new RoleDTO();
        dto.setId(id);
        dto.setCode(code);
        dto.setName(name);
        return dto;
    }

    @Getter
    public enum RoleType{
        CONSTANT(0, "Fixed role"),
        CUSTOM(1, "Custom role"),
        ;
        @EnumValue
        int value;
        String desc;

        RoleType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }
}
