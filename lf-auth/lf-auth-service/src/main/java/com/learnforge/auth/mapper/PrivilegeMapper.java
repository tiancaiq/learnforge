package com.learnforge.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnforge.auth.domain.po.Privilege;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Permission Table, Including Menu and Access Path Permissions Mapper Interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-15
 */
public interface PrivilegeMapper extends BaseMapper<Privilege> {

    List<Privilege> listRolePrivileges(@Param("roleId") Long roleId);
}
