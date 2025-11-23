package click.metroeye.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

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
    fun apiSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/v1/subways/**"
            ))
            .csrf { it.disable() }
            .authorizeExchange {
                it.anyExchange().permitAll()
            }
            .build()
    }
}