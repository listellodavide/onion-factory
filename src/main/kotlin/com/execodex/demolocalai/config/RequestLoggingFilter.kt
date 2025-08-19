package com.execodex.demolocalai.config

import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * Logs every incoming HTTP request and its corresponding response status with duration.
 */
@Component
class RequestLoggingFilter : WebFilter, Ordered {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 5

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val method = request.method?.toString() ?: "UNKNOWN"
        val path = request.path.pathWithinApplication().value()
        val query = request.uri.rawQuery?.let { if (it.isNotEmpty()) "?" + it else "" } ?: ""
        val clientIp = exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"
        val userAgent = request.headers.getFirst("User-Agent") ?: "unknown"

        val start = System.currentTimeMillis()
        log.info("Incoming request: {} {}{} from={} ua=\"{}\"", method, path, query, clientIp, userAgent)

        return chain.filter(exchange)
            .doFinally {
                val status = exchange.response.statusCode?.value() ?: 200
                val duration = System.currentTimeMillis() - start
                log.info("Completed: {} {} -> {} in {} ms", method, path, status, duration)
            }
    }
}
