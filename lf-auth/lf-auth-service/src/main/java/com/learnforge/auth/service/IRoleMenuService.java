package com.learnforge.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.auth.domain.po.RoleMenu;

import java.util.List;

/**
 * <p>
 * Account-Role Association Table Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    void removeByRoleId(Long id);

    void deleteRoleMenus(Long roleId, List<Long> menuIds);
}
