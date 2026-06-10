package com.learnforge.gateway.swagger;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
public class GatewaySwaggerResourceProvider implements SwaggerResourcesProvider {

    /**
     * Swagger2 default url suffix
     */
    private static final String SWAGGER2_URL = "/v2/api-docs";

    /**
     * Route locator
     */
    private final RouteLocator routeLocator;

    /**
     * Gateway application name
     */
    @Value("${spring.application.name}")
    private String gatewayName;

    /**
     * Get Swagger resources
     */
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        Map<String, String> servers = new HashMap<>();
        // 1. Get the host from the route Uri as the service name, use the route id as the request path, here ensure the route id matches the route path prefix
        routeLocator.getRoutes()
                .filter(route -> route.getUri().getHost() != null)
                .filter(route -> !gatewayName.equals(route.getUri().getHost()))
                .subscribe( r -> servers.put(r.getUri().getHost(), r.getId()));
        // 2. Create custom resource
        servers.forEach((name, path) -> {
            // Create Swagger resource
            SwaggerResource swaggerResource = new SwaggerResource();
            // Set access address
            swaggerResource.setUrl("/" + path + SWAGGER2_URL);
            // Set name
            swaggerResource.setName(name);
            swaggerResource.setSwaggerVersion("3.0.0");
            resources.add(swaggerResource);
        });
        return resources;
    }
}