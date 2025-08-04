package com.execodex.demolocalai

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OpenApiDocsTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `api-docs endpoint should return 200 OK`() {
        // Add debug log to see the actual path
        println("[DEBUG_LOG] Testing API docs endpoint at: /api-docs")
        
        webTestClient.get()
            .uri("/api-docs")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { response ->
                println("[DEBUG_LOG] API Docs response size: ${response.responseBody?.size ?: 0} bytes")
            }
    }
    
    @Test
    fun `swagger-ui endpoint should be accessible`() {
        println("[DEBUG_LOG] Testing Swagger UI endpoint at: /swagger-ui.html")
        
        webTestClient.get()
            .uri("/swagger-ui.html")
            .exchange()
            .expectStatus().is3xxRedirection
            .expectHeader().valueEquals("Location", "/webjars/swagger-ui/index.html")
    }
}