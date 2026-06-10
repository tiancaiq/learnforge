package com.learnforge.course.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.learnforge.api.dto.course.CourseSimpleInfoDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.validate.annotations.ParamChecker;
import com.learnforge.course.constants.CourseStatus;
import com.learnforge.course.domain.dto.*;
import com.learnforge.course.domain.vo.*;
import com.learnforge.course.service.*;
import com.learnforge.course.utils.CourseSaveBaseGroup;
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
 * Course controller
 *
 * @ClassName CourseController
 * @Author wusongsong
 * @Date 2022/7/10 15:34
 * @Version
 **/
@Api(tags = "Course related interface")
@RestController
@RequestMapping("courses")
@Slf4j
@Validated
public class CourseController {

    @Autowired
    private ICourseDraftService courseDraftService;

    @Autowired
    private ICourseCatalogueDraftService courseCatalogueDraftService;

    @Autowired
    private ICourseTeacherDraftService courseTeacherDraftService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICourseCatalogueService courseCatalogueService;

    //todo to be done in phase two
//    @GetMapping("statistics")
//    @ApiOperation("Course data statistics")
    public CourseStatisticsVO statistics() {
        return null;
    }

    @GetMapping("baseInfo/{id}")
    @ApiOperation("Get course basic information")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "Course ID"),
            @ApiImplicitParam(name = "see", value = "Is for viewing page data, default is viewing, if not for viewing page data, it is for editing page use")})
    public CourseBaseInfoVO baseInfo(@PathVariable("id") Long id,
                                     @RequestParam(value = "see", required = false, defaultValue = "1") Boolean see) {
        return courseDraftService.getCourseBaseInfo(id, see);
    }

    @PostMapping("baseInfo/save")
    @ApiOperation("Save course basic information")
    @ParamChecker
    //Validate non-business restriction fields
    public CourseSaveVO save(@RequestBody @Validated(CourseSaveBaseGroup.class) CourseBaseInfoSaveDTO courseBaseInfoSaveDTO) {
        return courseDraftService.save(courseBaseInfoSaveDTO);
    }

    @GetMapping("catas/{id}")
    @ApiOperation("Get course chapters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Course ID"),
            @ApiImplicitParam(name = "see", value = "Is for viewing page data, default is viewing, if not for viewing page data, it is for editing page use")
    })
    public List<CataVO> catas(@PathVariable(value = "id", required = false) Long id,
                              @RequestParam(value = "see", required = false, defaultValue = "1") Boolean see,
                              @RequestParam(value = "withPractice", required = false, defaultValue = "1") Boolean withPractice) {
        return courseCatalogueDraftService.queryCourseCatalogues(id, see, withPractice);
    }

    @PostMapping("catas/save/{id}/{step}")
    @ApiOperation("Save chapter")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Course ID"),
            @ApiImplicitParam(name = "step", value = "Step")
    })
    @ParamChecker
    public void catasSave(@RequestBody @Validated List<CataSaveDTO> cataSaveDTOS,
                          @PathVariable("id") Long id, @PathVariable(value = "step",required = false) Integer step) {
        courseCatalogueDraftService.save(id, cataSaveDTOS, step);
    }

    @PostMapping("media/save/{id}")
    @ApiOperation("Course video")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Course ID")
    })
    public void mediaSave(@PathVariable("id") Long id, @RequestBody @Valid List<CourseMediaDTO> courseMediaDTOS) {
        courseCatalogueDraftService.saveMediaInfo(id, courseMediaDTOS);
    }

    @PostMapping("subjects/save/{id}")
    @ApiOperation("Save section or practice question")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Course ID")
    })
    public void saveSuject(@PathVariable("id") Long id, @RequestBody @Validated List<CataSubjectDTO> cataSubjectDTO) {
        courseCatalogueDraftService.saveSuject(id, cataSubjectDTO);
    }

    @GetMapping("subjects/get/{id}")
    @ApiOperation("Get section or practice question (for editing)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Course ID")
    })
    public List<CataSimpleSubjectVO> getSuject(@PathVariable("id") Long id) {
        return courseCatalogueDraftService.getSuject(id);
    }

    @GetMapping("teachers/{id}")
    @ApiOperation("Query course related teacher information")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Course ID"),
            @ApiImplicitParam(name = "see", value = "Is for viewing page data, default is viewing, if not for viewing page data, it is for editing page use")
    })
    public List<CourseTeacherVO> teacher(@PathVariable("id") Long id,
                                         @RequestParam(value = "see", required = false, defaultValue = "1") Boolean see) {
        return courseTeacherDraftService.queryTeacherOfCourse(id, see);
    }

    @PostMapping("teachers/save")
    @ApiOperation("Save teacher information")
    public void teachersSave(@RequestBody @Validated CourseTeacherSaveDTO courseTeacherSaveDTO) {
        courseTeacherDraftService.save(courseTeacherSaveDTO);
    }


    @PostMapping("upShelf")
    @ApiOperation("Course launch")
    public void upShelf(@RequestBody @Validated CourseIdDTO courseIdDTO) {
        courseDraftService.upShelf(courseIdDTO.getId());
    }

    @GetMapping("checkBeforeUpShelf/{id}")
    @ApiOperation("Course launch pre-validation")
    public void checkBeforeUpShelf(@PathVariable("id") Long id){
        courseDraftService.checkBeforeUpShelf(id);
    }

    @PostMapping("downShelf")
    @ApiOperation("Course shutdown")
    public void downShelf(@RequestBody @Validated CourseIdDTO courseIdDTO) {
        courseDraftService.downShelf(courseIdDTO.getId());
    }

    /**
     * First delete the draft, then delete the data, and then delete the draft
     *
     * @param id
     */
    @DeleteMapping("delete/{id}")
    @ApiOperation("Course deletion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id")
    })
    public void deleteById(@PathVariable("id") Long id) {
        courseService.delete(id);
    }

    @ApiOperation("Get course information according to condition list")
    @GetMapping("/simpleInfo/list")
    public List<CourseSimpleInfoDTO> getSimpleInfoList(CourseSimpleInfoListDTO courseSimpleInfoListDTO) {
        return courseService.getSimpleInfoList(courseSimpleInfoListDTO);
    }

    @ApiOperation("Get all chapter sequence numbers according to course id")
    @GetMapping("/catas/index/list/{id}")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "Course ID")
    )
    public List<CataSimpleInfoVO> catasIndexList(@PathVariable("id") Long id) {
        return courseCatalogueService.getCatasIndexList(id);
    }

    @ApiOperation("Generate practice id")
    @GetMapping("generator")
    public CourseCataIdVO generator() {
        return new CourseCataIdVO(IdWorker.getId());
    }

    @ApiOperation("Course Management Search Interface")
    @GetMapping("/page")
    public PageDTO<CoursePageVO> queryForPage(CoursePageQuery coursePageQuery) {
        if(CourseStatus.NO_UP_SHELF.equals(coursePageQuery.getStatus()) ||
        CourseStatus.DOWN_SHELF.equals(coursePageQuery.getStatus())){
            //Query Drafts for Courses to be Listed or Already Downgraded
            return courseDraftService.queryForPage(coursePageQuery);
        }else {
            //Query Formal Data for Courses Already Listed or Completed
            return courseService.queryForPage(coursePageQuery);
        }
    }

    @ApiOperation("Check if Course Name Already Exists")
    @GetMapping("/checkName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "name", value = "Course Name")
    })
    public NameExistVO checkNameExist(@RequestParam(value = "id",required = false) Long id,
                                      @RequestParam(value = "name") String name){
        return courseService.checkName(name, id);
    }

    @ApiOperation("Query Course Basic Info, Directory, and Learning Progress")
    @GetMapping("/{id}/catalogs")
    public CourseAndSectionVO queryCourseAndCatalogById(@PathVariable("id") Long courseId){
        return courseService.queryCourseAndCatalogById(courseId);
    }
}
