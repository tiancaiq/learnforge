package com.learnforge.user.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.user.domain.vo.TeacherPageVO;
import com.learnforge.user.service.ITeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Teacher details table frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@RestController
@RequestMapping("/teachers")
@Api(tags = "User management interface")
public class TeacherController {

    @Autowired
    private ITeacherService teacherService;

    @GetMapping("/page")
    @ApiOperation("Paginated query of teacher information")
    public PageDTO<TeacherPageVO> queryTeacherPage(UserPageQuery pageQuery){
        return teacherService.queryTeacherPage(pageQuery);
    }
}
