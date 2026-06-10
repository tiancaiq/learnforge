package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.domain.po.CourseCataSubjectDraft;
import com.learnforge.course.mapper.CourseCataSubjectDraftMapper;
import com.learnforge.course.service.ICourseCataSubjectDraftService;
import com.learnforge.course.service.ICourseCatalogueDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Course-Question Relationship Table Draft Service Implementation Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-09-21
 */
@Service
public class CourseCataSubjectDraftServiceImpl extends ServiceImpl<CourseCataSubjectDraftMapper, CourseCataSubjectDraft> implements ICourseCataSubjectDraftService {

    @Autowired
    private ICourseCatalogueDraftService courseCatalogueDraftService;

    @Override
    @Transactional
    public void deleteNotInCataIdList(Long courseId) {

        //1. Get All Course Sections and Exercises
        List<Long> cataIdList = courseCatalogueDraftService.queryCataIdsOfCourse(courseId,
                Arrays.asList(
                        CourseConstants.CataType.SECTION,
                        CourseConstants.CataType.PRATICE
                ));
        //1. Delete Conditions
        LambdaUpdateWrapper<CourseCataSubjectDraft> updateWrapper =
                Wrappers.lambdaUpdate(CourseCataSubjectDraft.class)
                        .eq(CourseCataSubjectDraft::getCourseId, courseId)
                        .notIn(CollUtils.isNotEmpty(cataIdList),
                                CourseCataSubjectDraft::getCataId, cataIdList);
        //2. Delete Question
        baseMapper.delete(updateWrapper);
    }
}
