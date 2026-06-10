package com.learnforge.user.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.user.domain.vo.StaffVO;
import com.learnforge.user.service.IStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Employee details table frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@RestController
@RequestMapping("/staffs")
@Api(tags = "User management interface")
public class StaffController {

    private final IStaffService staffService;

    public StaffController(IStaffService staffService) {
        this.staffService = staffService;
    }

    @ApiOperation("Paginated query of employee information")
    @GetMapping("page")
    public PageDTO<StaffVO> queryStaffPage(UserPageQuery pageQuery){
        return staffService.queryStaffPage(pageQuery);
    }
}
