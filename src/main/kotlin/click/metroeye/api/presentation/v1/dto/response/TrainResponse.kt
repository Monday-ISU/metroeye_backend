package click.metroeye.api.presentation.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "실시간 열차 도착 정보")
data class TrainResponse(
    @Schema(description = "지하철 호선 ID")
    val subwayId: String?,
    @Schema(description = "상하행선 구분 (0: 상행, 1: 하행)")
    val updnLine: String?,
    @Schema(description = "이전 지하철역 ID")
    val statnFid: String?,
    @Schema(description = "다음 지하철역 ID")
    val statnTid: String?,
    @Schema(description = "현재 지하철역 ID")
    val statnId: String?,
    @Schema(description = "열차 번호")
    val btrainNo: String?,
    @Schema(description = "열차 종류 (급행, ITX, 일반, 특급)")
    val btrainSttus: String?,
    @Schema(description = "열차 도착 예정 시간 (초)")
    val barvlDt: String?,
    @Schema(description = "종착 지하철역명")
    val bstatnNm: String?,
    @Schema(description = "첫번째 도착 메시지")
    val arvlMsg2: String?,
    @Schema(description = "두번째 도착 메시지")
    val arvlMsg3: String?,
    @Schema(description = "도착 코드 (0: 진입, 1: 도착, 2: 출발, 3: 전역출발, 4: 전역도착, 5: 전역진입)")
    val arvlCd: String?,
    @Schema(description = "막차 여부 (0: 막차 아님, 1: 막차)")
    val lstcarAt: String?
)
