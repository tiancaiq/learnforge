package com.learnforge.user.controller;

import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.user.constants.UserErrorInfo;
import com.learnforge.user.domain.dto.UserFormDTO;
import com.learnforge.user.domain.po.User;
import com.learnforge.user.domain.po.UserDetail;
import com.learnforge.user.domain.vo.UserDetailVO;
import com.learnforge.user.enums.UserStatus;
import com.learnforge.user.service.IUserDetailService;
import com.learnforge.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("users")
@Api(tags = "User management interface")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserDetailService detailService;

    @ApiOperation("Add new user, usually an employee or teacher")
    @PostMapping
    public Long saveUser(@Valid @RequestBody UserDTO userDTO){
        userDTO.setId(null);
        return userService.saveUser(userDTO);
    }

    @ApiOperation("Update user information")
    @PutMapping("/{id}")
    public void updateUser(@RequestBody UserDTO userDTO){
        userService.updateUser(userDTO);
    }

    @ApiOperation("Update current logged-in user information, can modify password")
    @PutMapping
    public void updateCurrentUser(@Valid @RequestBody UserFormDTO userDTO){
        userService.updateUserWithPassword(userDTO);
    }

    @PutMapping("/{id}/password/default")
    @ApiOperation("Reset password")
    public void resetPassword(
            @ApiParam(value = "ID of the user to reset password", example = "1") @PathVariable("id") Long userId) {
        userService.resetPassword(userId);
    }

    @PutMapping("/{id}/status/{status}")
    @ApiOperation("Modify user status, status=0 is disabled, status=1 is normal")
    public void updateUserStatus(
            @ApiParam(value = "ID of the user to reset password", example = "1") @PathVariable("id") Long userId,
            @ApiParam(value = "Status", example = "1") @PathVariable("status") Integer status
    ) {
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.of(status));
        userService.updateById(user);
    }

    @ApiOperation("Get current logged-in user information")
    @GetMapping(value = "/me")
    public UserDetailVO me() {
        return userService.myInfo();
    }

    @ApiOperation("Query user information by ID")
    @GetMapping("/{id}")
    public UserDTO queryUserById(
            @ApiParam("User id") @PathVariable("id") Long id) {
        UserDetail userDetail = detailService.queryById(id);
        return BeanUtils.copyBean(userDetail, UserDTO.class, (d, u) -> u.setType(d.getType().getValue()));
    }

    /**
     * Login structure
     * @param loginDTO login form
     * @param isStaff whether it is a backend login
     * @return login user information
     */
    @ApiIgnore
    @PostMapping("/detail/{isStaff}")
    public LoginUserDTO queryUserDetail(
            @Valid @RequestBody LoginFormDTO loginDTO, @PathVariable("isStaff") boolean isStaff) {
        return userService.queryUserDetail(loginDTO, isStaff);
    }

    /**
     * <h1>Batch query user information by id</h1>
     *
     * @param ids collection of user ids
     * @return user collection
     */
    @ApiIgnore
    @GetMapping("/list")
    public List<UserDTO> queryUserByIds(
            @ApiParam("List of user IDs") @RequestParam("ids") List<Long> ids) {
        if(CollUtils.isEmpty(ids)){
            return CollUtils.emptyList();
        }
        // 1. Query list
        List<UserDetail> list = detailService.queryByIds(ids);
        // 2. Convert
        return BeanUtils.copyList(list, UserDTO.class, (d, u) -> u.setType(d.getType().getValue()));
    }

    /**
     * Query user type
     *
     * @param id user id
     * @return user type, 0 - regular learner, 1 - teacher, 2 - other staff
     */
    @ApiIgnore
    @GetMapping("/{id}/type")
    public Integer queryUserType(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BadRequestException(UserErrorInfo.Msg.USER_ID_NOT_EXISTS);
        }
        return user.getType().getValue();
    }

    @ApiIgnore
    @GetMapping("/ids")
    public Long exchangeUserIdWithPhone(@RequestParam("phone") String phone) {
        User user = userService
                .lambdaQuery().eq(User::getCellPhone, phone).one();
        if (user == null) {
            throw new BadRequestException(UserErrorInfo.Msg.USER_ID_NOT_EXISTS);
        }
        return user.getId();
    }

    @ApiOperation("Check if user phone number exists")
    @GetMapping("checkCellphone")
    public Boolean checkCellPhone(@RequestParam("cellphone") String cellPhone){
        return userService.lambdaQuery()
                .eq(User::getCellPhone, cellPhone)
                // .in(User::getType, UserType.STAFF, UserType.TEACHER)
                .count() <= 0;
    }
}
