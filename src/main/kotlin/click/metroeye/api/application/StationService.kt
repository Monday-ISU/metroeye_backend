package click.metroeye.api.application

import click.metroeye.api.infrastructure.client.seoul.SeoulSubwayClientAdapter
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StationService(
    private val seoulSubwayClientAdapter: SeoulSubwayClientAdapter
) {
    fun getArrivalsByStation(
        station: String,
        line: String,
        direction: String
    ): Mono<Map<String, Any?>> {
        val startIndex = 1
        val endIndex = 100
        val realtimeArrivalResponseMono = seoulSubwayClientAdapter.getRealtimeArrivalsByStation(startIndex, endIndex, station)
        val realtimePositionResponseMono = seoulSubwayClientAdapter.getRealtimePositionsByLine(startIndex, endIndex, line)

        return Mono.zip(realtimeArrivalResponseMono, realtimePositionResponseMono)
            .map { tuple ->
                val realtimeArrivalResponse = tuple.t1
                val realtimePositionResponse = tuple.t2

                if(realtimeArrivalResponse.success && realtimePositionResponse.success) {
                    val filteredRealtimeArrivalResponse = realtimeArrivalResponse.data?.filter {
                        it.updnLine == direction
                    }

                    val filteredRealtimePositionResponse = realtimePositionResponse.data?.filter {
                        it.updnLine == if (direction == "상행" || direction == "내선") "0" else "1"
                    }

                    mapOf(
                        "realtimeArrival" to filteredRealtimeArrivalResponse,
                        "realtimePosition" to filteredRealtimePositionResponse
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