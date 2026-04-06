package click.metroeye.api.domain

class Station(
    val id: Long,
    val name: String,
    val stationCode: String,
    val externalCode: String,
    val lineId: Long,
    val lineName: String,
    val lineCode: String
)