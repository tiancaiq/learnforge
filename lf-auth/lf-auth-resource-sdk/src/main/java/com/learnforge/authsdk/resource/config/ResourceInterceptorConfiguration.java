package com.learnforge.authsdk.resource.config;

import cn.hutool.core.collection.CollUtil;
import com.learnforge.authsdk.resource.interceptors.LoginAuthInterceptor;
import com.learnforge.authsdk.resource.interceptors.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(ResourceAuthProperties.class)
public class ResourceInterceptorConfiguration implements WebMvcConfigurer {

    private final ResourceAuthProperties authProperties;

    @Autowired
    public ResourceInterceptorConfiguration(ResourceAuthProperties resourceAuthProperties) {
        this.authProperties = resourceAuthProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Add user info interceptor
        registry.addInterceptor(new UserInfoInterceptor()).order(0);
        // 2. Whether to enable login interception
        if(!authProperties.getEnable()){
            // No login interception needed
            return;
        }
        // 2. Add login interceptor
        InterceptorRegistration registration = registry.addInterceptor(new LoginAuthInterceptor()).order(1);
        // 2.1. Add interceptor path
        if(CollUtil.isNotEmpty(authProperties.getIncludeLoginPaths())){
            registration.addPathPatterns(authProperties.getIncludeLoginPaths());
        }
        // 2.2. Add excluded path
        if(CollUtil.isNotEmpty(authProperties.getExcludeLoginPaths())){
            registration.excludePathPatterns(authProperties.getExcludeLoginPaths());
        }
        // 2.3. Exclude swagger path
        registration.excludePathPatterns(
                "/v2/**",
                "/v3/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/doc.html"
        );
    }
}
