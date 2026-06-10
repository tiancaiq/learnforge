package com.learnforge.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.auth.domain.po.Role;

import java.util.List;

/**
 * <p>
 * Role Table Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
public interface IRoleService extends IService<Role> {

    boolean exists(Long roleId);
    boolean exists(List<Long> roleIds);

    void deleteRole(Long id);
}
