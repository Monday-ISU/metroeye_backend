package click.metroeye.api.presentation.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "호선 응답 객체")
data class LineResponse(
    @field:Schema(description = "호선 ID")
    val lineId: Long?,
    @field:Schema(description = "호선 이름")
    val lineName: String,
    @field:Schema(description = "호선 색상")
    val color: String
)
