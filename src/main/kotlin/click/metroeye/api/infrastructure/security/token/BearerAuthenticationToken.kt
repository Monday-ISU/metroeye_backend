package click.metroeye.api.infrastructure.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken

class BearerAuthenticationToken(
    private val token: String
) : AbstractAuthenticationToken(null) {
    override fun getCredentials(): Any = token
    override fun getPrincipal(): Any? = null

    init {
        isAuthenticated = false
    }
}