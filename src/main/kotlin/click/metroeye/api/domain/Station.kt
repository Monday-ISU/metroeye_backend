package click.metroeye.api.domain

class Station private constructor(
    val id: Long,
    val name: String,
    val code: String,
    val prevCode: String?,
    val nextCode: String?,
    val line: Line
) {
    companion object {
        fun of(id: Long, name: String, code: String, prevCode: String?, nextCode: String?, line: Line): Station {
            return Station(id, name, code, prevCode, nextCode, line)
        }
    }
}