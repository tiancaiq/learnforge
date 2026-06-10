package com.learnforge.course.controller;

import com.learnforge.course.domain.dto.CategoryAddDTO;
import com.learnforge.course.domain.dto.CategoryDisableOrEnableDTO;
import com.learnforge.course.domain.dto.CategoryListDTO;
import com.learnforge.course.domain.dto.CategoryUpdateDTO;
import com.learnforge.course.domain.vo.CategoryInfoVO;
import com.learnforge.course.domain.vo.CategoryVO;
import com.learnforge.course.domain.vo.SimpleCategoryVO;
import com.learnforge.course.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Course category
 *
 * @ClassName CategoryController
 * @Author wusongsong
 * @Date 2022/7/10 11:16
 * @Version
 **/
@RestController
@Api(tags = "Course category related interface")
@RequestMapping("categorys")
@Slf4j
@Validated
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("list")
    @ApiOperation("Query course category information")
    public List<CategoryVO> list(CategoryListDTO categoryListDTO) {
        log.info("list categoryListDTO : {}", categoryListDTO);
        return categoryService.list(categoryListDTO);
    }

    @GetMapping("{id}")
    @ApiOperation("Get course category information")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "Classification id")
    )
    public CategoryInfoVO get(@PathVariable("id") Long id) {
        return categoryService.get(id);
    }

    @PostMapping("add")
    @ApiOperation("Add course category")
    public void add(@Valid @RequestBody CategoryAddDTO categoryAddDTO) {

        categoryService.add(categoryAddDTO);
    }

    @DeleteMapping("{id}")
    @ApiOperation("Delete category information")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "Classification id")
    )
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }

    @PutMapping("disableOrEnable")
    @ApiOperation("Course category disable or enable")
    public void disableOrEnable(@Validated @RequestBody CategoryDisableOrEnableDTO categoryDisableOrEnableDTO) {
        categoryService.disableOrEnable(categoryDisableOrEnableDTO);
    }

    @PutMapping("update")
    @ApiOperation("Update course category")
    public void updateCategory(@Validated @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        categoryService.update(categoryUpdateDTO);
    }

    @GetMapping("all")
    @ApiOperation("Get all course category information, only include id, name, course category relationship")
    public List<SimpleCategoryVO> all(@RequestParam(value = "admin",required = false, defaultValue = "0") Boolean admin) {
        return categoryService.all(admin);
    }

    @GetMapping("getAllOfOneLevel")
    @ApiOperation("Get all course categories, not layered")
    public List<CategoryVO> allOfOneLevel() {
        return categoryService.allOfOneLevel();
    }
}
