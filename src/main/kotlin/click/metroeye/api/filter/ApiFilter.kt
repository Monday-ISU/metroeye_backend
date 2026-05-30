package click.metroeye.api.filter

import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class ApiFilter(
    private val minVersion: String,
    private val isMaintenance: Boolean,
    private val objectMapper: ObjectMapper
): WebFilter {
    companion object {
        private val logger = LoggerFactory.getLogger(ApiFilter::class.java)

        private val apiPaths = listOf(
            "/v1/**"
        )
        private val antPathMatcher = AntPathMatcher()
    }


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (apiPaths.none { antPathMatcher.match(it, exchange.request.uri.path) }) {
            return chain.filter(exchange)
        }

        val response = exchange.response

        if (isMaintenance) {
            return writeErrorResponse(
                response = response,
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE,
                clientMessage = "서버 점검 중입니다. 잠시 후 다시 시도해주세요.",
                serverMessage = "Server is under maintenance."
            )
        }

        val clientVersion = exchange.request.headers.getFirst("Client-Version")

        if (clientVersion.isNullOrBlank()) {
            return writeErrorResponse(
                response = response,
                httpStatus = HttpStatus.BAD_REQUEST,
                clientMessage = "클라이언트 버전이 누락되었습니다.",
                serverMessage = "Header.client-version is missing."
            )
        }

        val versionRegex = Regex("^\\d+\\.\\d+\\.\\d+$")

        if (!versionRegex.matches(clientVersion)) {
            return writeErrorResponse(
                response = response,
                httpStatus = HttpStatus.BAD_REQUEST,
                clientMessage = "유효하지 않은 클라이언트 버전입니다.",
                serverMessage = "Invalid client version."
            )
        }

        if (!isSupportedVersion(clientVersion, minVersion)) {
            return writeErrorResponse(
                response = response,
                httpStatus = HttpStatus.UPGRADE_REQUIRED,
                clientMessage = "서비스 이용을 위해 앱을 업데이트해주세요.",
                serverMessage = "Client version is below the minimum supported version."
            )
        }


        return chain.filter(exchange)
    }

    private fun isSupportedVersion(clientVersion: String, minVersion: String): Boolean {
        val clientVersionParts = clientVersion.split('.').map { it.toInt() }
        val minVersionParts = minVersion.split('.').map { it.toInt() }

        clientVersionParts.zip(minVersionParts).forEach { (clientVersionPart, minVersionPart) ->
            if (clientVersionPart != minVersionPart) {
                return clientVersionPart > minVersionPart
            }
        }

        return true
    }

    private fun writeErrorResponse(
        response: ServerHttpResponse,
        httpStatus: HttpStatus,
        clientMessage: String,
        serverMessage: String
    ): Mono<Void> {
        response.statusCode = httpStatus
        response.headers.contentType = MediaType.APPLICATION_JSON

        val apiResponse = ApiResponse(clientMessage, serverMessage, null)
        val bytes = objectMapper.writeValueAsBytes(apiResponse)
        val buffer = response.bufferFactory().wrap(bytes)
        return response.writeWith(Mono.just(buffer))
    }
}