package com.learnforge.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.auth.common.domain.PrivilegeRoleDTO;
import com.learnforge.auth.domain.po.Privilege;
import com.learnforge.common.domain.query.PageQuery;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Permission Table, Including Menu and Access Path Permissions Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-15
 */
public interface IPrivilegeService extends IService<Privilege> {

    Page<Privilege> listPrivilegesByPage(PageQuery pageQuery);

    void savePrivilege(Privilege privilege);

    void removePrivilegeById(Long id);

    List<PrivilegeRoleDTO> listPrivilegeRoles();

    Set<Long> listPrivilegeByRoleId(Long roleId);

    void bindRolePrivileges(Long roleId, List<Long> privilegeIds);

    void deleteRolePrivileges(Long roleId, List<Long> privilegeIds);
}
