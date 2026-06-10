package com.learnforge.auth.common.constants;

import java.time.Duration;

public class JwtConstants {
    public static final String PAYLOAD_USER_KEY = "user";
    public static final String PAYLOAD_JTI_KEY = "jti";

    public static final String JWT_REDIS_KEY_PREFIX = "jwt:uid:";
    // Token expiration time, set to 1 day during testing, normally 5 minutes
    public static final Duration JWT_TOKEN_TTL = Duration.ofMinutes(5);
    // public static final Duration JWT_TOKEN_TTL = Duration.ofMinutes(60 * 24);
    public static final Duration JWT_REFRESH_TTL = Duration.ofMinutes(30);

    public static final Duration JWT_REMEMBER_ME_TTL = Duration.ofDays(7);

    public static final String JWT_ALGORITHM = "rs256";
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String REFRESH_HEADER = "refresh";
    public static final String ADMIN_REFRESH_HEADER = "admin-refresh";

    public static final String USER_HEADER = "user-info";

    /* Permission cache KEY begin */
    public static final String AUTH_PRIVILEGE_KEY = "auth:privileges";
    public static final String AUTH_PRIVILEGE_VERSION_KEY = "version";
    public static final String LOCK_AUTH_PRIVILEGE_KEY = "lock:auth:privileges";
    /* Permission cache KEY end */

}
