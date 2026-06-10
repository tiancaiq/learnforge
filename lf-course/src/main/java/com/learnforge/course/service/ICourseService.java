package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.api.dto.course.*;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.course.domain.dto.CoursePageQuery;
import com.learnforge.course.domain.dto.CourseSimpleInfoListDTO;
import com.learnforge.course.domain.po.Course;
import com.learnforge.course.domain.vo.CourseAndSectionVO;
import com.learnforge.course.domain.vo.CoursePageVO;
import com.learnforge.course.domain.vo.NameExistVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Draft Course Service Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface ICourseService extends IService<Course> {

    /**
     * Modify course status
     *
     * @param id Course ID
     * @param status Course status
     */
    void updateStatus(Long id, Integer status);

    CourseDTO getCourseDTOById(Long id);


    void delete(Long id);

    /**
     * Query course simple information by conditions
     *
     * @param courseSimpleInfoListDTO Course tertiary category list
     * @return Course information list
     */
    List<CourseSimpleInfoDTO> getSimpleInfoList(CourseSimpleInfoListDTO courseSimpleInfoListDTO);

    /**
     * Query the number of questions and courses a teacher has created
     * @param teacherIds Teacher id
     * @return Teacher's question count
     */
    List<SubNumAndCourseNumDTO> countSubjectNumAndCourseNumOfTeacher(List<Long> teacherIds);

    /**
     * Course completion
     */
    int courseFinished();

    /**
     * Count the number of courses in each category id
     * @return Category corresponding course count
     */
    Map<Long, Integer> countCourseNumOfCategory();

    /**
     * Query category id with already published courses
     * @return
     */
    List<Long> getCategoryIdListWithCourse();

    /**
     * Count the number of courses in a specific course category
     *
     * @param categoryId Course category id
     * @return Course count
     */
    Integer countCourseNumOfCategory(Long categoryId);

    /**
     * Query course detailed information
     * @param id Course ID
     * @param withCatalogue Whether to query catalog data
     * @param withTeachers Whether to query teacher data
     * @return Course detailed information
     */
    CourseFullInfoDTO getInfoById(Long id, boolean withCatalogue, boolean withTeachers);

    /**
     * Pagination Query Course Information
     * @param coursePageQuery Pagination parameters
     * @return Course pagination data
     */
    PageDTO<CoursePageVO> queryForPage(CoursePageQuery coursePageQuery);
    /**
     * Query course list by course category id
     * @param categoryId Course category id
     * @param level Course category level
     * @return
     */
    List<Course> queryByCategoryIdAndLevel(Long categoryId, Integer level);

    /**
     * Check if the name exists or is occupied by other courses
     * @param name Course name
     * @param id Current course name
     */
    NameExistVO checkName(String name, Long id);

    /**
     * Query course id list in
     * @param idList
     * @return
     */
    List<Long> queryExists(List<Long> idList,List<Integer> statusList);

    /**
     * Query course id list by name fuzzy match
     * @param name
     * @return
     */
    List<Long> queryCourseIdByName(String name);

    CourseAndSectionVO queryCourseAndCatalogById(Long courseId);
}
