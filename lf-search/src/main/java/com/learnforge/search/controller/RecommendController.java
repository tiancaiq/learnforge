package com.learnforge.search.controller;

import com.learnforge.search.domain.vo.CourseVO;
import com.learnforge.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Course Recommendation Related Interfaces")
@RequiredArgsConstructor
@RestController
@RequestMapping("recommend")
public class RecommendController {

    private final ISearchService searchService;

    @ApiOperation("Premium Courses Interface")
    @GetMapping("/best")
    public List<CourseVO> queryBestTopN(){
        return searchService.queryBestTopN();
    }

    @ApiOperation("New Course Recommendation Interface")
    @GetMapping("/new")
    public List<CourseVO> queryNewTopN(){
        return searchService.queryNewTopN();
    }

    @ApiOperation("Premium Open Course Interface")
    @GetMapping("/free")
    public List<CourseVO> queryFreeTopN(){
        return searchService.queryFreeTopN();
    }

}
