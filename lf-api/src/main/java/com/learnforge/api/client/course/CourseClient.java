package com.learnforge.api.client.course;

import com.learnforge.api.dto.course.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "course", value = "course-service")
public interface CourseClient {

    /**
     * Get teacher question data and teaching data by teacher ID list
     * @param teacherIds Teacher ID list
     * @return Teacher ID and corresponding question count and teaching count
     */
    @GetMapping("/course/infoByTeacherIds")
    List<SubNumAndCourseNumDTO> infoByTeacherIds(@RequestParam("teacherIds") Iterable<Long> teacherIds);

    /**
     * Get the corresponding mediaId and course ID by section ID
     *
     * @param sectionId Section ID
     * @return Section's corresponding mediaId and course ID
     */
    @GetMapping("/course/section/{id}")
    SectionInfoDTO sectionInfo(@PathVariable("id") Long sectionId);

    /**
     * Query the number of times media is referenced by media ID list
     *
     * @param mediaIds Media ID list
     * @return List of media ID and the number of times it's referenced
     */
    @GetMapping("/course/media/useInfo")
    List<MediaQuoteDTO> mediaUserInfo(@RequestParam("mediaIds") Iterable<Long> mediaIds);

    /**
     * Query the data needed for the index library by course ID
     *
     * @param id Course ID
     * @return Data needed for the index library
     */
    @GetMapping("/course/{id}/searchInfo")
    CourseSearchDTO getSearchInfo(@PathVariable("id") Long id);

    /**
     * Query simple course information by course ID collection
     * @param ids ID collection
     * @return List of simple course information
     */
    @GetMapping("/courses/simpleInfo/list")
    List<CourseSimpleInfoDTO> getSimpleInfoList(@RequestParam("ids") Iterable<Long> ids);

    /**
     * Get course, directory, and teacher information by course id
     * @param id Course ID
     * @return course information, directory information, teacher information
     */
    @GetMapping("/course/{id}")
    CourseFullInfoDTO getCourseInfoById(
            @PathVariable("id") Long id,
            @RequestParam(value = "withCatalogue", required = false) boolean withCatalogue,
            @RequestParam(value = "withTeachers", required = false) boolean withTeachers
    );
}