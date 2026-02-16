package click.metroeye.api.infrastructure.external.common

import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class WebClientAdapter(
    private val webClient: WebClient
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WebClientAdapter::class.java)
    }

    fun <T> get(
        uri: String,
        requestParams: Map<String, Any>,
        responseType: ParameterizedTypeReference<T>
    ): Mono<T> {
        val requestUri = try {
            UriComponentsBuilder.fromUriString(uri)
                .apply { requestParams.forEach { (key, value) -> queryParam(key, value) } }
                .encode()
                .build()
                .toUri()
        } catch (e: Exception) {
            e.printStackTrace()
            return Mono.empty()
        }

        return webClient.get()
            .uri(requestUri)
            .retrieve()
            .bodyToMono(responseType)
    }
}