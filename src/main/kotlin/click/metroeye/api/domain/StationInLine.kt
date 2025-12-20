package click.metroeye.api.domain

class StationInLine(
    val stationInLineId: Long, // PK
    val lineId: Long, // 호선 PK (FK)
    val stationId: Long // 역 PK (FK)
) {
}