package com.learnforge.user.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.user.domain.dto.StudentFormDTO;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.user.domain.vo.StudentPageVo;
import com.learnforge.user.service.IStudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Student details table frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@RestController
@RequestMapping("/students")
@Api(tags = "User management interface")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @ApiOperation("Paginated query of student information")
    @GetMapping("/page")
    public PageDTO<StudentPageVo> queryStudentPage(UserPageQuery pageQuery){
        return studentService.queryStudentPage(pageQuery);
    }

    @ApiOperation("Student registration")
    @PostMapping("/register")
    public void registerStudent(@RequestBody StudentFormDTO studentFormDTO) {
        studentService.saveStudent(studentFormDTO);
    }

    @ApiOperation("Modify student password")
    @PutMapping("/password")
    public void updateMyPassword(@RequestBody StudentFormDTO studentFormDTO) {
        studentService.updateMyPassword(studentFormDTO);
    }
}
