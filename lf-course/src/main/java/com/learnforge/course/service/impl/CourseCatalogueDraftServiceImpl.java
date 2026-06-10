package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.exam.ExamClient;
import com.learnforge.api.dto.exam.QuestionBizDTO;
import com.learnforge.api.dto.exam.QuestionDTO;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.*;
import com.learnforge.common.validate.Checker;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.constants.CourseErrorInfo;
import com.learnforge.course.constants.CourseStatus;
import com.learnforge.course.domain.dto.CataSaveDTO;
import com.learnforge.course.domain.dto.CataSubjectDTO;
import com.learnforge.course.domain.dto.CourseMediaDTO;
import com.learnforge.course.domain.po.*;
import com.learnforge.course.domain.vo.CataSimpleSubjectVO;
import com.learnforge.course.domain.vo.CataVO;
import com.learnforge.course.mapper.CourseCataSubjectDraftMapper;
import com.learnforge.course.mapper.CourseCataSubjectMapper;
import com.learnforge.course.mapper.CourseCatalogueDraftMapper;
import com.learnforge.course.mapper.CourseCatalogueMapper;
import com.learnforge.course.service.ICourseCataSubjectDraftService;
import com.learnforge.course.service.ICourseCatalogueDraftService;
import com.learnforge.course.service.ICourseCatalogueService;
import com.learnforge.course.service.ICourseDraftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
@Slf4j
public class CourseCatalogueDraftServiceImpl extends ServiceImpl<CourseCatalogueDraftMapper, CourseCatalogueDraft> implements ICourseCatalogueDraftService {

    @Autowired
    private CourseCatalogueMapper courseCatalogueMapper;

    @Autowired
    private ICourseCatalogueService courseCatalogueService;

    @Autowired
    private ICourseDraftService courseDraftService;

    @Autowired
    private CourseCataSubjectDraftMapper courseCataSubjectDraftMapper;

    @Autowired
    private CourseCataSubjectMapper courseCataSubjectMapper;

