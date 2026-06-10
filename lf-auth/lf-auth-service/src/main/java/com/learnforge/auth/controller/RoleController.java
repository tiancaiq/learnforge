package com.learnforge.auth.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.learnforge.api.dto.auth.RoleDTO;
import com.learnforge.auth.domain.po.Role;
import com.learnforge.auth.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Api(tags = "Role management")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @ApiOperation("Query employee role list")
    @GetMapping("/list")
    public List<RoleDTO> listAllRoles(){
        // 1. Query
        List<Role> list = roleService.list();
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 3. Data conversion
        return list.stream().map(Role::toDTO).collect(Collectors.toList());
    }

    @ApiOperation("Query employee role list")
    @GetMapping
    public List<RoleDTO> listStaffRoles(){
        // 1. Query
        List<Role> list = roleService.lambdaQuery().eq(Role::getType, Role.RoleType.CUSTOM).list();
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 3. Data conversion
        return list.stream().map(Role::toDTO).collect(Collectors.toList());
    }

    @ApiOperation("Query role by id")
    @GetMapping("/{id}")
    public RoleDTO queryRoleById(@PathVariable("id") Long id){
        // 1. Query
        Role role = roleService.getById(id);
        if (role == null) {
            return null;
        }
        // 2. Data conversion
        return role.toDTO();
    }



    @ApiOperation("Add role")
    @PostMapping
    public RoleDTO saveRole(@RequestBody RoleDTO roleDTO) {
        Role role = new Role(roleDTO);
        role.setType(Role.RoleType.CUSTOM);
        // 1. Add
        roleService.save(role);
        // 2. Return
        roleDTO.setId(role.getId());
        return roleDTO;
    }

    @ApiOperation("Modify role information")
    @PutMapping("{id}")
    public void updateRole(
            @RequestBody RoleDTO roleDTO,
            @ApiParam(value = "Role id", example = "1") @PathVariable("id") Long id
    ) {
        // 1. Data conversion
        Role role = new Role(roleDTO);
        role.setId(id);
        // 2. Modify
        roleService.updateById(role);
    }

    @ApiOperation("Delete role information")
    @DeleteMapping("{id}")
    public void deleteRole(@ApiParam(value = "Role id", example = "1") @PathVariable("id") Long id) {
        roleService.deleteRole(id);
    }
}
