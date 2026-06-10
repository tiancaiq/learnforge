package com.learnforge.common.utils;

import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import springfox.documentation.swagger2.web.SwaggerTransformationContext;

import java.lang.reflect.Constructor;

/**
 * Swagger processing tool
 **/
@Slf4j
public class SwaggerUtils {


    /**
     * Use reflection to get the SwaggerTransformationContext object
     */
    public static SwaggerTransformationContext getInstance(Swagger swagger, ServerHttpRequest request){

        Constructor<SwaggerTransformationContext> constructor =
                ReflectUtils.getConstructor(SwaggerTransformationContext.class);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(swagger, request);
        }catch (Exception e){
            log.error("Generate swagger transformation failed e:",e);
        }
        return null;
    }
}
