package click.metroeye.api.infrastructure.crypto.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JsonWebTokenAdapter {
    fun generate(
        subject: String,
        claims: Map<String, Any>,
        expirationMillis: Long,
        secret: String
    ): String {
        val currentDate = Date()
        val expirationDate = Date(currentDate.time + expirationMillis)
        val signingKey = Keys.hmacShaKeyFor(secret.toByteArray())

        return Jwts.builder()
            .header().add("typ", "JWT").and()
            .subject(subject)
            .claims(claims)
            .issuedAt(currentDate)
            .expiration(expirationDate)
            .signWith(signingKey)
            .compact()
    }

    fun parseClaims(token: String, secret: String): Claims {
        val signingKey = Keys.hmacShaKeyFor(secret.toByteArray())

        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}