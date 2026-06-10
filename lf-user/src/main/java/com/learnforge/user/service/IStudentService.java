package com.learnforge.user.service;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.user.domain.dto.StudentFormDTO;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.user.domain.vo.StudentPageVo;

/**
 * <p>
 * Student Details Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
public interface IStudentService {

    void saveStudent(StudentFormDTO studentFormDTO);

    void updateMyPassword(StudentFormDTO studentFormDTO);

    PageDTO<StudentPageVo> queryStudentPage(UserPageQuery pageQuery);
}
