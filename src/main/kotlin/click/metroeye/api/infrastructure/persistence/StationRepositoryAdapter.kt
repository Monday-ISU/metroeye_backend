package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Line
import click.metroeye.api.domain.Station
import io.r2dbc.spi.Row
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class StationRepositoryAdapter(
    private val databaseClient: DatabaseClient
) {
    suspend fun loadStations(lineId: Long? = null): List<Station> {
        val bindParams = listOfNotNull(
            lineId?.let { "lineId" to it }
        )

        val whereClause = listOfNotNull(
            lineId?.let { "l.id = :lineId" }
        ).joinToString(" AND ").let {
            if (it.isBlank()) "" else "WHERE $it"
        }

        return databaseClient.sql(
            """
                SELECT
                    s.id AS station_id,
                    s.name AS station_name,
                    ls.station_code AS station_code,
                    ls.fr_code AS fr_code,
                    l.id AS line_id,
                    l.name AS line_name,
                    l.code AS line_code,
                    l.color AS line_color
                FROM `line_stations` AS ls
                INNER JOIN `stations` AS s ON ls.station_id = s.id
                INNER JOIN `lines` AS l ON ls.line_id = l.id
                $whereClause
                ORDER BY l.display_order ASC
            """.trimIndent())
            .let { spec ->
                bindParams.fold(spec) { boundSpec, (key, value) -> boundSpec.bind(key, value) }
            }
            .map { row ->
                Station(
                    id = row.get("station_id", Long::class.java)!!,
                    name = row.get("station_name", String::class.java)!!,
                    stationCode = row.get("station_code", String::class.java)!!,
                    externalCode = row.get("fr_code", String::class.java)!!,
                    line = Line(
                        id = row.get("line_id", Long::class.java)!!,
                        name = row.get("line_name", String::class.java)!!,
                        code = row.get("line_code", String::class.java)!!,
                        color = row.get("line_color", String::class.java)!!
                    )
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }
}
