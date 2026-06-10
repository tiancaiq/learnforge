package com.learnforge.api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.learnforge.api.cache.RoleCache;
import com.learnforge.api.client.auth.AuthClient;
import com.learnforge.api.dto.auth.RoleDTO;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

public class RoleCacheConfig {
    /**
     * Caffeine cache for roles
     */
    @Bean
    public Cache<Long, RoleDTO> roleCaches(){
        return Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();
    }
    /**
     * Role cache utility
     */
    @Bean
    public RoleCache roleCache(Cache<Long, RoleDTO> roleCaches, AuthClient authClient){
        return new RoleCache(roleCaches, authClient);
    }
}
