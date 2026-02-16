package click.metroeye.api.infrastructure.security.manager

import click.metroeye.api.infrastructure.crypto.token.JsonWebTokenAdapter
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
    class BearerAuthenticationManager(
    private val jsonWebTokenAdapter: JsonWebTokenAdapter,

    @Value("\${jwt.secret}")
    private val jwtSecret: String
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val accessToken = authentication.credentials as String

        val claims = try {
            jsonWebTokenAdapter.parseClaims(accessToken, jwtSecret)
        } catch (e: ExpiredJwtException) {
            return Mono.error(
                BadCredentialsException("Access token has expired.")
            )
        } catch (e: JwtException) {
            return Mono.error(
                BadCredentialsException("Access token is invalid.")
            )
        }

        val uuid = claims.subject ?: return Mono.error(
            BadCredentialsException("Access token subject is missing.")
        )

        val tokenType = claims["type"] as? String ?: return Mono.error(
            BadCredentialsException("Token type is missing.")
        )

        if (tokenType != "ACCESS") {
            return Mono.error(
                BadCredentialsException("Token is not an access token.")
            )
        }

        return Mono.just(
            UsernamePasswordAuthenticationToken(
                uuid,
                null,
                emptyList()
            )
        )
    }
}