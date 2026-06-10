package com.learnforge.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.user.domain.po.UserDetail;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.common.enums.UserType;

import java.util.List;

/**
 * <p>
 * Teacher Details Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-15
 */
public interface IUserDetailService extends IService<UserDetail> {

    UserDetail queryById(Long userId);

    List<UserDetail> queryByIds(List<Long> ids);

    Page<UserDetail> queryUserDetailByPage(UserPageQuery pageQuery, UserType type);
}
