package com.execodex.demolocalai.handlers.errors

import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Service
class ProductErrorHandler {
    
    fun handleError(error: Throwable): Mono<ServerResponse> {
        println("ProductErrorHandler: Handling error of type ${error.javaClass.name}")
        println("ProductErrorHandler: Error message: ${error.message}")
        
        return when (error) {
            is IllegalArgumentException -> {
                println("ProductErrorHandler: Handling IllegalArgumentException")
                val problemDetail = ProblemDetail.forStatus(409)
                    .apply {
                        title = "Product Already Exists"
                        detail = error.message ?: "A product with the same SKU already exists."
                        type = URI.create("https://example.com/errors/product-already-exists")
                    }
                
                ServerResponse.status(409)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .bodyValue(problemDetail)
            }
            else -> {
                println("ProductErrorHandler: Handling unknown error type")
                val problemDetail = ProblemDetail.forStatus(500)
                    .apply {
                        title = "Internal Server Error"
                        detail = "An unexpected error occurred: ${error.message}"
                        type = URI.create("https://example.com/errors/internal-server-error")
                    }
                
                ServerResponse.status(500)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .bodyValue(problemDetail)
            }
        }
    }
}