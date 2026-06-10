package com.learnforge.message.properties;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Thread pool configuration
 * @ClassName ThreadPoolProperties
 * @author wusongsong
 * @since 2022/6/23 15:36
 * @version 1.0.0
 **/
@Configuration
@Data
public class ThreadPoolProperties {

    @NestedConfigurationProperty
    private Map<String, Object> threadPools;


}
