package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.exam.ExamClient;
import com.learnforge.api.dto.course.CatalogueDTO;
import com.learnforge.api.dto.course.MediaQuoteDTO;
import com.learnforge.api.dto.course.SectionInfoDTO;
import com.learnforge.api.dto.exam.QuestionBizDTO;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.*;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.constants.CourseErrorInfo;
import com.learnforge.course.domain.po.CourseCatalogue;
import com.learnforge.course.domain.vo.CataSimpleInfoVO;
import com.learnforge.course.domain.vo.CataVO;
import com.learnforge.course.mapper.CourseCatalogueMapper;
import com.learnforge.course.properties.CourseProperties;
import com.learnforge.course.service.ICourseCatalogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Draft directory service implementation class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
@Service
public class CourseCatalogueServiceImpl extends ServiceImpl<CourseCatalogueMapper, CourseCatalogue> implements ICourseCatalogueService {

    @Autowired
    private CourseProperties courseProperties;

    @Autowired
    private ExamClient examClient;

    @Override
    public List<CatalogueDTO> queryCourseCatalogues(Long courseId, Boolean withPractice) {
        //1. Course directory query conditions
        LambdaQueryWrapper<CourseCatalogue> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogue.class)
                        .eq(CourseCatalogue::getCourseId, courseId);
        if (!withPractice) {
            //1.1 Course directory without exercise settings query type
            queryWrapper.in(CourseCatalogue::getType,
                    Arrays.asList(CourseConstants.CataType.SECTION,
                            CourseConstants.CataType.CHAPTER));
        }
        //1.2 Sort by directory type and sequence number
        queryWrapper.last(" order by type,c_index");
        //2. Query course directory list
        List<CourseCatalogue> courseCatalogues = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courseCatalogues)) {
            return null;
        }
        //3. Query the number of questions for the course directory
        Set<Long> ids = courseCatalogues.stream().map(CourseCatalogue::getId).collect(Collectors.toSet());
        List<QuestionBizDTO> questionBizDTOS = examClient.queryQuestionIdsByBizIds(ids);
        //4. Convert directory id and question id, score correspondence
        Map<Long, Long> cataIdAndNumMap =
                CollUtils.isEmpty(questionBizDTOS)
                        ? new HashMap<>() :
                        questionBizDTOS
                                .stream()
                                .collect(Collectors.groupingBy(QuestionBizDTO::getBizId, Collectors.counting()));
        // 5. Organize tree structure and return
        return TreeDataUtils.parseToTree(courseCatalogues, CatalogueDTO.class, (courseCatalogue, cataVO)->{
            cataVO.setMediaName(courseCatalogue.getVideoName());
            cataVO.setIndex(courseCatalogue.getCIndex());
            cataVO.setSubjectNum(cataIdAndNumMap.getOrDefault(courseCatalogue.getId(), 0L).intValue());
        }, new CourseCatalogDataWrapper());
    }

    @Override
    public List<MediaQuoteDTO> countMediaUserInfo(List<Long> mediaIds) {
        //1. Check if media id list is empty
        if (CollUtils.isEmpty(mediaIds)) {
            return CollUtils.emptyList();
        }
        //2. Course directory query conditions
        LambdaQueryWrapper<CourseCatalogue> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogue.class)
                .in(CourseCatalogue::getMediaId, mediaIds);
        //2.1 Query Course Catalog List
        List<CourseCatalogue> courseCatalogues = baseMapper.selectList(queryWrapper);
        //3. No Course Catalog Found for Media Asset
        if (CollUtils.isEmpty(courseCatalogues)) {
            //3.1 All Media Assets Are Not Referenced, All Set to Reference Count 0
            return mediaIds.stream()
                    .map(mediaId -> new MediaQuoteDTO(mediaId, 0))
                    .collect(Collectors.toList());
        }
        //4. Group Statistics for Media Asset Reference Count
        Map<Long, Long> mediaAndCount =
                courseCatalogues.stream()
                .collect(Collectors.groupingBy(
                        CourseCatalogue::getMediaId, Collectors.counting()));
        //5. Assemble Data, Set
        return mediaIds.stream().map(
                mediaId -> new MediaQuoteDTO(mediaId,
                        NumberUtils.null2Zero(mediaAndCount.get(mediaId))
                                .intValue())
        ).collect(Collectors.toList());
    }

    @Override
    public SectionInfoDTO getSimpleSectionInfo(Long sectionId) {
        //1. Section ID Null Check
        if (sectionId == null) {
            throw new BizIllegalException(CourseErrorInfo.Msg.CATEGORY_QUERY_ID_NULL);
        }
        //2. Get Section Corresponding Catalog Information
        CourseCatalogue courseCatalogue = baseMapper.selectById(sectionId);
        if (courseCatalogue == null) {
            return new SectionInfoDTO();
        }
        //3. Judge if Catalog Type is Section
        if (courseCatalogue.getType() != CourseConstants.CataType.SECTION) {
            return new SectionInfoDTO();
        }
        //4. Assemble data
        SectionInfoDTO sectionInfoDTO = BeanUtils.toBean(courseCatalogue, SectionInfoDTO.class);
        //5. Set Free Preview Duration
        sectionInfoDTO.setFreeDuration(courseCatalogue.getTrailer() == 1 ?
                courseProperties.getMedia().getTrailerDuration() : 0);
        return sectionInfoDTO;
    }

    @Override
    public List<CataSimpleInfoVO> getCatasIndexList(Long courseId) {
        //1. Course Catalog (Excluding Exercises) Query Conditions
        LambdaQueryWrapper<CourseCatalogue> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogue.class)
                        .eq(CourseCatalogue::getCourseId, courseId)
                        .in(CourseCatalogue::getType, Arrays.asList(
                                CourseConstants.CataType.CHAPTER,
                                CourseConstants.CataType.SECTION
                        ));
        //1.1 Query Course Catalog
        List<CourseCatalogue> courseCatalogues = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courseCatalogues)) {
            return new ArrayList<>();
        }
        //2. Chapter ID and Chapter Number Mapping Relationship
        Map<Long, Integer> chapterMap =
                courseCatalogues
                        .stream()
                        .filter(courseCatalogue -> courseCatalogue.getType() == CourseConstants.CataType.CHAPTER)
                        .collect(Collectors.toMap(CourseCatalogue::getId, CourseCatalogue::getCIndex));
        //3. Traverse Course Catalog, Assemble Data
        return courseCatalogues.stream()
                .filter(courseCatalogue -> courseCatalogue.getType() != CourseConstants.CataType.CHAPTER)

                .map(courseCatalogue -> {
                    //3.1 Assemble Catalog Number
                    String index = StringUtils.format("{}-{}",
                            chapterMap.get(courseCatalogue.getParentCatalogueId()),
                            courseCatalogue.getCIndex());
                    //3.2 Assemble Catalog Information, Catalog ID, Catalog Name, Catalog Number
                    return new CataSimpleInfoVO(courseCatalogue.getId(),
                            courseCatalogue.getName(), index, courseCatalogue.getCIndex(), null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CataSimpleInfoVO> getManyCataSimpleInfo(List<Long> ids) {
        // 1. Null Check
        if(CollUtils.isEmpty(ids)){
            return CollUtils.emptyList();
        }
        //2. Query Conditions
        LambdaQueryWrapper<CourseCatalogue> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogue.class)
                .in(CourseCatalogue::getId, ids);
        //3. Query Data
        List<CourseCatalogue> courseCatalogues = baseMapper.selectList(queryWrapper);
        //4. Data Transformation
        return BeanUtils.copyList(courseCatalogues, CataSimpleInfoVO.class);
    }

    @Override
    public CataSimpleInfoVO querySectionInfoById(Long id) {
        // 1. Query Data
        CourseCatalogue currentCourseCatalogue = baseMapper.selectById(id);
        // 2. Judge if it is a Section
        if(currentCourseCatalogue != null
                && currentCourseCatalogue.getType() == CourseConstants.CataType.SECTION) {
            // 2.1. Transform Data
            CataSimpleInfoVO cataSimpleInfoVO = BeanUtils.toBean(currentCourseCatalogue, CataSimpleInfoVO.class);
            // 3. Query Chapter Information
            CourseCatalogue courseCatalogue = baseMapper.selectById(currentCourseCatalogue.getParentCatalogueId());
            // 3.1. Chapter ID
            cataSimpleInfoVO.setChapterIndex(courseCatalogue.getCIndex());
            return cataSimpleInfoVO;
        }
        // 4. Return Empty Data
        return new CataSimpleInfoVO();
    }

    @Override
    public List<CataVO> queryCourseCataloguesVO(Long courseId, Boolean withPractice) {
        //1. Course directory query conditions
        LambdaQueryWrapper<CourseCatalogue> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogue.class)
                        .eq(CourseCatalogue::getCourseId, courseId);
        if (!withPractice) {
            //1.1 Course directory without exercise settings query type
            queryWrapper.in(CourseCatalogue::getType,
                    Arrays.asList(CourseConstants.CataType.SECTION,
                            CourseConstants.CataType.CHAPTER));
        }
        //1.2 Sort by directory type and sequence number
        queryWrapper.last(" order by type,c_index");
        //2. Query course directory list
        List<CourseCatalogue> courseCatalogues = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courseCatalogues)) {
            return null;
        }

        //3. Query Course Catalog ID, Question ID and Score List
        Set<Long> ids = courseCatalogues.stream().map(CourseCatalogue::getId).collect(Collectors.toSet());
        List<QuestionBizDTO> questionBizDTOS = examClient.queryQuestionIdsByBizIds(ids);
        //4. Convert directory id and question id, score correspondence
        Map<Long, Long> cataIdAndNumMap =
                CollUtils.isEmpty(questionBizDTOS)
                        ? new HashMap<>() :
                        questionBizDTOS
                                .stream()
                                .collect(Collectors.groupingBy(QuestionBizDTO::getBizId, Collectors.counting()));
        //5. Transform Question ID and Total Score Relationship
        Map<Long, Integer> cataIdAndTotalScoreMap = examClient.queryQuestionScoresByBizIds(ids);
        //6. Data Directory Structure Transformation
        List<CataVO> cataVOS =
                TreeDataUtils.parseToTree(courseCatalogues, CataVO.class,
                        (courseCatalogue, cataVO) -> {
                            //6.1 Set Media Asset Name
                            cataVO.setMediaName(courseCatalogue.getVideoName());
                            //6.2 Set Directory Index
                            cataVO.setIndex(courseCatalogue.getCIndex());
                            //6.3 Set Question Quantity
                            cataVO.setSubjectNum(NumberUtils.null2Zero(
                                    cataIdAndNumMap.get(courseCatalogue.getId()))
                                    .intValue()); //Total number of exercises
                            //6.4 Set Total Question Score
                            cataVO.setTotalScore(NumberUtils.null2Zero(
                                    cataIdAndTotalScoreMap.get(courseCatalogue.getId()))); //Total score of exercises
                        }, new CourseCatalogDataWrapper2());

        return cataVOS;
    }

    //Course Catalog Tree Structure Transformation Model
    private static class CourseCatalogDataWrapper implements TreeDataUtils.DataProcessor<CatalogueDTO, CourseCatalogue> {


        @Override
        public Object getParentKey(CourseCatalogue courseCatalogue) {
            return courseCatalogue.getParentCatalogueId();
        }

        @Override
        public Object getKey(CourseCatalogue courseCatalogue) {
            return courseCatalogue.getId();
        }

        @Override
        public Object getRootKey() {
            return 0L;
        }

        @Override
        public List<CatalogueDTO> getChild(CatalogueDTO catalogueDTO) {
            return catalogueDTO.getSections();
        }

        @Override
        public void setChild(CatalogueDTO parent, List<CatalogueDTO> child) {
            parent.setSections(child);
        }
    }

    //Course Catalog Tree Structure Transformation Model
    private static class CourseCatalogDataWrapper2 implements TreeDataUtils.DataProcessor<CataVO, CourseCatalogue> {


        @Override
        public Object getParentKey(CourseCatalogue courseCatalogue) {
            return courseCatalogue.getParentCatalogueId();
        }

        @Override
        public Object getKey(CourseCatalogue courseCatalogue) {
            return courseCatalogue.getId();
        }

        @Override
        public Object getRootKey() {
            return 0L;
        }

        @Override
        public List<CataVO> getChild(CataVO catalogueDTO) {
            return catalogueDTO.getSections();
        }

        @Override
        public void setChild(CataVO parent, List<CataVO> child) {
            parent.setSections(child);
        }
    }
}
