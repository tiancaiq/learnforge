package com.learnforge.auth.service;

import com.learnforge.api.dto.user.LoginFormDTO;

/**
 * <p>
 * Account Table, Platform User Account and Password Information Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
public interface IAccountService{

    String login(LoginFormDTO loginFormDTO, boolean isStaff);

    void logout();

    String refreshToken(String refreshToken);
}
