package click.metroeye.api.presentation.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공통 API 응답 객체")
data class ApiResponse<T>(
    @Schema(description = "클라이언트 메시지")
    val clientMessage: String,
    @Schema(description = "서버 메시지")
    val serverMessage: String,
    @Schema(description = "응답 데이터", nullable = true)
    val data: T?
)