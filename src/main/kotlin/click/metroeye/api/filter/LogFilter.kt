package click.metroeye.api.filter

import click.metroeye.api.decorator.CachedContentServerHttpResponse
import click.metroeye.api.util.RequestHeaderUtils
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
        val path = exchange.request.uri.path

        if (!path.startsWith("/v1")) {
            return chain.filter(exchange)
        }

        logger.info("""
            
            
            |[REQUEST]
            |>> METHOD: ${exchange.request.method}
            |>> REQUEST URI: ${exchange.request.uri.path}
            |>> CLIENT IP: ${RequestHeaderUtils.getClientIp(exchange.request)}
            |>> HEADERS: ${RequestHeaderUtils.getStringHeaders(exchange.request)}
            |>> REQUEST PARAM: ${exchange.request.queryParams}
            """.trimIndent()
        )

        val cachingResponse = CachedContentServerHttpResponse(exchange.response)

        val mutatedExchange = exchange.mutate()
            .response(cachingResponse)
            .build()

        return chain.filter(mutatedExchange)
            .doFinally {
                val status = exchange.response.statusCode?.value() ?: 200

                logger.info("""
            
            
                    |[RESPONSE]
                    |>> STATUS: $status
                    ${if (status != 200) "|>> RESPONSE BODY: ${cachingResponse.getCachedContent()}" else ""}   
                    """.trimIndent())
            }
    }
}