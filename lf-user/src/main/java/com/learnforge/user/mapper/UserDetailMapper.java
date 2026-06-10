package com.learnforge.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnforge.user.domain.po.UserDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Teacher Details Mapper Interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-15
 */
public interface UserDetailMapper extends BaseMapper<UserDetail> {

    UserDetail queryById(Long userId);

    List<UserDetail> queryByIds(List<Long> ids);

    Page<UserDetail> queryByPage(Page<UserDetail> p, @Param("ew") QueryWrapper<UserDetail> wrapper);
}
