package com.execodex.demolocalai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class GreetService(private val chatClient: ChatClient) {

    fun greet(name: String): Mono<String?> {
        return Mono.fromCallable { chatClient.prompt("Hello, who are you? My name is ${name}").call().content() }
            .subscribeOn(Schedulers.boundedElastic())
    }
}