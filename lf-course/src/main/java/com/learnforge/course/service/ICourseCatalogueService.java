package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.api.dto.course.CatalogueDTO;
import com.learnforge.api.dto.course.MediaQuoteDTO;
import com.learnforge.api.dto.course.SectionInfoDTO;
import com.learnforge.course.domain.po.CourseCatalogue;
import com.learnforge.course.domain.vo.CataSimpleInfoVO;
import com.learnforge.course.domain.vo.CataVO;

import java.util.List;

/**
 * <p>
 * Catalog Draft Service
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
public interface ICourseCatalogueService extends IService<CourseCatalogue> {

    /**
     * Query Online Course Catalog
     *
     * @param courseId course id
     * @return Course Catalog
     */
    List<CatalogueDTO> queryCourseCatalogues(Long courseId, Boolean withPractice);

    /**
     * Batch Count Media Resource ID Reference Times
     *
     * @param mediaIds Media Resource ID
     * @return media reference count
     */
    List<MediaQuoteDTO> countMediaUserInfo(List<Long> mediaIds);

    /**
     * Get simple section information
     *
     * @param sectionId Section ID
     * @return course id, media id, whether free preview is supported, free preview duration
     */
    SectionInfoDTO getSimpleSectionInfo(Long sectionId);

    /**
     * Get course directory list by course id
     *
     * @param courseId course id
     * @return course directory list
     */
    List<CataSimpleInfoVO> getCatasIndexList(Long courseId);

    List<CataSimpleInfoVO> getManyCataSimpleInfo(List<Long> ids);

    CataSimpleInfoVO querySectionInfoById(Long id);

    List<CataVO> queryCourseCataloguesVO(Long courseId, Boolean withPractice);
}
