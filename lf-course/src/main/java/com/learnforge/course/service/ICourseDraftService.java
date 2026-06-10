package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.api.dto.course.CourseDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.course.domain.dto.CourseBaseInfoSaveDTO;
import com.learnforge.course.domain.dto.CoursePageQuery;
import com.learnforge.course.domain.po.CourseDraft;
import com.learnforge.course.domain.vo.CourseBaseInfoVO;
import com.learnforge.course.domain.vo.CoursePageVO;
import com.learnforge.course.domain.vo.CourseSaveVO;
import com.learnforge.course.domain.vo.NameExistVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Draft Course Service Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
public interface ICourseDraftService extends IService<CourseDraft> {

    /**
     * Save draft
     *
     * @param courseBaseInfoSaveDTO Course basic information
     */
    CourseSaveVO save(CourseBaseInfoSaveDTO courseBaseInfoSaveDTO);

    /**
     * If used for editing, need to retrieve the already edited content from the draft; if not for editing, directly get the official data
     *
     * @param id Course ID
     * @param see Whether used for viewing data on the page, if not, it's for the editing page
     * @return Course basic information
     */
    CourseBaseInfoVO getCourseBaseInfo(Long id, Boolean see);

    /**
     * Modify the course draft progress step by step, steps can only be incremented, cannot skip or go back
     * @param id Course ID
     * @param step Completed step count
     */
    void updateStep(Long id, Integer step);

    /**
     * Course launch
     *
     * @param id Course publish
     */
    void upShelf(Long id);

    void checkBeforeUpShelf(Long id);

    /**
     * Course shutdown
     *
     * @param id Course ID
     */
    void downShelf(Long id);

    /**
     * Get course search information
     * @param id Course ID
     * @return Course data
     */
    CourseDTO getCourseDTOById(Long id);

    /**
     * Delete course draft
     *
     * @param id Course ID
     */
    void delete(Long id);

    /**
     * Page query by update time
     * @param coursePageQuery Course pagination parameters
     * @return Course pagination data
     */
    PageDTO<CoursePageVO> queryForPage(CoursePageQuery coursePageQuery);

    /**
     * Check if the name exists or is occupied by other courses
     * @param name Course name
     * @param id Current course name
     */
    NameExistVO checkName(String name, Long id);

    /**
     * Query course id list that exists
     * @param idList
     * @return
     */
    List<Long> queryExists(List<Long> idList);

    /**
     * Count the number of courses in the draft course category
     * @return
     */
    Map<Long, Integer> countCourseNumOfCategory();
}
