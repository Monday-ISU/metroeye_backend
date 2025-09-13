package click.metroeye.api.infrastructure.client.common

import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Component
class WebClientAdapter(
    webClientBuilder: WebClient.Builder
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WebClientAdapter::class.java)
    }

    private val webClient: WebClient = webClientBuilder
        .filter(requestFilter())
        .filter(responseFilter())
        .build()

    fun <T> get(
        uri: String,
        requestParams: Map<String, Any>,
        responseType: ParameterizedTypeReference<T>
    ): T? {
        try {
            val requestUri: URI = UriComponentsBuilder
                .fromUriString(uri)
                .apply { requestParams.forEach { (key, value) -> queryParam(key, value) } }
                .build()
                .toUri()

            return this.webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(responseType)
                .block()
        } catch(e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun requestFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor {
            request ->
            logger.info("""
                
                
                |[WEBCLIENT REQUEST]
                |>> METHOD: {}
                |>> URL: {} 
                """.trimIndent(),
                request.method(),
                request.url()
            )
            Mono.just(request)
        }
    }

    private fun responseFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor {
                response ->
            logger.info("""
                
                
                |[WEBCLIENT RESONSE]
                |>> STATUS: {}
                """.trimIndent(),
                response.statusCode()
            )
            Mono.just(response)
        }
    }
}