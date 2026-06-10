package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.course.domain.po.CourseCataSubjectDraft;

/**
 * <p>
 * Course-Title Relationship Draft Service Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-09-21
 */
public interface ICourseCataSubjectDraftService extends IService<CourseCataSubjectDraft> {
    /**
     * Delete non-existent course section directory
     * @param courseId
     */
    void deleteNotInCataIdList(Long courseId);
}
