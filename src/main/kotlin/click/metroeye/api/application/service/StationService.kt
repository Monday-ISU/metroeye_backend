package click.metroeye.api.application.service

import click.metroeye.api.infrastructure.persistence.StationRepositoryAdapter
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
                stationName = loadedStation.name,
                stationCode = loadedStation.code,
                lineId = loadedStation.line.id!!
            )
        }
    }
}
