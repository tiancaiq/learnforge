package com.learnforge.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.auth.domain.po.RolePrivilege;

import java.util.List;

/**
 * <p>
 * Account-Role Association Table Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
public interface IRolePrivilegeService extends IService<RolePrivilege> {

    void removeByPrivilegeId(Long id);

    void removeByRoleId(Long id);

    void deleteRolePrivileges(Long roleId, List<Long> privilegeIds);
}
