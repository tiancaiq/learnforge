package com.learnforge.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.auth.domain.po.Menu;

import java.util.List;

/**
 * <p>
 * Permission Table, Including Menu and Access Path Permissions Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> listMenuByUser();

    void saveMenu(Menu menu);

    void deleteMenu(Long id);

    void bindRoleMenus(Long roleId, List<Long> menuIds);

    void deleteRoleMenus(Long roleId, List<Long> menuIds);
}
