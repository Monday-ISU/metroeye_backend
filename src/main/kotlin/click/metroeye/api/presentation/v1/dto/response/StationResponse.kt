package click.metroeye.api.presentation.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "역 정보 응답 객체")
data class StationResponse(
    @field:Schema(description = "역 이름")
    val stationName: String,
    @field:Schema(description = "역 코드")
    val stationCode: String,
    @field:Schema(description = "호선 ID")
    val lineId: Long,
    @field:Schema(description = "호선 이름")
    val lineName: String,
    @field:Schema(description = "호선 코드")
    val lineCode: String,
    @field:Schema(description = "호선 색상")
    val lineColor: String
)
