package com.learnforge.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.user.domain.dto.UserFormDTO;
import com.learnforge.user.domain.po.User;
import com.learnforge.user.domain.vo.UserDetailVO;

/**
 * <p>
 * Student User Table Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-28
 */
public interface IUserService extends IService<User> {
    LoginUserDTO queryUserDetail(LoginFormDTO loginDTO, boolean isStaff);

    void resetPassword(Long userId);

    UserDetailVO myInfo();

    void addUserByPhone(User user, String code);

    void updatePasswordByPhone(String cellPhone, String code, String password);

    Long saveUser(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void updateUserWithPassword(UserFormDTO userDTO);
}
