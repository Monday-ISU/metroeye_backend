package click.metroeye.api.application.service

import click.metroeye.api.infrastructure.persistence.StationRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.StationResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StationService(
    private val stationRepositoryAdapter: StationRepositoryAdapter
) {

    @Transactional(readOnly = true)
    suspend fun getStations(lineId: Long?): List<StationResponse> {
        val loadedStations = stationRepositoryAdapter.loadStations(lineId)

        return loadedStations
            .distinctBy { it.stationCode }
            .map { loadedStation ->
            StationResponse(
                stationName = loadedStation.name,
                stationCode = loadedStation.stationCode,
                lineId = loadedStation.line.id!!,
                lineName = loadedStation.line.name,
                lineCode = loadedStation.line.code,
                lineColor = loadedStation.line.color
            )
        }
    }
}
