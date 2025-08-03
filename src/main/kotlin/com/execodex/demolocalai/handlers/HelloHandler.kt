package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.service.GreetService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class HelloHandler(private val greetService: GreetService) {


    fun sayHello(request: ServerRequest) =
        ServerResponse.ok().bodyValue("hello world")

    fun sayHelloWithName(request: ServerRequest) =
        ServerResponse.ok().bodyValue("hello ${request.pathVariable("name")}")

    fun greet(request: ServerRequest): Mono<ServerResponse> {
        val userName: String = try {
            request.pathVariable("name")
//                .let { name ->
//                if (name.isEmpty()) {
//                    "unknown";
//                }
//                name;
//            }
        } catch (e: IllegalArgumentException) {
            "Anonymous"
        }
        return greetService.greet(userName)
            .flatMap { body -> ServerResponse.ok().bodyValue(body ?: "No response, install Docker and a model") }
    }
}