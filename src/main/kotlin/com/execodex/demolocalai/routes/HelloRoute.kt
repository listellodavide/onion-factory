package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.HelloHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * Configuration for hello-related routes.
 */
@Configuration
class HelloRoute(private val helloHandler: HelloHandler) {
    
    /**
     * Defines the routes for hello operations.
     *
     * @return a router function with hello routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/hello",
            beanClass = HelloHandler::class,
            beanMethod = "sayHello",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "sayHello",
                summary = "Say hello",
                description = "Returns a simple hello world message",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = String::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/hello/{name}",
            beanClass = HelloHandler::class,
            beanMethod = "sayHelloWithName",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "sayHelloWithName",
                summary = "Say hello with name",
                description = "Returns a hello message with the provided name",
                parameters = [
                    Parameter(
                        name = "name",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Name to greet"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = String::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/greet/{name}",
            beanClass = HelloHandler::class,
            beanMethod = "greet",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "greetWithName",
                summary = "Greet with name",
                description = "Returns a greeting message with the provided name using AI",
                parameters = [
                    Parameter(
                        name = "name",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Name to greet"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = String::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/greet",
            beanClass = HelloHandler::class,
            beanMethod = "greet",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "greet",
                summary = "Greet anonymous user",
                description = "Returns a greeting message for an anonymous user using AI",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = String::class))]
                    )
                ]
            )
        )
    )
    fun helloRoutes(): RouterFunction<ServerResponse> = router {
        GET("/hello", helloHandler::sayHello)
        GET("/hello/{name}", helloHandler::sayHelloWithName)
        GET("/greet/{name}", helloHandler::greet)
        GET("/greet", helloHandler::greet)
    }
}