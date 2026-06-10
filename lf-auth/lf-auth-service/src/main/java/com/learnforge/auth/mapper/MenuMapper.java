package com.learnforge.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnforge.auth.domain.po.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Permission Table, Including Menu and Access Path Permissions Mapper Interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> listByRoles(@Param("roleIds") List<Long> roleIds);
}
