package com.learnforge.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.auth.AuthClient;
import com.learnforge.api.dto.auth.RoleDTO;
import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.common.enums.UserType;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.ForbiddenException;
import com.learnforge.common.exceptions.UnauthorizedException;
import com.learnforge.common.utils.AssertUtils;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.user.domain.dto.UserFormDTO;
import com.learnforge.user.domain.po.User;
import com.learnforge.user.domain.po.UserDetail;
import com.learnforge.user.domain.vo.UserDetailVO;
import com.learnforge.user.enums.UserStatus;
import com.learnforge.user.mapper.UserMapper;
import com.learnforge.user.service.ICodeService;
import com.learnforge.user.service.IUserDetailService;
import com.learnforge.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.learnforge.user.constants.UserConstants.*;
import static com.learnforge.user.constants.UserErrorInfo.Msg.*;


/**
 * <p>
 * Student User Table Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private AuthClient authClient;
    @Autowired
    private IUserDetailService detailService;

    @Override
    public LoginUserDTO queryUserDetail(LoginFormDTO loginDTO, boolean isStaff) {
        // 1. Determine login method
        Integer type = loginDTO.getType();
        User user = null;
        // 2. Username and password login
        if (type == 1) {
            user = loginByPw(loginDTO);
        }
        // 3. Verification code login
        if (type == 2) {
            user = loginByVerifyCode(loginDTO.getCellPhone(), loginDTO.getPassword());
        }
        // 4. Invalid login method
        if (user == null) {
            throw new BadRequestException(ILLEGAL_LOGIN_TYPE);
        }
        // 5. Determine if user type matches login method
        if (isStaff ^ user.getType() != UserType.STUDENT) {
            throw new BadRequestException(isStaff ? "Non-management end user" : "Non-student end user");
        }
        // 6. Package return
        LoginUserDTO userDTO = new LoginUserDTO();
        userDTO.setUserId(user.getId());
        userDTO.setRoleId(handleRoleId(user));
        return userDTO;
    }

    @Override
    public void resetPassword(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        AssertUtils.isTrue(updateById(user), USER_ID_NOT_EXISTS);
    }

    @Override
    public UserDetailVO myInfo() {
        // 1. Get login user ID
        Long userId = UserContext.getUser();
        if (userId == null) {
            return null;
        }
        // 2. Query user
        UserDetail userDetail = detailService.queryById(userId);
        AssertUtils.isNotNull(userDetail, USER_ID_NOT_EXISTS);
        // 3. Package VO
        UserType type = userDetail.getType();
        // 3.1. Basic information
        UserDetailVO vo = BeanUtils.toBean(userDetail, UserDetailVO.class);
        // 3.2. Detailed information
        switch (type) {
            case STAFF:
                RoleDTO roleDTO = authClient.queryRoleById(userDetail.getRoleId());
                vo.setRoleName(roleDTO == null ? "" : roleDTO.getName());
                break;
            case STUDENT:
                vo.setRoleName(STUDENT_ROLE_NAME);
                break;
            case TEACHER:
                vo.setRoleName(TEACHER_ROLE_NAME);
                break;
            default:
                break;
        }
        return vo;
    }

    @Override
    public void addUserByPhone(User user, String code) {
        // 1. Verification code validation
        codeService.verifyCode(user.getCellPhone(), code);
        // 2. Check if phone number exists
        Integer count = lambdaQuery().eq(User::getCellPhone, user.getCellPhone()).count();
        if (count > 0) {
            throw new BadRequestException(PHONE_ALREADY_EXISTS);
        }
        // 3. Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 4. Add
        user.setUsername(user.getCellPhone());
        save(user);
    }

    @Override
    public void updatePasswordByPhone(String cellPhone, String code, String password) {
        // 1. Verification code validation
        codeService.verifyCode(cellPhone, code);
        // 2. Query user
        User oldUser = lambdaQuery().eq(User::getCellPhone, cellPhone).one();
        if (oldUser == null) {
            // Phone number does not exist
            throw new BadRequestException(PHONE_NOT_EXISTS);
        }
        // 2. Modify password
        User user = new User();
        user.setId(user.getId());
        user.setPassword(passwordEncoder.encode(password));
        updateById(user);
    }

    public void updatePhoneById(Long id, String cellPhone) {
        // 1.1. Determine if phone number needs to be modified
        if (StringUtils.isNotBlank(cellPhone)) {
            // 1.2. If modification is needed, package data
            User user = new User();
            user.setId(id);
            user.setUsername(cellPhone);
            user.setCellPhone(cellPhone);
            // 1.3. Modify
            updateById(user);
        }
    }

    @Override
    @Transactional
    public Long saveUser(UserDTO userDTO) {
        UserType type = UserType.of(userDTO.getType());
        // 1. Save user basic information
        User user = new User();
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setCellPhone(userDTO.getCellPhone());
        user.setUsername(userDTO.getCellPhone());
        user.setType(type);
        save(user);
        // 2. Add details
        UserDetail detail = BeanUtils.toBean(userDTO, UserDetail.class);
        detail.setId(user.getId());
        detail.setType(type);
        if(type == UserType.TEACHER){
            detail.setRoleId(TEACHER_ROLE_ID);
        }else{
            if (userDTO.getRoleId() == null) {
                throw new BadRequestException("Employee role information cannot be empty");
            }
        }
        detailService.save(detail);
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) {
        // 1. If phone number is passed, modify phone number
        String cellphone = userDTO.getCellPhone();
        if(StringUtils.isNotBlank(cellphone)){
            User user = new User();
            user.setId(userDTO.getId());
            user.setCellPhone(cellphone);
            user.setUsername(cellphone);
            updateById(user);
        }
        // 2. Modify details
        UserDetail detail = BeanUtils.toBean(userDTO, UserDetail.class);
        detail.setType(null);
        detailService.updateById(detail);
    }

    @Override
    public void updateUserWithPassword(UserFormDTO userDTO) {
        // 1. Attempt to update password
        String pw = userDTO.getPassword();
        String oldPw = userDTO.getOldPassword();
        if(StringUtils.isNotBlank(pw) && StringUtils.isNotBlank(pw)) {
            Long userId = UserContext.getUser();
            // 1.1. Query user
            User user = getById(userId);
            // 1.2. Validate
            if (user == null) {
                throw new UnauthorizedException(USER_ID_NOT_EXISTS);
            }
            // 1.3. Validate password
            if (!passwordEncoder.matches(oldPw, user.getPassword())) {
                // Password does not match
                throw new UnauthorizedException(INVALID_UN_OR_PW);
            }
            // 1.4. Modify password
            user = new User();
            user.setId(userId);
            user.setPassword(passwordEncoder.encode(pw));
            updateById(user);
        }
        // 2. Update user details
        UserDetail detail = BeanUtils.toBean(userDTO, UserDetail.class);
        detail.setRoleId(null);
        detail.setType(null);
        detailService.updateById(detail);
    }

    public User loginByPw(LoginFormDTO loginDTO) {
        // 1. Data validation
        String username = loginDTO.getUsername();
        String cellPhone = loginDTO.getCellPhone();
        if (StrUtil.isBlank(username) && StrUtil.isBlank(cellPhone)) {
            throw new BadRequestException(INVALID_UN);
        }
        // 2. Query by username or phone number
        User user = lambdaQuery()
                .eq(StrUtil.isNotBlank(username), User::getUsername, username)
                .eq(StrUtil.isNotBlank(cellPhone), User::getCellPhone, cellPhone)
                .one();
        AssertUtils.isNotNull(user, INVALID_UN_OR_PW);
        // 3. Validate if disabled
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException(USER_FROZEN);
        }
        // 4. Validate password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException(INVALID_UN_OR_PW);
        }

        return user;
    }

    private Long handleRoleId(User user) {
        Long roleId = 0L;
        switch (user.getType()) {
            case STUDENT:
                roleId = STUDENT_ROLE_ID;
                break;
            case TEACHER:
                roleId = TEACHER_ROLE_ID;
                break;
            case STAFF:
                UserDetail detail = detailService.getById(user.getId());
                roleId = detail.getRoleId();
                break;
        }
        return roleId;
    }

    public User loginByVerifyCode(String phone, String code) {
        // 1. Validate verification code
        codeService.verifyCode(phone, code);
        // 2. Query by phone number
        User user = lambdaQuery().eq(User::getCellPhone, phone).one();
        if (user == null) {
            throw new BadRequestException(PHONE_NOT_EXISTS);
        }
        // 3. Validate if disabled
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException(USER_FROZEN);
        }
        return user;
    }
}
