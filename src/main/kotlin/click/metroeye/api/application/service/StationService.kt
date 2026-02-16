package click.metroeye.api.application.service

import click.metroeye.api.application.dto.RealtimeStationArrivalRequestModel
import click.metroeye.api.infrastructure.external.seoul.SeoulSubwayClientAdapter
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StationService(
    private val seoulSubwayClientAdapter: SeoulSubwayClientAdapter
) {
    fun getArrivalsByStation(
        realtimeStationArrivalRequestModel: RealtimeStationArrivalRequestModel
    ): Mono<Map<String, Any?>> {
        val startIndex = 1
        val endIndex = 100
        val stationName = realtimeStationArrivalRequestModel.stationName
        val lineName = realtimeStationArrivalRequestModel.lineName
        val realtimeArrivalResponseMono = seoulSubwayClientAdapter.getRealtimeArrivalsByStation(startIndex, endIndex, stationName)
        val realtimePositionResponseMono = seoulSubwayClientAdapter.getRealtimePositionsByLine(startIndex, endIndex, lineName)

        return Mono.zip(realtimeArrivalResponseMono, realtimePositionResponseMono)
            .map { tuple ->
                val realtimeArrivalResponse = tuple.t1
                val realtimePositionResponse = tuple.t2

                if(realtimeArrivalResponse.success && realtimePositionResponse.success) {
                    mapOf(
                        "realtimeArrival" to realtimeArrivalResponse.data,
                        "realtimePosition" to realtimePositionResponse.data
                    )
                } else {
                    mapOf(
                        "realtimeArrival" to emptyList<Any>(),
                        "realtimePosition" to emptyList<Any>()
                    )
                }
            }
    }
}