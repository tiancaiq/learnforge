package com.learnforge.auth.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnforge.auth.domain.dto.PrivilegeDTO;
import com.learnforge.auth.domain.po.Privilege;
import com.learnforge.auth.domain.vo.PrivilegeOptionVO;
import com.learnforge.auth.service.IPrivilegeService;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Permission table, including menu permissions and access path permissions frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-15
 */
@RestController
@RequestMapping("/privileges")
@Api(tags = "Permission management interface")
@RequiredArgsConstructor
public class PrivilegeController {

    private final IPrivilegeService privilegesService;

    /**
     * Page query all permissions
     *
     * @param pageQuery page query condition
     * @return page result
     */
    @ApiOperation("Page query all permissions")
    @GetMapping
    public PageDTO<PrivilegeDTO> listAllPrivileges(PageQuery pageQuery) {
        // 1. Page query
        Page<Privilege> page = privilegesService.listPrivilegesByPage(pageQuery);
        // 2. Non-empty check
        List<Privilege> list = page.getRecords();
        if (CollectionUtil.isEmpty(list)) {
            // Result is empty, return empty result, add total page count
            return new PageDTO<>(page.getTotal(), page.getPages(), Collections.emptyList());
        }
        // 3. Data conversion
        List<PrivilegeDTO> dtoList = list.stream().map(Privilege::toDTO).collect(Collectors.toList());
        // 4. Package return
        return new PageDTO<>(page.getTotal(), page.getPages(), dtoList);
    }

    /**
     * Query all permissions, as dropdown menu
     *
     * @return page result
     */
    @ApiOperation("Query permissions under menu, as dropdown menu")
    @GetMapping("options/{menuId}")
    public List<PrivilegeOptionVO> listAllPrivilegesOptionsByMenuId(
            @ApiParam(value = "Menu id", example = "1") @PathVariable("menuId") Long menuId
    ) {
        // 1. Query permissions under menu
        List<Privilege> list = privilegesService.lambdaQuery()
                .eq(Privilege::getMenuId, menuId)
                .eq(Privilege::getInternal, false)
                .list();
        // 2. Non-empty check
        if (CollectionUtil.isEmpty(list)) {
            // Result is empty, return empty result
            return Collections.emptyList();
        }
        // 3. Data conversion
        return list.stream()
                .map(PrivilegeOptionVO::new).collect(Collectors.toList());
    }

    /**
     * Query permissions of a role
     *
     * @return permission list of a role
     */
    @ApiOperation("Query the privilege list under the menu, a role's privileges")
    @GetMapping("/roles/{roleId}/{menuId}")
    public List<PrivilegeOptionVO> listPrivilegeByRoleId(
            @ApiParam(value = "Role id", required = true, example = "1") @PathVariable("roleId") Long roleId,
            @ApiParam(value = "Menu id", required = true, example = "1") @PathVariable("menuId") Long menuId
    ) {
        // 1. Query the privilege id corresponding to the role
        Set<Long> privilegeIds = privilegesService.listPrivilegeByRoleId(roleId);
        if (CollectionUtil.isEmpty(privilegeIds)) {
            return Collections.emptyList();
        }
        // 2. Query all privileges under the menu
        List<PrivilegeOptionVO> vos = listAllPrivilegesOptionsByMenuId(menuId);
        // 3. Mark
        for (PrivilegeOptionVO vo : vos) {
            vo.setChecked(privilegeIds.contains(vo.getId()));
        }
        return vos;
    }

    /**
     * Add privilege
     *
     * @param privilegeDTO privilege data
     * @return added privilege data
     */
    @ApiOperation("Add privilege")
    @PostMapping
    public PrivilegeDTO savePrivilege(@Validated @RequestBody PrivilegeDTO privilegeDTO) {
        // Domain object conversion
        Privilege privilege = new Privilege(privilegeDTO);
        // Add
        privilegesService.savePrivilege(privilege);
        // Return
        return privilege.toDTO();
    }

    /**
     * Modify privilege
     *
     * @param privilegeDTO privilege data
     * @param id           id of the privilege to modify
     * @return modified privilege result
     */
    @ApiOperation("Modify privilege")
    @PutMapping("{id}")
    public PrivilegeDTO updatePrivilege(
            @RequestBody PrivilegeDTO privilegeDTO,
            @ApiParam(value = "Id of the privilege to modify", required = true, example = "1") @PathVariable("id") Long id) {
        // Domain object conversion
        Privilege privilege = new Privilege(privilegeDTO);
        privilege.setId(id);
        // Modify
        privilegesService.updateById(privilege);
        // Return
        return privilege.toDTO();
    }

    /**
     * Delete privilege
     *
     * @param id privilege id
     */
    @ApiOperation("Delete privilege")
    @DeleteMapping("{id}")
    public void removePrivilegeById(
            @ApiParam(value = "Id of the privilege to delete", required = true, example = "1") @PathVariable("id") Long id) {
        privilegesService.removePrivilegeById(id);
    }

    /**
     * Bind role with API privilege
     *
     * @param roleId       role id
     * @param privilegeIds  privilege id collection
     */
    @PostMapping("/role/{roleId}")
    @ApiOperation("Bind role with API privilege")
    public void bindRolePrivileges(
            @ApiParam(value = "Role id", example = "1") @PathVariable("roleId") Long roleId,
            @ApiParam(value = "Collection of API privilege ids") List<Long> privilegeIds) {
        privilegesService.bindRolePrivileges(roleId, privilegeIds);
    }

    /**
     * Unbind role's API privilege
     *
     * @param roleId       role id
     * @param privilegeIds  privilege id collection
     */
    @DeleteMapping("/role/{roleId}")
    @ApiOperation("Unbind role's API privilege")
    public void deleteRolePrivileges(
            @ApiParam(value = "Role id", example = "1") @PathVariable("roleId") Long roleId,
            @ApiParam(value = "Collection of API privilege ids") List<Long> privilegeIds) {
        privilegesService.deleteRolePrivileges(roleId, privilegeIds);
    }


}
