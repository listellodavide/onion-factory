package com.execodex.demolocalai.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * Configuration for Cross-Origin Resource Sharing (CORS).
 * This allows frontend applications from different origins to access the API.
 */
@Configuration
class CorsConfig {

    /**
     * Creates a CORS filter that allows cross-origin requests.
     *
     * @return a CorsWebFilter with permissive CORS settings
     */
    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration().apply {
            // Allow requests from any origin
            addAllowedOrigin("*")
            // Allow common HTTP methods
            addAllowedMethod("GET")
            addAllowedMethod("POST")
            addAllowedMethod("PUT")
            addAllowedMethod("DELETE")
            addAllowedMethod("OPTIONS")
            // Allow common headers
            addAllowedHeader("*")
            // Allow credentials (cookies, authorization headers, etc.)
            allowCredentials = false
            // How long the browser should cache the CORS response (in seconds)
            maxAge = 3600L
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }

        return CorsWebFilter(source)
    }
}