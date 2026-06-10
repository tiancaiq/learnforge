package com.learnforge.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.auth.domain.po.Role;
import com.learnforge.auth.mapper.RoleMapper;
import com.learnforge.auth.service.IRoleMenuService;
import com.learnforge.auth.service.IRolePrivilegeService;
import com.learnforge.auth.service.IRoleService;
import com.learnforge.auth.util.PrivilegeCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * Role Table Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private final IRoleMenuService roleMenuService;
    private final IRolePrivilegeService rolePrivilegeService;
    private final PrivilegeCache privilegeCache;

    @Override
    public boolean exists(Long roleId) {
        Integer count = lambdaQuery().eq(Role::getId, roleId).count();
        return count > 0;
    }

    @Override
    public boolean exists(List<Long> roleIds) {
        Integer count = lambdaQuery().in(Role::getId, roleIds).count();
        return count != roleIds.size();
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        // 1. Delete Role
        removeById(id);
        // 2. Delete Role-Permission Association Information
        roleMenuService.removeByRoleId(id);
        rolePrivilegeService.removeByRoleId(id);
        // 3. Clear Cache
        privilegeCache.removeCacheByRoleId(id);
    }
}
