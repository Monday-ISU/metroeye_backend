package click.metroeye.api.application.service

import click.metroeye.api.infrastructure.external.seoul.SeoulSubwayClientAdapter
import click.metroeye.api.infrastructure.persistence.StationRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.TrainResponse
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class TrainService(
    private val stationRepositoryAdapter: StationRepositoryAdapter,
    private val seoulSubwayClientAdapter: SeoulSubwayClientAdapter
) {
    suspend fun getTrains(stationId: Long): List<TrainResponse>? {
        val station = stationRepositoryAdapter.loadStationById(stationId) ?: return null

        val apiResponse = seoulSubwayClientAdapter
            .getRealtimeArrivalsByStation(1, 1000, station.name)
            .awaitSingleOrNull() ?: return emptyList()

        if (!apiResponse.success || apiResponse.data == null) return emptyList()

        return apiResponse.data.map { arrival ->
            TrainResponse(
                updnLine = arrival.updnLine,
                statnFid = arrival.statnFid,
                statnTid = arrival.statnTid,
                statnId = arrival.statnId,
                btrainSttus = arrival.btrainSttus,
                btrainNo = arrival.btrainNo,
                arvlCd = arrival.arvlCd,
                lstcarAt = arrival.lstcarAt
            )
        }
    }
}
