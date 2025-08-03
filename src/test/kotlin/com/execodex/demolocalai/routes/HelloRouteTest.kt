package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.HelloHandler
import com.execodex.demolocalai.service.GreetService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest
@AutoConfigureWebTestClient
class HelloRouteTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun mockGreetService(): GreetService {
            val mockService = Mockito.mock(GreetService::class.java)
            Mockito.`when`(mockService.greet("Anonymous"))
                .thenReturn(Mono.just("Hello, I am an AI assistant. How can I help you today?"))
            return mockService
        }
    }

    @Test
    fun `should return greeting response from greet endpoint`() {
        // When & Then
        webTestClient.get()
            .uri("/greet")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo("Hello, I am an AI assistant. How can I help you today?")
    }
}