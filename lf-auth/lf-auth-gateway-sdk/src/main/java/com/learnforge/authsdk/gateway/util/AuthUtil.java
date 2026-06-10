package com.learnforge.authsdk.gateway.util;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import com.learnforge.auth.common.domain.PrivilegeRoleDTO;
import com.learnforge.common.domain.R;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.common.exceptions.ForbiddenException;
import com.learnforge.common.exceptions.UnauthorizedException;
import com.learnforge.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.auth.common.constants.AuthErrorInfo.Code.EXPIRED_TOKEN_CODE;
import static com.learnforge.auth.common.constants.AuthErrorInfo.Code.INVALID_TOKEN_CODE;
import static com.learnforge.auth.common.constants.AuthErrorInfo.Msg.*;
import static com.learnforge.auth.common.constants.JwtConstants.*;

@Slf4j
public class AuthUtil {
    // Cache permission information
    private Map<String, PrivilegeRoleDTO> privileges = new HashMap<>();
    // Set of path matchers to intercept
    private Set<String> paths = new HashSet<>();
    // Permission version information, reduce unnecessary cache processing
    private int privilegeVersion;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final JwtSignerHolder jwtSignerHolder;
    private final StringRedisTemplate stringRedisTemplate;
    private final BoundHashOperations<String, String, String> hashOps;

    public AuthUtil(JwtSignerHolder jwtSignerHolder, StringRedisTemplate stringRedisTemplate) {
        this.jwtSignerHolder = jwtSignerHolder;
        this.stringRedisTemplate = stringRedisTemplate;
        this.hashOps = stringRedisTemplate.boundHashOps(AUTH_PRIVILEGE_KEY);
    }

    public R<LoginUserDTO> parseToken(String token) {
        // 1. Check if token is empty
        if(StringUtils.isBlank(token)){
            return R.error(INVALID_TOKEN_CODE, INVALID_TOKEN);
        }
        JWT jwt = null;
        try {
            jwt = JWT.of(token).setSigner(jwtSignerHolder.getJwtSigner());
        } catch (Exception e) {
            return R.error(INVALID_TOKEN_CODE, INVALID_TOKEN);
        }
        // 2. Check if jwt is valid
        if (!jwt.verify()) {
            // Verification failed, return empty
            return R.error(INVALID_TOKEN_CODE, INVALID_TOKEN);
        }
        // 3. Check if expired
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            return R.error(EXPIRED_TOKEN_CODE, EXPIRED_TOKEN);
        }
        // 4. Data format check
        Object userPayload = jwt.getPayload(PAYLOAD_USER_KEY);
        if (userPayload == null) {
            // Data is empty
            return R.error(INVALID_TOKEN_CODE, INVALID_TOKEN_PAYLOAD);
        }

        // 5. Data parsing
        LoginUserDTO userDTO;
        try {
            userDTO = ((JSONObject)userPayload).toBean(LoginUserDTO.class);
        } catch (RuntimeException e) {
            // Token format is incorrect
            return R.error(INVALID_TOKEN_CODE, INVALID_TOKEN_PAYLOAD);
        }

        // 6. Return
        return R.ok(userDTO);
    }

    public void checkAuth(String antPath, R<LoginUserDTO> r){
        // 1. Determine if the path requires permission
        String matchPath = findMatchPath(antPath);
        if(matchPath == null){
            // No permission restriction, directly proceed
            return;
        }
        // 2. Determine if login is successful
        if(!r.success()){
            // Not logged in, directly report error
            throw new UnauthorizedException(r.getCode(), r.getMsg());
        }
        // 3. Get required permissions for current path
        PrivilegeRoleDTO pathPrivilege = findPathPrivilege(matchPath);

        // 4. Permission check
        Set<Long> requiredRoles = pathPrivilege.getRoles();
        if (!CollectionUtil.contains(requiredRoles, r.getData().getRoleId())) {
            // No access permission
            throw new ForbiddenException(FORBIDDEN);
        }
    }

    private String findMatchPath(String antPath){
        String matchPath = null;
        for (String pathPattern : paths) {
            if(antPathMatcher.match(pathPattern, antPath)){
                matchPath = pathPattern;
                break;
            }
        }
        return matchPath;
    }

    private PrivilegeRoleDTO findPathPrivilege(String path){
        return privileges.get(path);
    }

    private List<PrivilegeRoleDTO> loadPrivileges(){
        List<String> values = hashOps.values();
        if(CollUtil.isEmpty(values)){
            return Collections.emptyList();
        }
        return values.stream()
                .map(json -> JSONUtil.toBean(json, PrivilegeRoleDTO.class))
                .collect(Collectors.toList());
    }

    private int currentVersion() {
        String version = stringRedisTemplate.opsForValue().get(AUTH_PRIVILEGE_VERSION_KEY);
        if(StrUtil.isEmpty(version)){
            return 0;
        }
        return Integer.parseInt(version);
    }


    @Scheduled(fixedDelay = 20000)
    public void refreshTask(){
        // 1. Get version number
        int currentVersion = currentVersion();
        if (currentVersion == this.privilegeVersion) {
            // Version is consistent, data has not been updated, directly end task
            return;
        }
        // 2. Get latest permission information
        List<PrivilegeRoleDTO> privilegeRoleDTOS = loadPrivileges();
        if(CollUtil.isEmpty(privilegeRoleDTOS)){
            // Update version
            this.privilegeVersion = currentVersion;
            return;
        }
        // 3. Data processing
        Map<String, PrivilegeRoleDTO> map = new HashMap<>();
        for (PrivilegeRoleDTO p : privilegeRoleDTOS) {
            map.put(p.getAntPath(), p);
            this.privileges = map;
        }
        this.paths = map.keySet();
        // 4. Update version
        this.privilegeVersion = currentVersion;
    }
}
