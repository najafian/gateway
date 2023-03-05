package com.helia.gateway.config

import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.route.RouteDefinition
import org.springframework.cloud.gateway.route.RouteDefinitionLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



@Configuration
class SwaggerConfig {

    @Autowired
    val locator: RouteDefinitionLocator? = null
    @Bean
    fun apis(): List<GroupedOpenApi>? {
        val groups: MutableList<GroupedOpenApi> = ArrayList()
        val definitions: List<RouteDefinition> = locator?.routeDefinitions?.collectList()?.block()!!

        definitions.stream().filter {
                routeDefinition: RouteDefinition -> routeDefinition.id.matches(".*-service".toRegex())
        }.forEach { routeDefinition: RouteDefinition ->
            val name = routeDefinition.id.replace("-service".toRegex(), "")
            groups.add(GroupedOpenApi.builder().pathsToMatch("/$name/**").group(name).build())
        }
        return groups
    }
}