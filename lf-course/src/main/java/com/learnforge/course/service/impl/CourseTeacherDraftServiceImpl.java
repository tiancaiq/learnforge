package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.domain.dto.CourseTeacherSaveDTO;
import com.learnforge.course.domain.po.CourseTeacher;
import com.learnforge.course.domain.po.CourseTeacherDraft;
import com.learnforge.course.domain.vo.CourseTeacherVO;
import com.learnforge.course.mapper.CourseTeacherDraftMapper;
import com.learnforge.course.service.ICourseDraftService;
import com.learnforge.course.service.ICourseTeacherDraftService;
import com.learnforge.course.service.ICourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Course Teacher Relationship Draft Service Implementation Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Service
public class CourseTeacherDraftServiceImpl extends ServiceImpl<CourseTeacherDraftMapper, CourseTeacherDraft> implements ICourseTeacherDraftService {

    @Autowired
    private ICourseDraftService courseDraftService;

    @Autowired
    private ICourseTeacherService courseTeacherService;

    @Autowired
    private UserClient userClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(CourseTeacherSaveDTO courseTeacherSaveDTO) {

        //1. Data Deletion Conditions
        LambdaUpdateWrapper<CourseTeacherDraft> updateWrapper =
                Wrappers.lambdaUpdate(CourseTeacherDraft.class)
                        .eq(CourseTeacherDraft::getCourseId, courseTeacherSaveDTO.getId());
        //1.1. Data Deletion
        baseMapper.delete(updateWrapper);

        //2. Assemble Data to be Inserted
        List<CourseTeacherDraft> courseTeacherDrafts =
                BeanUtils.copyList(courseTeacherSaveDTO.getTeachers(),
                        CourseTeacherDraft.class, (teacherInfo, teacherDraft) -> {
                            //2.1. Set Course ID
                            teacherDraft.setCourseId(courseTeacherSaveDTO.getId());
                            //2.2. Set Teacher ID
                            teacherDraft.setTeacherId(teacherInfo.getId());
                            //2.3. Set Teacher Sort Order in Course
                            teacherDraft.setCIndex(courseTeacherSaveDTO.getTeachers().indexOf(teacherInfo));
                        });
        //3. Batch Insert Course Teacher Information
        saveBatch(courseTeacherDrafts);
        //4. Update Course Fill Progress
        courseDraftService.updateStep(courseTeacherSaveDTO.getId(), CourseConstants.CourseStep.TEACHER);
    }

    @Override
    public List<CourseTeacherVO> queryTeacherOfCourse(Long courseId, Boolean see) {
        if (see) {
            //1. Query Course Teacher Relationship
            List<CourseTeacherVO> courseTeacherVOS = courseTeacherService.queryTeachers(courseId);
            //1.1. Check if Course Teacher Relationship is Not Null
            if (CollUtils.isNotEmpty(courseTeacherVOS)) {
                return courseTeacherVOS;
            }
            //2. Query Course Teacher Relationship from Draft
            courseTeacherVOS = queryTeachers(courseId);
            //3. Assemble data
            return CollUtils.isEmpty(courseTeacherVOS) ? new ArrayList<>() : courseTeacherVOS;
        } else {
            //4. Query Course Teacher Relationship from Draft
            return queryTeachers(courseId);
        }
    }

    @Override
    @Transactional(rollbackFor = {DbException.class, Exception.class})
    public void copyToShelf(Long courseId, Boolean isFirstShelf) {
        //1. First Query the Draft Data
        LambdaQueryWrapper<CourseTeacherDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacherDraft::getCourseId, courseId);
        List<CourseTeacherDraft> courseTeacherDrafts = baseMapper.selectList(queryWrapper);
        //2. Delete Existing Teacher Data
        if (!isFirstShelf) {
            courseTeacherService.deleteByCourseId(courseId);
        }
        //3. Upload Draft to Production
        List<CourseTeacher> courseTeachers = BeanUtils.copyList(courseTeacherDrafts, CourseTeacher.class);
        courseTeacherService.saveOrUpdateBatch(courseTeachers);
        //4. Delete draft
        if (baseMapper.deleteByCourseId(courseId) <= 0) {
            throw new DbException(ErrorInfo.Msg.DB_DELETE_EXCEPTION);
        }
    }

    private List<CourseTeacherVO> queryTeachers(Long couserId) {

        //1. Query conditions
        LambdaQueryWrapper<CourseTeacherDraft> queryWrapper =
                Wrappers.lambdaQuery(CourseTeacherDraft.class)
                        .eq(CourseTeacherDraft::getCourseId, couserId);
        //1.1. Query Data
        List<CourseTeacherDraft> courseTeacherDrafts = baseMapper.selectList(queryWrapper);
        //1.2. Check Data for Null
        if (CollUtils.isEmpty(courseTeacherDrafts)) {
            return new ArrayList<>();
        }

        // 2. Query Teacher Detailed Information
        List<UserDTO> UserDTOS = userClient.queryUserByIds(
                courseTeacherDrafts.stream().map(CourseTeacherDraft::getTeacherId).collect(Collectors.toList()));
        // 3. Organize as a Map
        Map<Long, UserDTO> UserDTOMap = UserDTOS.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO -> UserDTO));

        //4. Data Assembly
        return BeanUtils.copyList(courseTeacherDrafts, CourseTeacherVO.class,
                (courseTeacher, courseTeacherVO) -> {
            //4.1. Teacher Information
                    UserDTO teacherDetailDTO = UserDTOMap.get(courseTeacher.getTeacherId());
            if (teacherDetailDTO != null) {
                //4.2. Set Teacher Avatar
                courseTeacherVO.setIcon(teacherDetailDTO.getIcon());
                courseTeacherVO.setPhoto(teacherDetailDTO.getPhoto());
                //4.3. Set Teacher Name
                courseTeacherVO.setName(teacherDetailDTO.getName());
                //4.4. Set Teacher Introduction
                courseTeacherVO.setIntroduce(teacherDetailDTO.getIntro());
                //4.5. Set Teacher Profession
                courseTeacherVO.setJob(teacherDetailDTO.getJob());
            }
            //4.6. Set Teacher ID
            courseTeacherVO.setId(courseTeacher.getTeacherId());
        });
    }

}
