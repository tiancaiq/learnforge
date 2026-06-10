package com.learnforge.auth.service.impl;

import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.auth.common.constants.JwtConstants;
import com.learnforge.auth.service.IAccountService;
import com.learnforge.auth.service.ILoginRecordService;
import com.learnforge.auth.util.JwtTool;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.BooleanUtils;
import com.learnforge.common.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Account Table, Platform User Account and Password Information Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService{
    private final JwtTool jwtTool;
    private final UserClient userClient;
    private final ILoginRecordService loginRecordService;

    @Override
    public String login(LoginFormDTO loginDTO, boolean isStaff) {
        // 1. Query and Validate User Information
        LoginUserDTO detail = userClient.queryUserDetail(loginDTO, isStaff);
        if (detail == null) {
            throw new BadRequestException("Login Info Incorrect");
        }

        // 2. Generate Login Token Based on JWT
        // 2.1. Set Remember Me Flag
        detail.setRememberMe(loginDTO.getRememberMe());
        // 2.2. Generate Token
        String token = generateToken(detail);

        // 3. Record Login Info
        loginRecordService.loginSuccess(loginDTO.getCellPhone(), detail.getUserId());
        // 4. Return Result
        return token;
    }

    private String generateToken(LoginUserDTO detail) {
        // 2.2. Generate Access-Token
        String token = jwtTool.createToken(detail);
        // 2.3. Generate Refresh-Token, Save Refresh-Token JTI to Redis
        String refreshToken = jwtTool.createRefreshToken(detail);
        // 2.4. Write Refresh-Token to User Cookie and Set HttpOnly to True
        int maxAge = BooleanUtils.isTrue(detail.getRememberMe()) ?
                (int) JwtConstants.JWT_REMEMBER_ME_TTL.toSeconds() : -1;
        WebUtils.cookieBuilder()
                .name(detail.getRoleId() == 2 ? JwtConstants.REFRESH_HEADER : JwtConstants.ADMIN_REFRESH_HEADER)
                .value(refreshToken)
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
        return token;
    }

    @Override
    public void logout() {
        // Delete JTI
        jwtTool.cleanJtiCache();
        // Delete Cookie
        WebUtils.cookieBuilder()
                .name(JwtConstants.REFRESH_HEADER)
                .value("")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }

    @Override
    public String refreshToken(String refreshToken) {
        // 1. Validate Refresh-Token and JTI
        LoginUserDTO userDTO = jwtTool.parseRefreshToken(refreshToken);
        // 2. Generate New Access-Token and Refresh-Token
        return generateToken(userDTO);
    }
}
