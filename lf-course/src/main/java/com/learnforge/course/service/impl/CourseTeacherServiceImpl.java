package com.learnforge.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.course.domain.po.CourseTeacher;
import com.learnforge.course.domain.vo.CourseTeacherVO;
import com.learnforge.course.mapper.CourseTeacherMapper;
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
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements ICourseTeacherService {

    @Autowired
    private UserClient userClient;

    @Override
    public List<CourseTeacherVO> queryTeachers(Long couserId) {
        //1. Query conditions
        LambdaQueryWrapper<CourseTeacher> queryWrapper =
                Wrappers.lambdaQuery(CourseTeacher.class)
                        .eq(CourseTeacher::getCourseId, couserId);
        //2. Query data
        List<CourseTeacher> courseTeachers = baseMapper.selectList(queryWrapper);
        //2.1. Data Null Check
        if (CollUtils.isEmpty(courseTeachers)) {
            return null;
        }
        //3. Query Teacher Information
        List<UserDTO> teacherDetailDTOS =
                userClient.queryUserByIds(
                        courseTeachers
                                .stream()
                                .map(CourseTeacher::getTeacherId)
                                .collect(Collectors.toList()));
        //3.1. Teacher ID + Teacher Information Map
        Map<Long, UserDTO> teacherDetailDTOMap =
                teacherDetailDTOS
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId,
                                TeacherDetailDTO -> TeacherDetailDTO));
        //4. Assemble data
        return BeanUtils.copyList(courseTeachers, CourseTeacherVO.class,
                (courseTeacher, courseTeacherVO) -> {
                    //4.1. Teacher Information
                    UserDTO teacherDetailDTO = teacherDetailDTOMap.get(courseTeacher.getTeacherId());
                    if (teacherDetailDTO != null) {
                        //4.2. Set Teacher Avatar
                        courseTeacherVO.setIcon(teacherDetailDTO.getPhoto());
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void deleteByCourseId(Long courserId) {
        //1. Delete Teacher-Course Relationship
        if (baseMapper.deleteByCourseId(courserId) <= 0) {
            throw new DbException(ErrorInfo.Msg.DB_DELETE_EXCEPTION);
        }
    }

    @Override
    public List<Long> getTeacherIdOfCourse(Long courseId) {
        //1. Query conditions
        LambdaQueryWrapper<CourseTeacher> queryWrapper =
                Wrappers.lambdaQuery(CourseTeacher.class)
                        .eq(CourseTeacher::getCourseId, courseId)
                        .orderByAsc(CourseTeacher::getCIndex);
        //2. Query data
        List<CourseTeacher> courseTeachers = baseMapper.selectList(queryWrapper);
        //3. Assemble data
        return CollUtils.isEmpty(courseTeachers) ?
                new ArrayList<>() : courseTeachers
                .stream()
                .map(CourseTeacher::getTeacherId)
                .collect(Collectors.toList());
    }

}
