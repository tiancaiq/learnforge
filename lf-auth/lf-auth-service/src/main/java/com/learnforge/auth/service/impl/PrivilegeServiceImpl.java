package com.learnforge.auth.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.auth.common.domain.PrivilegeRoleDTO;
import com.learnforge.auth.domain.po.Privilege;
import com.learnforge.auth.domain.po.RolePrivilege;
import com.learnforge.auth.mapper.PrivilegeMapper;
import com.learnforge.auth.service.IPrivilegeService;
import com.learnforge.auth.service.IRolePrivilegeService;
import com.learnforge.auth.service.IRoleService;
import com.learnforge.auth.util.PrivilegeCache;
import com.learnforge.common.domain.query.PageQuery;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.CollUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.auth.common.constants.AuthErrorInfo.Msg.*;
import static com.learnforge.auth.constants.AuthConstants.ADMIN_ROLE_ID;

/**
 * <p>
 * Permission Table, Including Menu and Access Path Permissions Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-15
 */
@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, Privilege> implements IPrivilegeService {

    private final IRolePrivilegeService rolePrivilegeService;
    private final IRoleService roleService;
    private final PrivilegeCache privilegeCache;

    @Override
    public Page<Privilege> listPrivilegesByPage(PageQuery pageQuery) {
        // 1. Page query
        return query()
                .orderBy(pageQuery.getSortBy() != null, pageQuery.getIsAsc(), pageQuery.getSortBy())
                .page(new Page<>(pageQuery.getPageNo(), pageQuery.getPageSize()));
    }

    @Override
    @Transactional
    public void savePrivilege(Privilege p) {
        p.setMethod(p.getMethod().toUpperCase());
        // 1. Check if Exists
        Integer count = lambdaQuery()
                .eq(Privilege::getMethod, p.getMethod())
                .eq(Privilege::getUri, p.getUri())
                .count();
        if(count > 0){
            // Already Exists, End
            throw new CommonException(PRIVILEGE_EXISTS);
        }
        // 2. Add Permission Data
        save(p);
        // 3. Add Permission to Super Administrator
        RolePrivilege rolePrivilege = new RolePrivilege();
        rolePrivilege.setPrivilegeId(p.getId());
        rolePrivilege.setRoleId(ADMIN_ROLE_ID);
        rolePrivilegeService.save(rolePrivilege);

        // 4. Add Cache
        privilegeCache.cacheSinglePrivilege(p, CollUtils.singletonSet(ADMIN_ROLE_ID));
    }

    @Override
    @Transactional
    public void removePrivilegeById(Long id) {
        // Delete privilege
        removeById(id);
        // Delete Role Permission Association
        rolePrivilegeService.removeByPrivilegeId(id);
        // Delete Cache
        privilegeCache.removePrivilegeCacheById(id);
    }

    @Override
    public List<PrivilegeRoleDTO> listPrivilegeRoles() {
        // 1. Query All Permissions
        List<Privilege> privileges = list();
        // 2. Query All Roles
        List<RolePrivilege> rpList = rolePrivilegeService.list();
        // 3. Group Roles by Permission
        Map<Long, List<RolePrivilege>> rpMap = rpList.stream()
                .collect(Collectors.groupingBy(RolePrivilege::getPrivilegeId));
        // 4. Assemble Permission-Role Mapping
        List<PrivilegeRoleDTO> list = new ArrayList<>(privileges.size());
        for (Privilege p : privileges) {
            // 4.1. Query Roles by Permission
            Set<Long> roles = rpMap.get(p.getId())
                    .stream()
                    .map(RolePrivilege::getRoleId)
                    .collect(Collectors.toSet());
            // 4.2. Assemble
            PrivilegeRoleDTO prDTO = new PrivilegeRoleDTO();
            prDTO.setId(p.getId());
            prDTO.setRoles(roles);
            prDTO.setAntPath(p.getMethod() + ":" + p.getUri());
            prDTO.setInternal(p.getInternal());
            // 4.3. Store in Map
            list.add(prDTO);
        }
        return list;
    }

    @Override
    public Set<Long> listPrivilegeByRoleId(Long roleId) {
        List<RolePrivilege> rolePrivileges = rolePrivilegeService.lambdaQuery()
                .eq(RolePrivilege::getRoleId, roleId)
                .list();
        if (CollectionUtil.isEmpty(rolePrivileges)) {
            return Collections.emptySet();
        }
        return rolePrivileges.stream().map(RolePrivilege::getPrivilegeId).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void bindRolePrivileges(Long roleId, List<Long> privilegeIds) {
        // 1. Check if Role Exists
        boolean roleExists = roleService.exists(roleId);
        if (!roleExists) {
            throw new CommonException(ROLE_NOT_FOUND);
        }
        // 2. Check if Permission Exists
        Integer privilegeCount = lambdaQuery().in(Privilege::getId, privilegeIds).count();
        if (privilegeCount != privilegeIds.size()) {
            throw new CommonException(PRIVILEGE_NOT_FOUND);
        }
        // 3. Bind Relationship
        List<RolePrivilege> rolePrivileges = new ArrayList<>(privilegeCount);
        for (Long privilegeId : privilegeIds) {
            rolePrivileges.add(new RolePrivilege(roleId, privilegeId));
        }
        // 4. Write to Database
        rolePrivilegeService.saveBatch(rolePrivileges);
        // 5. Reset Cache
        privilegeCache.initPrivilegesCache(listPrivilegeRoles());
    }

    @Override
    @Transactional
    public void deleteRolePrivileges(Long roleId, List<Long> privilegeIds) {
        // 1. Delete
        rolePrivilegeService.deleteRolePrivileges(roleId, privilegeIds);
        // 2. Remove Corresponding Role Permission Cache
        privilegeCache.initPrivilegesCache(listPrivilegeRoles());
    }
}
