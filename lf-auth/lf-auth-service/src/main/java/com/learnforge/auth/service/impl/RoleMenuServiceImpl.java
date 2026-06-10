package com.learnforge.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.auth.domain.po.RoleMenu;
import com.learnforge.auth.mapper.RoleMenuMapper;
import com.learnforge.auth.service.IRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Account-Role Association Table Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Override
    public void removeByRoleId(Long id) {
        remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id));
    }

    @Override
    public void deleteRoleMenus(Long roleId, List<Long> menuIds) {
        // delete from role_menus where role_id = ? and menu_id in (?,?);
        remove(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId)
                        .in(RoleMenu::getMenuId, menuIds)
        );
    }
}
