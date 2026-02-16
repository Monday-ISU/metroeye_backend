package click.metroeye.api.presentation.v1.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class IssueTokenRequest(
    @Schema(description = "인증 유형(CLIENT_CREDENTIALS, REFRESH_TOKEN)")
    val grantType: String,
    @Schema(description = "기기 고유 번호(CLIENT_CREDENTIALS 유형에서 필수)", required = false)
    val uuid: String?,
    @Schema(description = "기기 비밀키(CLIENT_CREDENTIALS 유형에서 필수)", required = false)
    val secret: String?,
    @Schema(description = "리프레시 토큰(REFRESH_TOKEN 유형에서 필수)", required = false)
    val refreshToken: String?
)
