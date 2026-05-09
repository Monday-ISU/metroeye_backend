package click.metroeye.api.filter

import click.metroeye.api.decorator.CachedContentServerHttpRequest
import click.metroeye.api.decorator.CachedContentServerHttpResponse
import click.metroeye.api.util.getClientIp
import click.metroeye.api.util.toHeadersString
import org.slf4j.LoggerFactory
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


class LogFilter : WebFilter {
    companion object {
        private val logger = LoggerFactory.getLogger(LogFilter::class.java)
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void?> {
        if (!exchange.request.uri.path.startsWith("/v1")) {
            return chain.filter(exchange)
        }

        val cachingRequest = CachedContentServerHttpRequest(exchange.request)
        val cachingResponse = CachedContentServerHttpResponse(exchange.response)

        val mutatedExchange = exchange.mutate()
            .request(cachingRequest)
            .response(cachingResponse)
            .build()

        return chain.filter(mutatedExchange)
            .doFinally {
                logger.info("""
            
            
                    |[REQUEST]
                    |>> METHOD: ${exchange.request.method}
                    |>> REQUEST URI: ${exchange.request.uri.path}
                    |>> CLIENT IP: ${exchange.request.headers.getClientIp()}
                    |>> HEADERS: ${exchange.request.headers.toHeadersString()}
                    |>> REQUEST PARAM: ${exchange.request.queryParams.takeIf { it.isNotEmpty() } ?: ""}
                    |>> REQUEST BODY: ${cachingRequest.getCachedContent().replace(Regex("\\s+"), "").trim()}
                    """.trimIndent()
                )

                val status = exchange.response.statusCode?.value() ?: 200

                logger.info("""
            
            
                    |[RESPONSE]
                    |>> STATUS: $status
                    ${if (status != 200) "|>> RESPONSE BODY: ${cachingResponse.getCachedContent()}" else ""}   
                    """.trimIndent())
            }
    }
}