    @Autowired
    private CourseCatalogueDraftMapper courseCatalogueDraftMapper;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private ICourseCataSubjectDraftService courseCataSubjectDraftService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void save(Long courseId, List<CataSaveDTO> cataSaveDTOS, Integer step) {
        //1. Sort chapters by ascending order of sequence number
        cataSaveDTOS = cataSaveDTOS
                .stream()
                .sorted(Comparator.comparing(CataSaveDTO::getIndex))
                .collect(Collectors.toList());

        //2. Validate chapter sequence number
        if (cataSaveDTOS.size() != cataSaveDTOS
                .stream()
                .map(CataSaveDTO::getIndex)
                .distinct()
                .count()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_INEDX);
        }
        if (cataSaveDTOS.size() < cataSaveDTOS.get(cataSaveDTOS.size() - 1).getIndex()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_INEDX_JUMP);
        }

        //2. Already published directories
        LambdaQueryWrapper<CourseCatalogue> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogue.class)
                        .eq(CourseCatalogue::getCourseId, courseId);
        List<CourseCatalogue> courseCatalogues = courseCatalogueMapper.selectList(queryWrapper);

        //2.1. Validate if published directories have been updated
        checkIndex(cataSaveDTOS, courseCatalogues);
        //3. Assemble data and save to database
        List<CourseCatalogueDraft> courseCatalogueDrafts =
                packageCatalogue(courseId, cataSaveDTOS, courseCatalogues);
        //4. Delete existing directory information
        if (step == CourseConstants.CourseStep.CATALOGUE) {
            //4.1 Delete sections and chapters data
            courseCatalogueDraftMapper.deleteByCourseId(courseId,
                    Arrays.asList(
                            CourseConstants.CataType.CHAPTER,
                            CourseConstants.CataType.SECTION));
        } else if (step == CourseConstants.CourseStep.SUBJECT) {
            //4.2 Save questions when saving directories
            courseCatalogueDraftMapper.deleteByCourseId(courseId,
                    Arrays.asList(
                            CourseConstants.CataType.CHAPTER,
                            CourseConstants.CataType.SECTION,
                            CourseConstants.CataType.PRATICE));
        } else {
            throw new BizIllegalException(ErrorInfo.Msg.OPERATE_FAILED);
        }
        //5. Reinsert directories into draft
        this.saveOrUpdateBatch(courseCatalogueDrafts);

        //6. Update course editing progress
        courseDraftService.updateStep(courseId, CourseConstants.CourseStep.CATALOGUE);

        //7. Delete questions in deleted chapters
        courseCataSubjectDraftService.deleteNotInCataIdList(courseId);
    }

    @Override
    public List<CataVO> queryCourseCatalogues(Long courseId, Boolean see, Boolean withPractice) {
        if (see) {
            //1.1 Query formal directory data
            List<CataVO> cataVOS = courseCatalogueService.queryCourseCataloguesVO(courseId, withPractice);
            if (CollUtils.isNotEmpty(cataVOS)) {
                return cataVOS;
            }
            //1.2 View draft directory
            cataVOS = queryCourseCatalogues(courseId, withPractice);
            return CollUtils.isEmpty(cataVOS) ? new ArrayList<>() : cataVOS;

        } else {
            //2.1 View draft directory
            List<CataVO> cataVOS = queryCourseCatalogues(courseId, withPractice);
            return CollUtils.isEmpty(cataVOS) ? new ArrayList<>() : cataVOS;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void saveMediaInfo(Long courseId, List<CourseMediaDTO> courseMediaDTOS) {
        //1. Validate if the video section id belongs to the current course's sections
        List<Long> cataIds =
                courseMediaDTOS.stream()
                        .map(CourseMediaDTO::getCataId)
                        .collect(Collectors.toList());
        //2. Each section has uploaded media resources
        checkSectionIds(cataIds, courseId);

        //3. Get course draft information
        CourseDraft courseDraft = courseDraftService.getById(courseId);
        //3.1. Determine if new course media resources are uploaded in order
        if (courseDraft == null ||
                courseDraft.getStep() < CourseConstants.CourseStep.CATALOGUE) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_NO_EXECUTE);
        }

        //4. Set media resources to section information
        List<CourseCatalogueDraft> catalogueDrafts =
                BeanUtils.copyList(courseMediaDTOS, CourseCatalogueDraft.class,
                        (dto, courseCatalogueDraft) ->
                                courseCatalogueDraft.setId(dto.getCataId()));
        //4.1. Update section media resources
        this.updateBatchById(catalogueDrafts);
        //4.2. Update course filling steps
        courseDraftService.updateStep(courseId, CourseConstants.CourseStep.MEDIA);
        //5. Calculate total playback duration of media resources in each chapter
        List<CourseCatalogueDraft> courseCatalogueDrafts = calculateCatalogMediaDuration(courseId);
        //5.1. Batch update total number of lessons in each major chapter
        this.updateBatchById(courseCatalogueDrafts, 500);


    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void saveSuject(Long courseId, List<CataSubjectDTO> cataSubjectDTOS) {
        //1. Data validation
        //1.1. Convert directory id list
        List<Long> cataIds = cataSubjectDTOS
                .stream()
                .map(CataSubjectDTO::getCataId)
                .collect(Collectors.toList());
        checkPracticeIds(cataIds, courseId);
        List<CourseCataSubjectDraft> cataSubjectDrafts = new ArrayList<>();

        //2. Query course draft
        CourseDraft courseDraft = courseDraftService.getById(courseId);
        //2.1. Determine if current upload of questions is allowed
        if (courseDraft == null || courseDraft.getStep() < CourseConstants.CourseStep.MEDIA) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_NO_EXECUTE);
        }

        //3. Assemble question directory relationship
        for (CataSubjectDTO cataSubjectDTO : cataSubjectDTOS) {
            for (Long subjectId : cataSubjectDTO.getSubjectIds()) {
                CourseCataSubjectDraft courseCataSubjectDraft = new CourseCataSubjectDraft();
                //3.1. Course id
                courseCataSubjectDraft.setCourseId(courseId);
                //3.2. Question id
                courseCataSubjectDraft.setSubjectId(subjectId);
                //3.3. Course directory
                courseCataSubjectDraft.setCataId(cataSubjectDTO.getCataId());
                if (courseCataSubjectDraft.getId() == null) {
                    courseCataSubjectDraft.setId(IdWorker.getId());
                }
                //3.4. Add course question relationship to
                cataSubjectDrafts.add(courseCataSubjectDraft);
            }
        }
        //4. Delete relationship between practice and questions
        courseCataSubjectDraftMapper.deleteByCourseId(courseId);
        //5. Batch insert relationship between practice and questions
        if (!cataSubjectDrafts.isEmpty()) {
            courseCataSubjectDraftMapper.batchInsert(cataSubjectDrafts);
        }
        //6. Update course filling progress
        courseDraftService.updateStep(courseId, CourseConstants.CourseStep.SUBJECT);
    }

    @Override
    public List<CataSimpleSubjectVO> getSuject(Long courseId) {

        //1. Query course directory and question relationships
        List<CourseCataSubjectDraft> cataSubjectDrafts = courseCataSubjectDraftMapper.getByCourseId(courseId);
        if (CollUtils.isEmpty(cataSubjectDrafts)) {
            return new ArrayList<>();
        }
        List<Long> subjectIdList = cataSubjectDrafts.stream().map(CourseCataSubjectDraft::getSubjectId).collect(Collectors.toList());
        List<QuestionDTO> subjects = examClient.queryQuestionByIds(subjectIdList);
        Map<Long, String> subjectIdAndNameMap = subjects.stream()
                .collect(Collectors.toMap(QuestionDTO::getId, QuestionDTO::getName));

        //4. Assemble data
        return cataSubjectDrafts.stream()
                //4.1. Grouping
                .collect(Collectors.groupingBy(CourseCataSubjectDraft::getCataId))
                .entrySet().stream().map(entry -> {
                    //4.2. List of questions corresponding to sections or tests
                    List<CataSimpleSubjectVO.SubjectInfo> subjectInfos = new ArrayList<>();
                    for (CourseCataSubjectDraft cataSubjectDraft : entry.getValue()) {
                        //4.3. Add question id and name to the question list of sections or tests
                        subjectInfos.add(new CataSimpleSubjectVO.SubjectInfo(
                                cataSubjectDraft.getSubjectId(),
                                subjectIdAndNameMap.get(cataSubjectDraft.getSubjectId())));
                    }
                    //4.4. Assemble model for question list corresponding to sections or tests
                    return new CataSimpleSubjectVO(entry.getKey(), subjectInfos);
                }).collect(Collectors.toList());
    }

    @Override
    public void checkCataInfoImplated(Long courseId) {
        //Query all directories
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCatalogueDraft::getCourseId, courseId);
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.selectList(queryWrapper);

        List<CourseCataSubjectDraft> courseCataSubjectDrafts = courseCataSubjectDraftMapper.getByCourseId(courseId);

        Map<Long, Long> subjectNumMap = CollUtils.isEmpty(courseCataSubjectDrafts) ? new HashMap<>() :
                courseCataSubjectDrafts.stream().collect(
                        Collectors.groupingBy(CourseCataSubjectDraft::getCataId,
                                Collectors.counting()));

        //Validate if practice or sections have uploaded questions and media resources
        CollUtils.check(courseCatalogueDrafts, new Checker<CourseCatalogueDraft>() {
            @Override
            public void check(CourseCatalogueDraft courseCatalogueDraft) {
                if (courseCatalogueDraft.getType() == CourseConstants.CataType.SECTION
                        && StringUtils.isEmpty(courseCatalogueDraft.getVideoName())) { //Section has not uploaded video
                    throw new BizIllegalException(
                            StringUtils.format(CourseErrorInfo.Msg.COURSE_UP_SHELF_SECTION_WITHOUT_MEDIA, courseCatalogueDraft.getName()));
                } else if (courseCatalogueDraft.getType() == CourseConstants.CataType.PRATICE
                        && NumberUtils.null2Zero(subjectNumMap.get(courseCatalogueDraft.getId())) <= 0) { //Practice has not added questions
                    throw new BizIllegalException(
                            StringUtils.format(CourseErrorInfo.Msg.COURSE_UP_SHELF_PRACTICE_WITHOUT_SUBJECT,
                                    courseCatalogueDraft.getName())
                    );
                }
            }
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void copySubjectToShelf(Long courseId, Boolean isFirstShelf) {
        //1. Retrieve question information from draft
        List<CourseCataSubjectDraft> courseCataSubjectDrafts = courseCataSubjectDraftMapper.getByCourseId(courseId);
        List<QuestionBizDTO> subjects = courseCataSubjectDrafts.stream()
                .map(s -> QuestionBizDTO.of(s.getCataId(), s.getSubjectId())).collect(Collectors.toList());
        //2. Delete relationship between practice and questions
        if (CollUtils.isEmpty(courseCataSubjectDrafts)) {
            // No questions in draft, end directly
            return;
        }
        //3. Upload new relationship between practice and questions to production
        examClient.saveQuestionBizInfoBatch(subjects);
        //4. Delete draft
        int result = courseCataSubjectDraftMapper.deleteByCourseId(courseId);
        if (result != courseCataSubjectDrafts.size()) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void copyToShelf(Long courseId, Boolean isFirstShelf) {
        //1. Retrieve directory information from draft
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.getByCourseId(courseId);
        List<CourseCatalogue> courseCatalogues = BeanUtils.copyList(courseCatalogueDrafts, CourseCatalogue.class);
        //2. Save data to go live
        boolean result = courseCatalogueService.saveOrUpdateBatch(courseCatalogues);
        if (!result) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
        //3. Delete draft
        int deleteResult = courseCatalogueDraftMapper.deleteByCourseId(courseId, Arrays.asList(
                CourseConstants.CataType.CHAPTER,
                CourseConstants.CataType.SECTION,
                CourseConstants.CataType.PRATICE
        ));
        if (deleteResult != courseCatalogueDrafts.size()) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    public Map<Long, Integer> calculateMediaDuration(Long courseId) {
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogueDraft.class)
                        .eq(CourseCatalogueDraft::getCourseId, courseId)
                        .eq(CourseCatalogueDraft::getType, CourseConstants.CataType.SECTION);
        List<CourseCatalogueDraft> list = list(queryWrapper);
        if (CollUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        CourseCatalogueDraft::getParentCatalogueId,
                        Collectors.summingInt(CourseCatalogueDraft::getMediaDuration)));
    }

    @Override
    public Integer totalSectionNums(Long courseId) {
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCatalogueDraft::getCourseId, courseId)
                .in(CourseCatalogueDraft::getType,
                        Arrays.asList(CourseConstants.CataType.SECTION, CourseConstants.CataType.PRATICE));
        return count(queryWrapper);
    }

    @Override
    public List<Long> queryCataIdsOfCourse(Long courseId, List<Integer> types) {
        //1. Query conditions
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogueDraft.class)
                        .eq(CourseCatalogueDraft::getCourseId, courseId)
                        .in(CourseCatalogueDraft::getType, types);
        //2. Query data
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.selectList(queryWrapper);
        //3. Return Data
        return CollUtils.isEmpty(courseCatalogueDrafts)
                ? new ArrayList<>()
                : courseCatalogueDrafts
                .stream()
                .map(CourseCatalogueDraft::getId)
                .collect(Collectors.toList());
    }


    /**
     * Validate whether chapters of already live courses have been moved or deleted
     *
     * @param courseCatalogues Already live
     * @param cataSaveDTOS     Data to be saved from frontend
     */
    private void checkIndex(List<CataSaveDTO> cataSaveDTOS, List<CourseCatalogue> courseCatalogues) {

        //Validate whether chapter sequence numbers are duplicated
        Map<Integer, CataSaveDTO> collect = cataSaveDTOS.stream().collect(Collectors.toMap(CataSaveDTO::getIndex, p -> p));
        if (collect.size() < cataSaveDTOS.size()) { //Duplicate chapter sequence numbers
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_CHAPTER_INDEX_REPEAT);
        }
        //cataSaveDTOS is sorted in ascending order. The largest chapter sequence number being greater than the number of chapters indicates that the sequence numbers in cataSaveDTOS have gaps
        if (cataSaveDTOS.get(cataSaveDTOS.size() - 1).getIndex() > cataSaveDTOS.size()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_CHAPTER_INDEX_INTERRUPTED);
        }

        if (CollUtils.isEmpty(courseCatalogues)) {
            return;
        }
        final Map<Long, Integer> saveIndexMap = new HashMap<>();
        for (CataSaveDTO cataSaveDTO : cataSaveDTOS) {//Chapter sequence number
            if (cataSaveDTO.getId() != null) {
                saveIndexMap.put(cataSaveDTO.getId(), cataSaveDTO.getIndex());
            }

            //Section and exercise sequence numbers
            if (CollUtils.isEmpty(cataSaveDTO.getSections())) { //Chapter has no corresponding sections
                throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_CHAPTER_WITHOUT_SECTION);
            }
            AtomicInteger count = new AtomicInteger(1);
            //Sections need to be sorted, exercises do not need to be sorted
            cataSaveDTO.getSections().stream().filter(section -> section.getType() == CourseConstants.CataType.SECTION)
                    .forEach(section -> {
                        if (section.getId() == null) {
                            //Sequence numbers start from 1
                            saveIndexMap.put(section.getId(), count.incrementAndGet());
                        }
                    });
        }

        for (CourseCatalogue courseCatalogue : courseCatalogues) {
            if (courseCatalogue.getType() != CourseConstants.CataType.CHAPTER) {
                continue;
            }
            Integer index = saveIndexMap.get(courseCatalogue.getId());
            if (index == null) {
                throw new BizIllegalException(StringUtils.format(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_CHAPTER_NAME_DELETED, courseCatalogue.getName()));
            }
            //Chapter sorting
            if (!index.equals(courseCatalogue.getCIndex())) {
                throw new BizIllegalException(StringUtils.format(ErrorInfo.Msg.OPERATE_FAILED, courseCatalogue.getName()));
            }
        }
    }

    /**
     * Assemble data, data priority: current save data > draft data > already live data
     * Use map to achieve data override, map key is directory id, value is directory information
     * 1. First, put already live data into map
     * 2. Put draft data into map. If both draft and already live data exist, draft data will automatically override live data
     * 3. Traverse current save data
     * 3.1 If directory information has no id, generate one
     * 3.2 Get directory information from map, if not found, generate a directory draft class
     * 3.3 Add the data to be saved to directory information, including sequence number, name, section id, directory id
     *
     * @param courseId     Course id
     * @param cataSaveDTOS Course directory list
     * @return List of course directories stored in the database
     */
    private List<CourseCatalogueDraft> packageCatalogue(Long courseId, List<CataSaveDTO> cataSaveDTOS, List<CourseCatalogue> shelfCourseCatalogues) {
        final Map<Long, CourseCatalogueDraft> savedMap = new HashMap<>();
        if (CollUtils.isNotEmpty(shelfCourseCatalogues)) {
            //Copy already live directories
            shelfCourseCatalogues.stream().forEach(courseCatalogue ->
                    savedMap.put(courseCatalogue.getId(), BeanUtils.toBean(courseCatalogue, CourseCatalogueDraft.class)));
        }
        //Get saved draft
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCatalogueDraft::getCourseId, courseId);
        List<CourseCatalogueDraft> savedCourseCataloguesDraft = list(queryWrapper);
        if (CollUtils.isNotEmpty(savedCourseCataloguesDraft)) {
            //Existing draft overrides already live course
            savedCourseCataloguesDraft.stream().forEach(courseCatalogueDraft ->
                    savedMap.put(courseCatalogueDraft.getId(), courseCatalogueDraft));
        }
        //Data to be saved as draft
        List<CourseCatalogueDraft> courseCatalogueDrafts = new ArrayList<>();
        for (CataSaveDTO cataSaveDTO : cataSaveDTOS) {
            Long chapterId = cataSaveDTO.getId() == null ? IdWorker.getId() : cataSaveDTO.getId();
            //Chapter directory
            CourseCatalogueDraft courseCatalogueDraft = savedMap.get(chapterId);
            if (courseCatalogueDraft == null) { //Not saved yet
                courseCatalogueDraft = new CourseCatalogueDraft();
                courseCatalogueDraft.setId(chapterId);
            }

            //Set basic information when adding or modifying directory
            courseCatalogueDraft.setCataBaseInfo(cataSaveDTO.getIndex(), cataSaveDTO.getName(), CourseConstants.CataType.CHAPTER,
                    0L, courseId);
            courseCatalogueDrafts.add(courseCatalogueDraft);
            //Section and exercise directory, sections need it, exercises do not
            //Sequence number
            AtomicInteger indexCount = new AtomicInteger(0);
            cataSaveDTO.getSections().stream().forEach(section -> {
                Long sectionId = section.getId() == null ? IdWorker.getId() : section.getId();
                //Section and exercise directory
                CourseCatalogueDraft courseCatalogueDraftSection = savedMap.get(sectionId);
                if (courseCatalogueDraftSection == null) { //Not saved yet
                    courseCatalogueDraftSection = new CourseCatalogueDraft();
                    courseCatalogueDraftSection.setId(sectionId);
                }
                Integer index = section.getType() == CourseConstants.CataType.SECTION ?
                        indexCount.incrementAndGet() : null;

                //Set basic information when adding or modifying directory
                courseCatalogueDraftSection.setCataBaseInfo(index, section.getName(), section.getType(),
                        chapterId, courseId);
                courseCatalogueDrafts.add(courseCatalogueDraftSection);
            });
        }
        return courseCatalogueDrafts;
    }

    private List<CataVO> queryCourseCatalogues(Long courseId, Boolean withPractice) {
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCatalogueDraft::getCourseId, courseId);
        if (!withPractice) {
            queryWrapper.in(CourseCatalogueDraft::getType,
                    Arrays.asList(CourseConstants.CataType.CHAPTER, CourseConstants.CataType.SECTION));
        }
        //According to type and sorting, sort by
        queryWrapper.last(" order by type,c_index");
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courseCatalogueDrafts)) {
            return null;
        }
        CourseDraft courseDraft = courseDraftService.getById(courseId);

        // Maximum number of live chapters, pending live set empty map, live chapters need to be sorted and remove section sequence numbers (within the same chapter) to get the maximum section
        Map<Long, CourseCatalogueDraft> chapterIdAndMaxSectionMap =
                (courseDraft.getStatus() == CourseStatus.NO_UP_SHELF.getStatus())
                        ? new HashMap<>() :
                        courseCatalogueDrafts.parallelStream()
                                .filter(ccd -> ccd.getType() == CourseConstants.CataType.SECTION && !ccd.getCanUpdate())
                                .collect(Collectors.groupingBy(CourseCatalogueDraft::getParentCatalogueId,
                                        Collectors.collectingAndThen(
                                                Collectors.reducing(
                                                        (c1, c2) -> c2.getCIndex().compareTo(c1.getCIndex()) > 0 ? c2 : c1),
                                                Optional::get)));
        int maxChapterIndex = (courseDraft.getStatus() == CourseStatus.NO_UP_SHELF.getStatus())
                ? 0
                : courseCatalogueDrafts.stream()
                .filter(ccd -> ccd.getType() == CourseConstants.CataType.CHAPTER && !ccd.getCanUpdate())
                .map(CourseCatalogueDraft::getCIndex)
                .max(Integer::compare).get();


        // 4. Query course corresponding sections and questions information
        List<CourseCataSubjectDraft> subjects = courseCataSubjectDraftMapper.getByCourseId(courseId);
        // 4.1. Count question quantity
        Map<Long, Long> cataIdAndNumMap = CollUtils.isEmpty(subjects) ? new HashMap<>() :
                subjects.stream().collect(Collectors.groupingBy(CourseCataSubjectDraft::getCataId, Collectors.counting()));
        // 4.2. Query score
        Map<Long, Integer> cataIdAndTotalScoreMap = new HashMap<>(cataIdAndNumMap.size());
        if (CollUtils.isNotEmpty(subjects)) {
            Set<Long> questionIds = subjects.stream().map(CourseCataSubjectDraft::getSubjectId).collect(Collectors.toSet());
            Map<Long, Integer> scoreMap = examClient.queryQuestionScores(questionIds);
            cataIdAndTotalScoreMap.putAll(
                    subjects.stream().collect(Collectors.groupingBy(
                            CourseCataSubjectDraft::getCataId,
                            Collectors.summingInt(d -> scoreMap.get(d.getSubjectId()))
                    )));
        }
        return TreeDataUtils.parseToTree(courseCatalogueDrafts, CataVO.class, (catalogueDraft, vo) -> {
            int maxIndexOnShelf = 0;
            int maxSectionIndexOnShelf = 0;
            if (catalogueDraft.getType() == CourseConstants.CataType.SECTION) {
                //Maximum number of edits for a section
                CourseCatalogueDraft courseCatalogueDraft = chapterIdAndMaxSectionMap.get(catalogueDraft.getParentCatalogueId());
                maxIndexOnShelf = NumberUtils.null2Zero(
                        courseCatalogueDraft == null ? 0 : courseCatalogueDraft.getCIndex());
            } else if (catalogueDraft.getType() == CourseConstants.CataType.CHAPTER) {
                maxIndexOnShelf = maxChapterIndex;
                CourseCatalogueDraft courseCatalogueDraft = chapterIdAndMaxSectionMap.get(catalogueDraft.getId());
                maxSectionIndexOnShelf = NumberUtils.null2Zero(
                        courseCatalogueDraft == null ? 0 : courseCatalogueDraft.getCIndex());
            }
            vo.setIndex(catalogueDraft.getCIndex());
            vo.setMediaName(catalogueDraft.getVideoName());
            vo.setSubjectNum(NumberUtils.null2Zero(cataIdAndNumMap.get(catalogueDraft.getId())).intValue()); //Total number of exercises
            vo.setTotalScore(NumberUtils.null2Zero(cataIdAndTotalScoreMap.get(catalogueDraft.getId()))); //Total score of exercises
            vo.setMaxIndexOnShelf(maxIndexOnShelf);
            vo.setMaxSectionIndexOnShelf(maxSectionIndexOnShelf);
        }, new CourseCatalogDraftDataWrapper());
    }

    /**
     * Validate exercise id list
     * 1. Validate that all exercises in this course have added questions
     * 2. Validate that the uploaded data contains data from other courses
     * Frontend passed chapter id list A, course all section id and exercise id list B, course all exercise id list C
     * Principle: 1. Check if A is a subset of B. If A is not a subset, the frontend passed id list contains sections or exercises not belonging to the current course
     * 2. Check if C is a subset of A. If C is not a subset, it means the frontend missed some exercise id of the current course
     *
     * @param cataIds  Frontend passed section id list or exercise list
     * @param courseId course id
     */
    private void checkPracticeIds(List<Long> cataIds, Long courseId) {
        //Query all sections and exercises directory list
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCatalogueDraft::getCourseId, courseId).in(CourseCatalogueDraft::getType,
                Arrays.asList(CourseConstants.CataType.SECTION, CourseConstants.CataType.PRATICE));
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courseCatalogueDrafts)) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_CATA_NOT_EXISTS);
        }
        //All section and exercise directory id list
        List<Long> allCataIdList = courseCatalogueDrafts
                .stream()
                .map(CourseCatalogueDraft::getId)
                .collect(Collectors.toList());

        //Check if the frontend passed chapter id list is a subset of all sections and exercises of the course
        // If not a subset, it means the frontend passed chapter id does not belong to the current course
        if (!CollUtils.containsAll(allCataIdList, cataIds)) {
            log.error("Frontend passed other chapter id, courseId:{},cataIds:{}", courseId, cataIds);
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_ILLEGAL);
        }
        //All exercise id list
        List<Long> practiceIdList = courseCatalogueDrafts.stream().filter(practice -> practice.getType() == CourseConstants.CataType.PRATICE).
                map(CourseCatalogueDraft::getId).collect(Collectors.toList());
        //Check if the frontend passed chapter id list is a superset of the course's exercise id list
        if (!CollUtils.containsAll(cataIds, practiceIdList)) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_SUBJECT_SAVE_SUBJECT_IDS_NULL);
        }
    }

    /**
     * Validate section or exercise id list
     * 1. Validate that all sections in this course have added videos
     * 2. Validate that the uploaded data contains data from other courses
     * Principle: 1. Compare the data passed by the interface with the total number of sections in the database. If they are inconsistent, return failure
     * 2. If the total numbers are consistent, calculate the intersection of both sets. If the length of the intersection is the same as the number of ids passed by the frontend, it is successful
     *
     * @param cataIds  Frontend passed section id list or exercise list
     * @param courseId course id
     */
    private void checkSectionIds(List<Long> cataIds, Long courseId) {
        //1. Database section query conditions
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogueDraft.class)
                        .eq(CourseCatalogueDraft::getType, CourseConstants.CataType.SECTION)
                        .eq(CourseCatalogueDraft::getCourseId, courseId);
        //2. Query sections
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.selectList(queryWrapper);

        //3. Check if the number of sections in the database matches the number passed by the frontend
        if (CollUtils.size(courseCatalogueDrafts) != CollUtils.size(cataIds)) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL);
        }
        //4. Convert to the list of section ids in the database
        List<Long> cataIdsInDb =
                courseCatalogueDrafts
                        .stream()
                        .map(CourseCatalogueDraft::getId)
                        .collect(Collectors.toList());
        //5. Take the intersection of the frontend passed section id list and the database section id list
        Collection<Long> cataIdsOfIntersection = CollUtils.intersection(cataIds, cataIdsInDb);
        //6. Check if the number of section ids in the intersection matches the number of ids passed by the frontend
        if (cataIdsOfIntersection.size() != cataIds.size()) {
            throw new BizIllegalException(CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL);
        }
    }

    /**
     * According to course id, count the total duration of media resources for each major chapter
     *
     * @param courseId
     * @return
     */
    private List<CourseCatalogueDraft> calculateCatalogMediaDuration(Long courseId) {
        //1. Query conditions
        LambdaQueryWrapper<CourseCatalogueDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseCatalogueDraft.class)
                        .eq(CourseCatalogueDraft::getCourseId, courseId)
                        .eq(CourseCatalogueDraft::getType, CourseConstants.CataType.SECTION);
        //2. Query data
        List<CourseCatalogueDraft> courseCatalogueDrafts = baseMapper.selectList(queryWrapper);
        if (CollUtils.isEmpty(courseCatalogueDrafts)) {
            return new ArrayList<>();
        }
        //3. Count the total duration of lessons for each chapter
        Map<Long, Integer> capthIdAndMediaDurationMap =
                courseCatalogueDrafts.stream().collect(Collectors.groupingBy(
                        CourseCatalogueDraft::getParentCatalogueId,
                        Collectors.summingInt(CourseCatalogueDraft::getMediaDuration)));
        //4. Package data
        return capthIdAndMediaDurationMap.keySet()
                .stream()
                .map(key -> CourseCatalogueDraft.builder()
                        .id(key)
                        .mediaDuration(capthIdAndMediaDurationMap.get(key))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Course directory draft tree data converter
     */
    private class CourseCatalogDraftDataWrapper implements TreeDataUtils.DataProcessor<CataVO, CourseCatalogueDraft> {

        @Override
        public Object getParentKey(CourseCatalogueDraft courseCatalogueDraft) {
            return courseCatalogueDraft.getParentCatalogueId();
        }

        @Override
        public Object getKey(CourseCatalogueDraft courseCatalogueDraft) {
            return courseCatalogueDraft.getId();
        }

        @Override
        public Object getRootKey() {
            return 0L;
        }

        @Override
        public List<CataVO> getChild(CataVO cataVO) {
            return cataVO.getSections();
        }

        @Override
        public void setChild(CataVO parent, List<CataVO> child) {
            parent.setSections(child);
        }
    }
}
