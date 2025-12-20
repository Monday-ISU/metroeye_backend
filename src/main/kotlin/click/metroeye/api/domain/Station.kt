package click.metroeye.api.domain

class Station(
    val stationId: Long, // PK
    val stationName: String, // 역 이름
    val stationCode: String, // 공공 데이터 역 코드
    val lines: List<Line> // 관련 호선 목록
) {
}