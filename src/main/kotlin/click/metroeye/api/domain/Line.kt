package click.metroeye.api.domain

class Line(
    val lineId: Long, // PK
    val lineName: String, // 호선 이름
    val lineCode: String, // 공공 데이터 호선 코드
    val lineColor: String // 호선 색상
) {
}