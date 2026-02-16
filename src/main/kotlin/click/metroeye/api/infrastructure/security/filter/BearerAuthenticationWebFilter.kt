package click.metroeye.api.infrastructure.security.filter

import click.metroeye.api.infrastructure.security.manager.BearerAuthenticationManager
import click.metroeye.api.infrastructure.security.token.BearerAuthenticationToken
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BearerAuthenticationWebFilter(
    private val bearerAuthenticationManager: BearerAuthenticationManager,
    private val objectMapper: ObjectMapper
) : AuthenticationWebFilter(bearerAuthenticationManager) {
    init {
        setServerAuthenticationConverter(bearerAuthenticationConverter())
        setAuthenticationFailureHandler { webFilterExchange, exception ->
            val exchange = webFilterExchange.exchange
            val response = exchange.response
            response.statusCode = HttpStatus.UNAUTHORIZED
            response.headers.contentType = MediaType.APPLICATION_JSON

            val apiResponse = ApiResponse(
                "인증에 실패했습니다.",
                exception.message ?: "Authentication failed.",
                null
            )

            val bytes = objectMapper.writeValueAsBytes(apiResponse)
            val buffer = response.bufferFactory().wrap(bytes)

            response.writeWith(Mono.just(buffer))
        }
    }

    private fun bearerAuthenticationConverter(): ServerAuthenticationConverter {
        return ServerAuthenticationConverter { exchange ->
            val headers = exchange.request.headers
            val authenticationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION)

            if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
                return@ServerAuthenticationConverter Mono.empty()
            }

            val accessToken = authenticationHeader.removePrefix("Bearer ").trim()

            Mono.just(
                BearerAuthenticationToken(accessToken)
            )
        }
    }
}