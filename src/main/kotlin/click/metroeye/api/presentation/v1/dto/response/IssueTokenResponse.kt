package click.metroeye.api.presentation.v1.dto.response

data class IssueTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
