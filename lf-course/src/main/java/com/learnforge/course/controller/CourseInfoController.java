package com.learnforge.course.controller;

import com.learnforge.api.dto.course.*;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.course.service.ICategoryService;
import com.learnforge.course.service.ICourseCatalogueService;
import com.learnforge.course.service.ICourseService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Internal Service Interface Call
 *
 * @ClassName CourseInfoController
 * @Author wusongsong
 * @Date 2022/7/18 15:19
 * @Version
 **/
@RestController
@RequestMapping("course")
@Api(tags = "Course Related Interfaces, Internal Call")
public class CourseInfoController {

    @Autowired
    private ICourseCatalogueService courseCatalogueService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("infoByTeacherIds")
    @ApiOperation("Get Number of Courses and Questions Managed by Teacher by Teacher ID")
    public List<SubNumAndCourseNumDTO> infoByTeacherIds(@RequestParam("teacherIds") List<Long> teacherIds) {

        if (CollUtils.isEmpty(teacherIds)) {
            return new ArrayList<>();
        }
        return courseService.countSubjectNumAndCourseNumOfTeacher(teacherIds);
    }

    /**
     * Get the corresponding mediaId and course ID by section ID
     *
     * @param sectionId Section ID
     * @return Section's corresponding mediaId and course ID
     */
    @GetMapping("/section/{id}")
    @ApiImplicitParam(name = "id", value = "Section ID, Not Support Chapter ID or Practice ID Query")
    public SectionInfoDTO sectionInfo(@PathVariable("id") Long sectionId) {
        return courseCatalogueService.getSimpleSectionInfo(sectionId);
    }

    /**
     * Query the number of times media is referenced by media ID list
     *
     * @param mediaIds Media ID list
     * @return List of media ID and the number of times it's referenced
     */
    @GetMapping("/media/useInfo")
    public List<MediaQuoteDTO> mediaUserInfo(@RequestParam("mediaIds") List<Long> mediaIds) {
        return courseCatalogueService.countMediaUserInfo(mediaIds);
    }

    @GetMapping("/{id}/searchInfo")
    @ApiOperation("When Course is Listed, Need to Query Course Info and Add to Index")
    public CourseDTO getSearchInfo(@ApiParam("Course ID") @PathVariable("id") Long id) {
        return courseService.getCourseDTOById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get Course Info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Get Course Info"),
            @ApiImplicitParam(name = "withCatalogue", value = "Whether to Query Directory Info"),
            @ApiImplicitParam(name = "withTeachers", value = "Whether to Query Course Teacher Info")
    })
    public CourseFullInfoDTO getById(
            @PathVariable("id") Long id,
            @RequestParam(value = "withCatalogue", required = false) boolean withCatalogue,
            @RequestParam(value = "withTeachers", required = false) boolean withTeachers) {
        return courseService.getInfoById(id, withCatalogue, withTeachers);
    }


    @GetMapping("/getCateNameMap")
    @ApiIgnore
    public Map<Long, String> queryByThirdCateIds(@RequestParam("thirdCateIdList") List<Long> thirdCateIdList) {
        return categoryService.queryByThirdCateIds(thirdCateIdList);
    }

    @GetMapping("/name")
    public List<Long> queryCoursesIdByName(@RequestParam("name") String name){
        return courseService.queryCourseIdByName(name);
    }
}
