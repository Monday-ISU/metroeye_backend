package click.metroeye.api.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(WebClientConfig::class.java)
    }

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .filter(requestFilter())
            .filter(responseFilter())
            .filter(requestErrorFilter())
            .build()
    }

    fun requestFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { request ->
            logger.info("""
                
                
                |[WEBCLIENT REQUEST]
                |>> METHOD: ${request.method()}
                |>> URL: ${request.url()}
                """.trimIndent()
            )
            Mono.just(request)
        }
    }

    fun responseFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { response ->
            logger.info("""
                
                
                |[WEBCLIENT RESPONSE]
                |>> STATUS: ${response.statusCode()}
                """.trimIndent()
            )
            Mono.just(response)
        }
    }

    fun requestErrorFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction { request, function ->
            function.exchange(request)
                .onErrorResume {
                    logger.error("""
                        
                        
                        |[WEBCLIENT REQUEST ERROR]
                        |>> MESSAGE: ${it.message}
                    """.trimIndent()
                    )
                    Mono.empty()
                }
        }
    }
}