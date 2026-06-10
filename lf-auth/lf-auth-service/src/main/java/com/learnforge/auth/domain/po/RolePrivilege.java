package com.learnforge.auth.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * Account, role association table
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role_privilege")
@NoArgsConstructor
public class RolePrivilege implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Role id
     */
    private Long roleId;

    /**
     * Privilege id
     */
    private Long privilegeId;


    public RolePrivilege(Long roleId, Long privilegeId) {
        this.roleId = roleId;
        this.privilegeId = privilegeId;
    }
}
