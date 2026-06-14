package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Line
import click.metroeye.api.domain.Station
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class StationRepositoryAdapter(
    private val databaseClient: DatabaseClient
) {
    suspend fun loadStationById(stationId: Long): Station? {
        return databaseClient.sql(
            """
                SELECT
                    s.id AS station_id,
                    s.name AS station_name,
                    ls.code AS station_code,
                    ls.prev_code AS prev_code,
                    ls.next_code AS next_code,
                    ls.is_terminal AS is_terminal,
                    l.id AS line_id,
                    l.name AS line_name,
                    l.color AS line_color
                FROM `line_stations` AS ls
                INNER JOIN `stations` AS s ON ls.station_id = s.id
                INNER JOIN `lines` AS l ON ls.line_id = l.id
                WHERE s.id = :stationId
                LIMIT 1
            """.trimIndent())
            .bind("stationId", stationId)
            .map { row ->
                Station.of(
                    id = row.get("station_id", Long::class.java)!!,
                    name = row.get("station_name", String::class.java)!!,
                    code = row.get("station_code", String::class.java)!!,
                    prevCode = row.get("prev_code", String::class.java),
                    nextCode = row.get("next_code", String::class.java),
                    line = Line.of(
                        id = row.get("line_id", Long::class.java)!!,
                        name = row.get("line_name", String::class.java)!!,
                        color = row.get("line_color", String::class.java)!!
                    )
                )
            }
            .all()
            .next()
            .awaitFirstOrNull()
    }

    suspend fun existsStationOnLine(stationId: Long, lineId: Long): Boolean {
        return databaseClient.sql(
            """
                SELECT COUNT(*) AS cnt
                FROM `line_stations` AS ls
                INNER JOIN `stations` AS s ON ls.station_id = s.id
                WHERE s.id = :stationId AND ls.line_id = :lineId
            """.trimIndent())
            .bind("stationId", stationId)
            .bind("lineId", lineId)
            .map { row -> row.get("cnt", Long::class.java)!! > 0 }
            .one()
            .awaitSingleOrNull() ?: false
    }

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
                    ls.code AS station_code,
                    ls.prev_code AS prev_code,
                    ls.next_code AS next_code,
                    ls.is_terminal AS is_terminal,
                    l.id AS line_id,
                    l.name AS line_name,
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
                Station.of(
                    id = row.get("station_id", Long::class.java)!!,
                    name = row.get("station_name", String::class.java)!!,
                    code = row.get("station_code", String::class.java)!!,
                    prevCode = row.get("prev_code", String::class.java),
                    nextCode = row.get("next_code", String::class.java),
                    line = Line.of(
                        id = row.get("line_id", Long::class.java)!!,
                        name = row.get("line_name", String::class.java)!!,
                        color = row.get("line_color", String::class.java)!!
                    )
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }
}
