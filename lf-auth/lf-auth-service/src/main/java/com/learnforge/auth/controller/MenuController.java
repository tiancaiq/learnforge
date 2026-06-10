package com.learnforge.auth.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.learnforge.auth.domain.dto.MenuDTO;
import com.learnforge.auth.domain.po.Menu;
import com.learnforge.auth.domain.vo.MenuOptionVO;
import com.learnforge.auth.service.IMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Permission table, including menu permissions and access path permissions frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@RestController
@RequestMapping("/menus")
@Api(tags = "Menu management")
@RequiredArgsConstructor
public class MenuController {

    private final IMenuService menuService;

    /**
     * Query sub menus by parent menu id
     * @param pid parent menu id, if 0, query level 1 menu
     * @return menu collection
     */
    @GetMapping("/parent/{pid}")
    @ApiOperation("Query sub menus by parent menu id")
    public List<MenuOptionVO> listMenusByParent(
            @ApiParam(value = "Parent menu id", example = "0") @PathVariable("pid") Long pid){
        // 1. Query by parent id
        List<Menu> list = menuService.lambdaQuery().eq(Menu::getParentId, pid).list();
        // 2. Non-empty check
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 3. Data conversion
        return list.stream().map(MenuOptionVO::new).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @ApiOperation("Query menu by id")
    public MenuOptionVO getMenuById(@ApiParam(value = "Menu id", example = "1") @PathVariable("id") Long id) {
        Menu menu = menuService.getById(id);
        if (menu == null) {
            return null;
        }
        return new MenuOptionVO(menu);
    }

    /**
     * Query menu, organize into multi-level tree structure
     * @return menu list, organized into tree structure
     */
    @GetMapping
    @ApiOperation("Query menu, organize into multi-level tree structure")
    public List<MenuOptionVO> listMenuTree(){
        // 1. Query all menus
        List<Menu> menus = menuService.list();
        return convert2MenuDTOs(menus);
    }

    private List<MenuOptionVO> convert2MenuDTOs(List<Menu> menus) {
        if (CollectionUtil.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // 2. Group by parent menu id
        Map<Long, List<MenuOptionVO>> menuMap = menus.stream()
                .map(MenuOptionVO::new)
                .collect(Collectors.groupingBy(MenuOptionVO::getParentId));
        // 3. Combine
        // 3.1. Get level 1 menu
        List<MenuOptionVO> parents = menuMap.get(0L);
        // 3.2. Get level 2 menu
        for (MenuOptionVO parent : parents) {
            List<MenuOptionVO> subMenus = menuMap.get(parent.getId());
            subMenus.sort(Comparator.comparingInt(MenuOptionVO::getPriority));
            parent.setSubMenus(subMenus);
        }
        // 3.3. Sort
        parents.sort(Comparator.comparingInt(MenuOptionVO::getPriority));
        return parents;
    }

    /**
     * Query menu options based on current user's permissions, organized into multi-level tree structure
     * @return menu list, organized into tree structure
     */
    @GetMapping("me")
    @ApiOperation("Query my menu, organized into multi-level tree structure")
    public List<MenuOptionVO> listMenuTreeByUser(){
        // 1. Query all menus
        List<Menu> menus = menuService.listMenuByUser();
        return convert2MenuDTOs(menus);
    }

    @PostMapping
    @ApiOperation("Add menu")
    public void saveMenu(@RequestBody MenuDTO menuDTO){
        // 1. Data conversion
        Menu menu = new Menu(menuDTO);
        // 2. Save
        menuService.saveMenu(menu);
    }

    @PutMapping("{id}")
    @ApiOperation("Update menu")
    public void updateMenu(
            @RequestBody MenuDTO menuDTO,
            @ApiParam(value = "Menu id", example = "1")@PathVariable("id") Long id) {
        menuDTO.setId(id);
        menuService.updateById(new Menu(menuDTO));
    }

    @DeleteMapping("{id}")
    @ApiOperation("Delete menu by id")
    public void deleteMenu(
            @ApiParam(value = "Menu id", example = "1") @PathVariable("id") Long id) {
        menuService.deleteMenu(id);
    }

    @PostMapping("/role/{roleId}")
    @ApiOperation("Bind role and menu permissions")
    public void bindRoleMenus(
            @ApiParam(value = "Role id", example = "1") @PathVariable("roleId") Long roleId,
            @ApiParam(value = "Menu id collection") List<Long> menuIds){
        menuService.bindRoleMenus(roleId, menuIds);
    }

    @DeleteMapping("/role/{roleId}")
    @ApiOperation("Unbind role and menu permissions")
    public void deleteRoleMenus(
            @ApiParam(value = "Role id", example = "1") @PathVariable("roleId") Long roleId,
            @ApiParam(value = "Menu id collection") List<Long> menuIds){
        menuService.deleteRoleMenus(roleId, menuIds);
    }
}
