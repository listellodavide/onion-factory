package com.execodex.demolocalai.config

import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        clientRegistrationsProvider: ObjectProvider<ReactiveClientRegistrationRepository>
    ): SecurityWebFilterChain {
        val clientRegistrations = clientRegistrationsProvider.ifAvailable

        var security = http
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/", "/**").permitAll()
                    .anyExchange().authenticated()
            }

        // Enable OAuth2 login only if a ReactiveClientRegistrationRepository is available
        if (clientRegistrations != null) {
            security = security.oauth2Login { }
        }

        return security.build()
    }
}