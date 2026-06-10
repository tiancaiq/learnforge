package com.learnforge.api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.learnforge.api.cache.CategoryCache;
import com.learnforge.api.client.course.CategoryClient;
import com.learnforge.api.dto.course.CategoryBasicDTO;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Map;

public class CategoryCacheConfig {
    /**
     * Caffeine cache for course categories
     */
    @Bean
    public Cache<String, Map<Long, CategoryBasicDTO>> categoryCaches(){
        return Caffeine.newBuilder()
                .initialCapacity(1) // Capacity limit
                .maximumSize(10_000) // Maximum memory limit
                .expireAfterWrite(Duration.ofMinutes(30)) // Validity period
                .build();
    }
    /**
     * Cache utility class for course categories
     */
    @Bean
    public CategoryCache categoryCache(
            Cache<String, Map<Long, CategoryBasicDTO>> categoryCaches, CategoryClient categoryClient){
        return new CategoryCache(categoryCaches, categoryClient);
    }
}
