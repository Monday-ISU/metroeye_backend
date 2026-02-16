package click.metroeye.api.infrastructure.crypto.secret

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class SecureRandomSecretAdapter {
    private val secureRandom = SecureRandom()
    private val base64Encoder = Base64.getUrlEncoder().withoutPadding()

    fun generate(byteLength: Int = 24): String {
        val bytes = ByteArray(byteLength)
        secureRandom.nextBytes(bytes)
        return base64Encoder.encodeToString(bytes)
    }
}