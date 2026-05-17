package click.metroeye.api.domain

class Station(
    val id: Long,
    val name: String,
    val code: String,
    val prevCode: String?,
    val nextCode: String?,
    val line: Line
)