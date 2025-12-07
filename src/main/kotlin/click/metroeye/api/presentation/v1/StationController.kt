package click.metroeye.api.presentation.v1

import click.metroeye.api.application.StationService
import click.metroeye.api.presentation.v1.dto.RealtimeStationArrivalRequest
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/stations")
class StationController(
    private val subwayService: StationService
) {
    @GetMapping(path = ["/{station}/arrivals/realtime"], produces = [APPLICATION_JSON_VALUE])
    fun getRealtimeArrivalsByStation(
        @PathVariable station: String,
        realtimeStationArrivalRequest: RealtimeStationArrivalRequest
    ): Mono<ResponseEntity<Map<String, Any?>>> {
        return subwayService.getArrivalsByStation(
            station,
            realtimeStationArrivalRequest.line,
            realtimeStationArrivalRequest.direction
        )
            .map { subwayApiResponse ->
                ResponseEntity.ok(
                    mapOf(
                        "status" to 200,
                        "client_message" to "조회되었습니다.",
                        "server_message" to "SUCCESS",
                        "data" to subwayApiResponse
                    )
                )
            }
    }
}