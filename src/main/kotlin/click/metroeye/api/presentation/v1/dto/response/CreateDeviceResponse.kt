package click.metroeye.api.presentation.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "기기 등록 응답 객체")
data class CreateDeviceResponse(
    @field:Schema(description = "기기 비밀키")
    val secret: String,
    @field:Schema(description = "엑세스 토큰")
    val accessToken: String,
    @field:Schema(description = "리프레시 토큰")
    val refreshToken: String,
    @field:Schema(description = "엑세스 토큰 만료까지 남은 시간(초)")
    val expiresIn: Long
)
