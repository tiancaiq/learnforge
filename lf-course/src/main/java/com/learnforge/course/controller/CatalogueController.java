package com.learnforge.course.controller;

import com.learnforge.course.domain.vo.CataSimpleInfoVO;
import com.learnforge.course.service.ICourseCatalogueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Course directory related interface
 *
 * @ClassName CatalogueController
 * @Author wusongsong
 * @Date 2022/7/27 13:59
 * @Version
 **/
@Api(tags = "Chapter and section directory related interface")
@RestController
@RequestMapping("catalogues")
public class CatalogueController {

    @Autowired
    private ICourseCatalogueService courseCatalogueService;

    @GetMapping("batchQuery")
    @ApiOperation("Batch query basic information according to chapter and section directory")
    public List<CataSimpleInfoVO> batchQuery(@RequestParam("ids") List<Long> ids) {
        return courseCatalogueService.getManyCataSimpleInfo(ids);
    }

    @GetMapping("querySectionInfoById/{id}")
    @ApiOperation("Get section information")
    public CataSimpleInfoVO querySectionInfoById(@PathVariable("id") Long id) {
        return courseCatalogueService.querySectionInfoById(id);
    }
}
