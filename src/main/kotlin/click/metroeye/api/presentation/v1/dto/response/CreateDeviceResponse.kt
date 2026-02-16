package click.metroeye.api.presentation.v1.dto.response

data class CreateDeviceResponse(
    val secret: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
