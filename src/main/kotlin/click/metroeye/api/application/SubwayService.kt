package click.metroeye.api.application

import click.metroeye.api.infrastructure.client.seoul.SeoulSubwayClientAdapter
import click.metroeye.api.infrastructure.client.seoul.dto.RealtimeArrivalInfo
import click.metroeye.api.infrastructure.client.seoul.dto.SeoulSubwayApiResponse
import org.springframework.stereotype.Service

@Service
class SubwayService(
    private val seoulSubwayClientAdapter: SeoulSubwayClientAdapter
) {
    fun getArrivalsByStation(startIndex: Int, endIndex: Int, stationName: String): SeoulSubwayApiResponse<List<RealtimeArrivalInfo>> {
        return seoulSubwayClientAdapter.getArrivalsByStation(startIndex, endIndex, stationName)
    }
}