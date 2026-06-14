package click.metroeye.api.application.service

import click.metroeye.api.constants.DirectionType
import click.metroeye.api.constants.ErrorCode
import click.metroeye.api.domain.Station
import click.metroeye.api.exception.InvalidStationException
import click.metroeye.api.infrastructure.persistence.StationRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.AdjacentStationsResponse
import click.metroeye.api.presentation.v1.dto.response.StationResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StationService(
    private val stationRepositoryAdapter: StationRepositoryAdapter
) {
    companion object {
        private val logger = LoggerFactory.getLogger(StationService::class.java)
    }

    @Transactional(readOnly = true)
    suspend fun getStations(lineId: Long?): List<StationResponse> {
        val loadedStations = stationRepositoryAdapter.loadStations(lineId)

        return loadedStations
            .distinctBy { it.code }
            .map { loadedStation ->
            StationResponse(
                stationId = loadedStation.id,
                stationName = loadedStation.name,
                stationCode = loadedStation.code,
                lineId = loadedStation.line.id
            )
        }
    }

    @Transactional(readOnly = true)
    suspend fun getAdjacentStations(lineId: Long, stationId: Long, size: Int): List<AdjacentStationsResponse> {
        stationRepositoryAdapter.loadStationById(stationId)
            ?: throw InvalidStationException(ErrorCode.STATION_NOT_FOUND)

        val loadedStations = stationRepositoryAdapter.loadStations(lineId)
        val selectedStation = loadedStations.find { it.id == stationId }
            ?: throw InvalidStationException(ErrorCode.STATION_LINE_NOT_MATCH)

        val stationsByCode = loadedStations.groupBy { it.code }

        val nextStationRoutes = traverseAdjacentStationRoutes(selectedStation.code, stationsByCode, DirectionType.NEXT, size)
            .filter { it.isNotEmpty() }
            .map { (listOf(selectedStation.code) + it).reversed() }
        val prevStationRoutes = traverseAdjacentStationRoutes(selectedStation.code, stationsByCode, DirectionType.PREV, size)
            .filter { it.isNotEmpty() }
            .map { listOf(selectedStation.code) + it }


        return listOf(
            DirectionType.PREV to nextStationRoutes,
            DirectionType.NEXT to prevStationRoutes
        ).flatMap { (directionType, stationRoutes) ->
            stationRoutes.mapIndexed { index, stationCodes ->
                AdjacentStationsResponse(directionType, index, stationCodes)
            }
        }
    }

    private fun traverseAdjacentStationRoutes(
        currentStationCode: String,
        stationsByCode: Map<String, List<Station>>,
        directionType: DirectionType,
        count: Int
    ): List<List<String>> {
        if (count == 0) return listOf(emptyList())

        val currentStations = stationsByCode[currentStationCode] ?: return listOf(emptyList())

        return currentStations
            .map { station ->
                when (directionType) {
                    DirectionType.PREV -> station.prevCode
                    DirectionType.NEXT -> station.nextCode
                }
            }
            .distinct()
            .flatMap { adjacentStationCode ->
                adjacentStationCode ?: return@flatMap listOf(emptyList())
                traverseAdjacentStationRoutes(adjacentStationCode, stationsByCode, directionType, count - 1)
                    .map { adjacentStationCodes -> listOf(adjacentStationCode) + adjacentStationCodes }
            }
    }
}
