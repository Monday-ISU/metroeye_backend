package click.metroeye.api.filter

import click.metroeye.api.filter.decorator.CachedContentServerHttpRequest
import click.metroeye.api.filter.decorator.CachedContentServerHttpResponse
import click.metroeye.api.util.getClientIp
import click.metroeye.api.util.toHeadersString
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class LogFilter : WebFilter {
    companion object {
        private val logger = LoggerFactory.getLogger(LogFilter::class.java)

        private val loggingPaths = listOf(
            "/v1/**"
        )
        private val antPathMatcher = AntPathMatcher()
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (loggingPaths.none { antPathMatcher.match(it, exchange.request.uri.path) }) {
            return chain.filter(exchange)
        }

        val requestBodyBytes: Mono<ByteArray> = if (exchange.request.headers.contentType?.includes(MediaType.APPLICATION_JSON) == true) {
            DataBufferUtils.join(exchange.request.body)
                .map { buffer ->
                    val bytes = ByteArray(buffer.readableByteCount())
                    buffer.read(bytes)
                    DataBufferUtils.release(buffer)
                    bytes
                }
                .defaultIfEmpty(ByteArray(0))
        } else {
            Mono.just(ByteArray(0))
        }

        return requestBodyBytes.flatMap { bytes ->
            logger.info("""
            
            
                |[REQUEST]
                |>> METHOD: ${exchange.request.method}
                |>> REQUEST URI: ${exchange.request.uri.path}
                |>> CLIENT IP: ${exchange.request.headers.getClientIp()}
                |>> HEADERS: ${exchange.request.headers.toHeadersString()}
                |>> REQUEST PARAM: ${exchange.request.queryParams.takeIf { it.isNotEmpty() } ?: ""}
                |>> REQUEST BODY: ${String(bytes, Charsets.UTF_8).replace(Regex("\\s+"), "").trim()}
            """.trimIndent())

            val cachingRequest = CachedContentServerHttpRequest(exchange.request, bytes)
            val cachingResponse = CachedContentServerHttpResponse(exchange.response)
            val mutatedExchange = exchange.mutate()
                .request(cachingRequest)
                .response(cachingResponse)
                .build()

            chain.filter(mutatedExchange)
                .doFinally {
                    val status = exchange.response.statusCode?.value()
                    val isSuccessStatus = exchange.response.statusCode?.is2xxSuccessful ?: false

                    logger.info("""
            
            
                        |[RESPONSE]
                        |>> STATUS: $status
                        ${if (!isSuccessStatus) "|>> RESPONSE BODY: ${cachingResponse.bodyAsString()}" else ""}   
                    """.trimIndent())
                }
        }
    }
}