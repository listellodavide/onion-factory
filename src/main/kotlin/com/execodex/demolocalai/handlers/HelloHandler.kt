package com.execodex.demolocalai.handlers

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class HelloHandler(private val chatClientBuilder: ChatClient.Builder) {
    private val chatClient: ChatClient = chatClientBuilder

        .build()

    fun sayHello(request: ServerRequest) =
        ServerResponse.ok().bodyValue("hello world")

    fun sayHelloWithName(request: ServerRequest) =
        ServerResponse.ok().bodyValue("hello ${request.pathVariable("name")}")

    fun greet(request: ServerRequest): Mono<ServerResponse> {
        return Mono.fromCallable { chatClient.prompt("Hello, who are you?").call().content() }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { body -> ServerResponse.ok().bodyValue(body ?: "No response") }
    }
}