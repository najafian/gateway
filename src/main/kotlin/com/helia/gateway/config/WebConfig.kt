package com.helia.gateway.config

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI


@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebConfig(clientProperties: OAuth2ClientProperties) {
    private val authorizationLogoutUrl: URI

    init {
        clientProperties.provider.values.stream().findFirst().map { it.issuerUri }
            .map { UriComponentsBuilder.fromHttpUrl(it) }
            .map { it.replacePath("logout").build().toUri() }.orElseThrow()
            .also { authorizationLogoutUrl = it }
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        val redirectserverLogout=RedirectServerLogoutSuccessHandler()
        http.authorizeExchange { exchanges: ServerHttpSecurity.AuthorizeExchangeSpec ->
            exchanges.pathMatchers("/login","/login2","/static/img/**").permitAll().anyExchange().authenticated().and().headers().frameOptions().disable()
        }
            .oauth2Login(Customizer.withDefaults())
            .logout().logoutSuccessHandler(redirectserverLogout).and()
            .oauth2ResourceServer { it.jwt() }
            .addFilterBefore(WebGlobalFilter(), SecurityWebFiltersOrder.HTTPS_REDIRECT)
        http.csrf().disable()
        return http.build()
    }

    @Bean
    fun staticResourceLocator(): RouterFunction<ServerResponse> {
        return RouterFunctions.resources("/**", ClassPathResource("/"))
    }
}

class WebGlobalFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(exchange)
    }
}