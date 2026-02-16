package click.metroeye.api.config

import click.metroeye.api.infrastructure.security.filter.BearerAuthenticationWebFilter
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    @Order(1)
    fun swaggerSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ))
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.pathMatchers(
                    "/",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
            }
            .build()
    }

    @Bean
    @Order(2)
    fun apiSecurityFilterChain(
        http: ServerHttpSecurity,
        bearerAuthenticationWebFilter: BearerAuthenticationWebFilter,
        objectMapper: ObjectMapper
    ): SecurityWebFilterChain {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/v1/lines/**"
            ))
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.anyExchange().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint { exchange, exception ->
                    val response = exchange.response
                    response.statusCode = HttpStatus.UNAUTHORIZED
                    response.headers.contentType = MediaType.APPLICATION_JSON
                    val apiResponse = ApiResponse(
                        "인증에 실패했습니다.",
                        "Access token is missing.",
                        null
                    )

                    val bytes = objectMapper.writeValueAsBytes(apiResponse)
                    val buffer = response.bufferFactory().wrap(bytes)
                    response.writeWith(Mono.just(buffer))
                }
            }
            .addFilterAt(
                bearerAuthenticationWebFilter,
                SecurityWebFiltersOrder.AUTHENTICATION
            )
            .build()
    }
}