package com.learnforge.search.controller;


import com.learnforge.search.domain.vo.CourseVO;
import com.learnforge.search.service.IInterestsService;
import com.learnforge.search.service.ISearchService;
import com.learnforge.api.dto.course.CategoryBasicDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * User Interest Table, Save Interested Secondary Category ID Frontend Controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-21
 */
@RestController
@RequestMapping("/interests")
@Api(tags = "Course Recommendation Related Interfaces")
@RequiredArgsConstructor
public class InterestsController {

    private final IInterestsService interestsService;
    private final ISearchService searchService;

    @ApiOperation("Add Interest")
    @PostMapping
    public void saveMyInterests(@RequestParam("interestedIds") List<Long>interestedIds){
        interestsService.saveInterests(interestedIds);
    }

    @ApiOperation("Query My Interests")
    @GetMapping
    public List<CategoryBasicDTO> queryMyInterests(){
        return interestsService.queryMyInterests();
    }


    @ApiOperation("Query Top 10 Courses by Secondary Category ID")
    @GetMapping("/{id}/courses")
    public List<CourseVO> queryCourseByCateId(@PathVariable("id") Long cateLv2Id){
        return searchService.queryCourseByCateId(cateLv2Id);
    }
}