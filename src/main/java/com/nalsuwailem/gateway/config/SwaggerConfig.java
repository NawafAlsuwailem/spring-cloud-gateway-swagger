package com.nalsuwailem.gateway.config;

import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Primary // register custom configuration first
public class SwaggerConfig implements SwaggerResourcesProvider {

    public static final String API_URI = "/v3/api-docs"; // OpenApi description default URI

    private final RouteDefinitionLocator routeLocator; // Gateway locator

    public SwaggerConfig(RouteDefinitionLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    /**
     * create Swagger resource registration details.
     * @return List
     */
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>(); // declare & initialize swagger resource list
        routeLocator.getRouteDefinitions().subscribe( // subscribe to Gateway locator definitions to monitor locator definition changes
                routeDefinition -> { // for each route definition
                    String resourceName = routeDefinition.getId(); // assign the route ID to resourceName
                    String location = routeDefinition.getPredicates().get(0).getArgs().get("_genkey_0") // get the route address (e.g., http://address:port/
                            .replace("/**", API_URI); // replace predicate extensions with /v3/api-docs. The resulted location will become: http://address:port/v3/api-docs
                    resources.add(swaggerResource(resourceName, location)); // add the route definition as a Swagger resource
                }
        );
        return resources;
    }

    /**
     * Swagger resource registration.
     * @param name
     * @param location
     * @return SwaggerResource
     */
    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource(); // initialize a swaggerResource pointer
        swaggerResource.setName(name); // set swaggerResource name to the given name
        swaggerResource.setLocation(location); // set swaggerResource location to the given location (e.g., http://address:port/v3/api-docs)
        swaggerResource.setSwaggerVersion("2.0"); // set swaggerResource version.
        return swaggerResource;
    }
}
