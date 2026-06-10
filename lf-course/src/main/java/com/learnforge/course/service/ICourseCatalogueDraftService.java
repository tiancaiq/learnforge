package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.course.domain.dto.CataSaveDTO;
import com.learnforge.course.domain.dto.CataSubjectDTO;
import com.learnforge.course.domain.dto.CourseMediaDTO;
import com.learnforge.course.domain.po.CourseCatalogueDraft;
import com.learnforge.course.domain.vo.CataSimpleSubjectVO;
import com.learnforge.course.domain.vo.CataVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Catalog Draft Service
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
public interface ICourseCatalogueDraftService extends IService<CourseCatalogueDraft> {

    /**
     * Save Course Catalog Structure
     *
     * @param courseId course id
     * @param cataSaveDTOS Course Catalog Information
     */
    void save(Long courseId, List<CataSaveDTO> cataSaveDTOS, Integer step);

    /**
     * Query Course Catalog
     *
     * @param courseId Course ID Required
     * @param see Whether Used for Data Viewing
     * @return
     */
    List<CataVO> queryCourseCatalogues(Long courseId, Boolean see, Boolean withPractice);

    /**
     * Save Media Resource Information
     */
    void saveMediaInfo(Long courseId, List<CourseMediaDTO> courseMediaDTOS);

    void saveSuject(Long courseId, List<CataSubjectDTO> cataSubjectDTOS);

    /**
     * Get Questions by Course ID for Draft Editing
     *
     * @param courseId
     * @return
     */
    List<CataSimpleSubjectVO> getSuject(Long courseId);

    /**
     * Validate Course Catalog Data Completeness, Including Video, Question
     *
     * @param courseId
     */
    void checkCataInfoImplated(Long courseId);

    /**
     * Copy Question to Shelf
     *
     * @param courseId
     * @param isFirstShelf
     */
    void copySubjectToShelf(Long courseId, Boolean isFirstShelf);

    /**
     * Copy Catalog to Shelf
     *
     * @param courseId
     * @param isFirstShelf
     */
    void copyToShelf(Long courseId, Boolean isFirstShelf);

    /**
     * Calculate Total Media Resource Duration for Each Chapter in Current Course
     *
     * @param courseId
     * @return
     */
    Map<Long, Integer> calculateMediaDuration(Long courseId);

    /**
     * Total Sections and Practice Questions in Course, Excluding Chapters
     *
     * @param courseId
     * @return
     */
    Integer totalSectionNums(Long courseId);

    /**
     * Query Course Section/Chapter/Test ID List by Type
     *
     * @param courseId
     * @param types
     * @return
     */
    List<Long> queryCataIdsOfCourse(Long courseId, List<Integer> types);

}
