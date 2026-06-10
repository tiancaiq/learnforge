package com.learnforge.auth.controller;


import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.auth.common.constants.JwtConstants;
import com.learnforge.auth.service.IAccountService;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Account login related interfaces
 */
@RestController
@RequestMapping("/accounts")
@Api(tags = "Account management")
@RequiredArgsConstructor
public class AccountController {

    private final IAccountService accountService;

    @ApiOperation("Login and get token")
    @PostMapping(value = "/login")
    public String loginByPw(@RequestBody LoginFormDTO loginFormDTO) {
        return accountService.login(loginFormDTO, false);
    }

    @ApiOperation("Admin login and get token")
    @PostMapping(value = "/admin/login")
    public String adminLoginByPw(@RequestBody LoginFormDTO loginFormDTO) {
        return accountService.login(loginFormDTO, true);
    }

    @ApiOperation("Logout")
    @PostMapping(value = "/logout")
    public void logout() {
        accountService.logout();
    }

    @ApiOperation("Refresh token")
    @GetMapping(value = "/refresh")
    public String refreshToken(
            @CookieValue(value = JwtConstants.REFRESH_HEADER, required = false) String studentToken,
            @CookieValue(value = JwtConstants.ADMIN_REFRESH_HEADER, required = false) String adminToken
    ) {
        if (studentToken == null && adminToken == null) {
            throw new BadRequestException("Login timeout");
        }
        String host = WebUtils.getHeader("origin");
        if (host == null) {
            throw new BadRequestException("Login timeout");
        }
        String token = host.startsWith("www", 7) ? studentToken : adminToken;
        if (token == null) {
            throw new BadRequestException("Login timeout");
        }
        return accountService.refreshToken(WebUtils.cookieBuilder().decode(token));
    }
}
