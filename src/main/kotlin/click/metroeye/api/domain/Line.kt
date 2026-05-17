package click.metroeye.api.domain

class Line(
    val id: Long,
    val name: String,
    val color: String
) {
    companion object {
        fun of(id: Long, name: String, color: String): Line {
            return Line(id, name, color)
        }
    }
}