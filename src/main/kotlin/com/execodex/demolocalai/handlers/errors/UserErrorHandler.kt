package com.execodex.demolocalai.handlers.errors

import org.springframework.dao.DuplicateKeyException
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Service
class UserErrorHandler {
    private lateinit var exceptionHandler: MutableMap<Class<out Throwable>, (Throwable) -> Mono<ServerResponse>>
    
    constructor() {
        exceptionHandler = mutableMapOf()
        exceptionHandler[DuplicateKeyException::class.java] = handleUserAlreadyExistsException
    }


    val handleError: (Throwable) -> Mono<ServerResponse> = { error ->
        exceptionHandler.getOrDefault(
            error::class.java,
            { e ->
                val problemDetail = ProblemDetail.forStatus(500)
                    .apply {
                        title = "A new Internal Server Error"
                        detail = "An unhandlerd, unexpected error occurred ${e.message ?: "Unknown error"}"
                        type = URI.create("https://example.com/errors/internal-server-error")
                    }
                ServerResponse.status(500)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .bodyValue(problemDetail)
            }
        )(error)
    }

    private val handleUserAlreadyExistsException: (Throwable) -> Mono<ServerResponse> = { error ->
        val problemDetail = ProblemDetail.forStatus(409)
            .apply {
                title = "User Already Exists"
                detail = error.message ?: "A user with the same username or email already exists."
                type = URI.create("https://example.com/errors/user-already-exists")
            }

        ServerResponse.status(409)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .bodyValue(problemDetail)
    }


}