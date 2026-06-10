package com.learnforge.auth.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.auth.constants.AuthConstants;
import com.learnforge.auth.domain.po.AccountRole;
import com.learnforge.auth.domain.po.Menu;
import com.learnforge.auth.domain.po.RoleMenu;
import com.learnforge.auth.mapper.MenuMapper;
import com.learnforge.auth.service.IAccountRoleService;
import com.learnforge.auth.service.IMenuService;
import com.learnforge.auth.service.IRoleMenuService;
import com.learnforge.auth.service.IRoleService;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.learnforge.auth.common.constants.AuthErrorInfo.Msg.MENU_NOT_FOUND;
import static com.learnforge.auth.common.constants.AuthErrorInfo.Msg.ROLE_NOT_FOUND;

/**
 * <p>
 * Permission Table, Including Menu and Access Path Permissions Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    private final IRoleMenuService roleMenuService;
    private final IRoleService roleService;
    private final IAccountRoleService accountRoleService;

    @Override
    public List<Menu> listMenuByUser() {
        // 1. Get User Information
        Long userId = UserContext.getUser();
        // 2. Query Roles
        List<AccountRole> accountRoles = accountRoleService.lambdaQuery().eq(AccountRole::getAccountId, userId).list();
        if (CollUtil.isEmpty(accountRoles)) {
            return Collections.emptyList();
        }
        List<Long> roleIds = accountRoles.stream().map(AccountRole::getRoleId).collect(Collectors.toList());
        // 3. Query Menus
        return getBaseMapper().listByRoles(roleIds);
    }

    @Override
    @Transactional
    public void saveMenu(Menu menu) {
        // 1. Add Menu
        save(menu);
        // 2. Check if Current Menu has Parent Menu
        if(menu.getParentId() != 0) {
            // Has Parent Menu, Need to Check Parent Menu hashChildren Property
            Menu parent = getById(menu.getParentId());
            if(!parent.getHasChildren()){
                // Update Parent Menu's hasChildren Property
                parent.setHasChildren(true);
                parent.setUpdateTime(null);
                updateById(parent);
            }
        }
        // 3. Associate with Administrator
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setMenuId(menu.getId());
        roleMenu.setRoleId(AuthConstants.ADMIN_ROLE_ID);
        roleMenuService.save(roleMenu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        // 1. Query Current Menu
        Menu menu = getById(id);
        if (menu == null) {
            return;
        }
        // 2. Check if Current Menu has Submenu
        List<Long> delIds;
        if (menu.getHasChildren()) {
            // 2.1. Add Submenu and Parent Menu
            delIds = lambdaQuery()
                    .eq(Menu::getParentId, id)
                    .list()
                    .stream()
                    .map(Menu::getId)
                    .collect(Collectors.toList());
            // Add Parent Menu ID
            delIds.add(id);
        }else {
            // 2.2. Add Parent Menu ID
            delIds = Collections.singletonList(id);
        }
        // 3. Delete Menu
        removeByIds(delIds);
        // 4. Delete Menu-Role Association Data
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getMenuId, delIds));
    }

    @Override
    public void bindRoleMenus(Long roleId, List<Long> menuIds) {
        // 1. Check if Role Exists
        boolean exists = roleService.exists(roleId);
        if (!exists) {
            throw new CommonException(ROLE_NOT_FOUND);
        }
        // 2. Check if Menu Exists
        Integer menuCount = lambdaQuery().in(Menu::getId, menuIds).count();
        if (menuCount != menuIds.size()) {
            throw new CommonException(MENU_NOT_FOUND);
        }
        // 3. Bind Relationship
        List<RoleMenu> roleMenus = new ArrayList<>(menuCount);
        for (Long menuId : menuIds) {
            roleMenus.add(new RoleMenu(roleId, menuId));
        }
        // 4. Write to Database
        roleMenuService.saveBatch(roleMenus);
    }

    @Override
    public void deleteRoleMenus(Long roleId, List<Long> menuIds) {
        roleMenuService.deleteRoleMenus(roleId, menuIds);
    }
}
