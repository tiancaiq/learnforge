package com.learnforge.auth.util;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.learnforge.auth.common.constants.AuthErrorInfo;
import com.learnforge.auth.common.constants.JwtConstants;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.AssertUtils;
import com.learnforge.common.utils.BooleanUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

import static com.learnforge.auth.common.constants.JwtConstants.JWT_REFRESH_TTL;
import static com.learnforge.auth.common.constants.JwtConstants.JWT_TOKEN_TTL;

@Component
public class JwtTool {
    private final StringRedisTemplate stringRedisTemplate;
    private final JWTSigner jwtSigner;

    public JwtTool(StringRedisTemplate stringRedisTemplate, KeyPair keyPair) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * Create Access-Token
     *
     * @param userDTO User Information
     * @return access-token
     */
    public String createToken(LoginUserDTO userDTO) {
        // 1. Generate jws
        return JWT.create()
                .setPayload(JwtConstants.PAYLOAD_USER_KEY, userDTO)
                .setExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_TTL.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * Create refresh token, and record the token's JTI in Redis
     *
     * @param userDetail user information
     * @return refresh token
     */
    public String createRefreshToken(LoginUserDTO userDetail) {
        // 1. Generate JTI
        String jti = UUID.randomUUID().toString(true);
        // 2. Generate jwt
        // 2.1. If it's a remember me token, validity is 7 days, otherwise 30 minutes
        Duration ttl = BooleanUtils.isTrue(userDetail.getRememberMe()) ?
                JwtConstants.JWT_REMEMBER_ME_TTL : JWT_REFRESH_TTL;
        // 2.2. Generate Token
        String token = JWT.create()
                .setJWTId(jti)
                .setPayload(JwtConstants.PAYLOAD_USER_KEY, userDetail)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
        // 3. Cache jti, validity same as token, after expiration or deletion of JTI, the corresponding refresh-token becomes invalid
        stringRedisTemplate.opsForValue()
                .set(JwtConstants.JWT_REDIS_KEY_PREFIX + userDetail.getUserId(), jti, ttl);
        return token;
    }

    /**
     * Parse refresh token
     *
     * @param refreshToken refresh token
     * @return user information obtained by parsing refresh token
     */
    public LoginUserDTO parseRefreshToken(String refreshToken) {
        // 1. Check if token is empty
        AssertUtils.isNotNull(refreshToken, AuthErrorInfo.Msg.INVALID_TOKEN);
        // 2. Validate and parse jwt
        JWT jwt;
        try {
            jwt = JWT.of(refreshToken).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new BadRequestException(400, AuthErrorInfo.Msg.INVALID_TOKEN, e);
        }
        // 2. Check if jwt is valid
        if (!jwt.verify()) {
            // Validation failed
            throw new BadRequestException(400, AuthErrorInfo.Msg.INVALID_TOKEN);
        }
        // 3. Check if expired
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new BadRequestException(400, AuthErrorInfo.Msg.EXPIRED_TOKEN);
        }
        // 4. Data format check
        Object userPayload = jwt.getPayload(JwtConstants.PAYLOAD_USER_KEY);
        Object jtiPayload = jwt.getPayload(JwtConstants.PAYLOAD_JTI_KEY);
        if (jtiPayload == null || userPayload == null) {
            // Data is empty
            throw new BadRequestException(400, AuthErrorInfo.Msg.INVALID_TOKEN);
        }

        // 5. Data parsing
        LoginUserDTO userDTO;
        try {
            userDTO = ((JSONObject) userPayload).toBean(LoginUserDTO.class);
        } catch (RuntimeException e) {
            // Data format is incorrect
            throw new BadRequestException(400, AuthErrorInfo.Msg.INVALID_TOKEN);
        }

        // 6. JTI validation
        String jti = stringRedisTemplate.opsForValue().get(JwtConstants.JWT_REDIS_KEY_PREFIX + userDTO.getUserId());
        if (!StringUtils.equals(jti, jtiPayload.toString())) {
            // jti is inconsistent
            throw new BadRequestException(400, AuthErrorInfo.Msg.INVALID_TOKEN);
        }
        return userDTO;
    }

    /**
     * Clean up refresh-token's jti, essentially invalidating refresh-token
     */
    public void cleanJtiCache() {
        stringRedisTemplate.delete(JwtConstants.JWT_REDIS_KEY_PREFIX + UserContext.getUser());
    }
}
