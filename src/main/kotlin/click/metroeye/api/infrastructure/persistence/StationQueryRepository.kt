package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Station
import io.r2dbc.spi.Row
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class StationQueryRepository(
    private val databaseClient: DatabaseClient
) {
    fun loadStations(): Flux<Station> {
        return databaseClient.sql(
            """
                SELECT
                    s.id AS station_id,
                    s.name AS station_name,
                    ls.station_code AS station_code,
                    ls.fr_code AS fr_code,
                    l.id AS line_id,
                    l.name AS line_name,
                    l.code AS line_code
                FROM `line_stations` ls
                INNER JOIN `stations` s ON s.id = ls.station_id
                INNER JOIN `lines` l ON l.id = ls.line_id
                ORDER BY l.display_order ASC, ls.id ASC
            """.trimIndent()
        )
            .map { row, _ -> mapRow(row) }
            .all()
    }

    fun loadStationsByLineId(lineId: Long): Flux<Station> {
        return databaseClient.sql(
            """
                SELECT
                    s.id AS station_id,
                    s.name AS station_name,
                    ls.station_code AS station_code,
                    ls.fr_code AS fr_code,
                    l.id AS line_id,
                    l.name AS line_name,
                    l.code AS line_code
                FROM `line_stations` ls
                INNER JOIN `stations` s ON s.id = ls.station_id
                INNER JOIN `lines` l ON l.id = ls.line_id
                WHERE l.id = ?
                ORDER BY ls.id ASC
            """.trimIndent()
        )
            .bind(0, lineId)
            .map { row, _ -> mapRow(row) }
            .all()
    }

    fun loadStationsByLineKey(lineKey: String): Flux<Station> {
        return databaseClient.sql(
            """
                SELECT
                    s.id AS station_id,
                    s.name AS station_name,
                    ls.station_code AS station_code,
                    ls.fr_code AS fr_code,
                    l.id AS line_id,
                    l.name AS line_name,
                    l.code AS line_code
                FROM `line_stations` ls
                INNER JOIN `stations` s ON s.id = ls.station_id
                INNER JOIN `lines` l ON l.id = ls.line_id
                WHERE l.name = ? OR l.code = ?
                ORDER BY ls.id ASC
            """.trimIndent()
        )
            .bind(0, lineKey)
            .bind(1, lineKey)
            .map { row, _ -> mapRow(row) }
            .all()
    }

    private fun mapRow(row: Row): Station {
        val stationId = row.get("station_id", java.lang.Long::class.java)!!
        val stationName = row.get("station_name", String::class.java)!!
        val stationCode = row.get("station_code", String::class.java)!!
        val externalCode = row.get("fr_code", String::class.java)!!
        val lineId = row.get("line_id", java.lang.Long::class.java)!!
        val lineName = row.get("line_name", String::class.java)!!
        val lineCode = row.get("line_code", String::class.java)!!

        return Station(
            id = stationId.toLong(),
            name = stationName,
            stationCode = stationCode,
            externalCode = externalCode,
            lineId = lineId.toLong(),
            lineName = lineName,
            lineCode = lineCode
        )
    }
}
