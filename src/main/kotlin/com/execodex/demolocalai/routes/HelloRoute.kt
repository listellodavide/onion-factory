package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.HelloHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class HelloRoute(private val helloHandler: HelloHandler) {
    
    @Bean
    fun helloRoutes(): RouterFunction<ServerResponse> = router {
        GET("/hello", helloHandler::sayHello)
        GET("/hello/{name}", helloHandler::sayHelloWithName)
        GET("/greet", helloHandler::greet)
    }
}