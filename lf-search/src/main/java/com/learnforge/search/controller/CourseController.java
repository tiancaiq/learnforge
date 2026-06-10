package com.learnforge.search.controller;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.search.domain.query.CoursePageQuery;
import com.learnforge.search.domain.vo.CourseVO;
import com.learnforge.search.service.ICourseService;
import com.learnforge.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("courses")
@Api(tags = "Course Search Interface")
@RequiredArgsConstructor
public class CourseController {

    private final ISearchService searchService;
    private final ICourseService courseService;

    @ApiOperation("User Course Search Interface")
    @GetMapping("/portal")
    public PageDTO<CourseVO> queryCoursesForPortal(CoursePageQuery query){
        return searchService.queryCoursesForPortal(query);
    }

    @ApiIgnore
    @GetMapping("/name")
    public List<Long> queryCoursesIdByName(@RequestParam("keyword") String keyword){
        return searchService.queryCoursesIdByName(keyword);
    }

    @ApiOperation("Handle Specified Course Upload Failure")
    @PostMapping("/up")
    public void handleCoursesUp(
            @ApiParam("Course ID Collection") @RequestParam("courseIds") List<Long> courseIds) {
        for (Long courseId : courseIds) {
            courseService.handleCourseUp(courseId);
        }
    }

    @ApiOperation("Handle Specified Course Offline Failure")
    @PostMapping("/down")
    public void handleCoursesDown(
            @ApiParam("Course ID Collection") @RequestParam("courseIds") List<Long> courseIds) {
        for (Long courseId : courseIds) {
            courseService.handleCourseDeletes(courseIds);
        }
    }
}
