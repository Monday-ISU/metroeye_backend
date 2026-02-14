package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.StationService
import click.metroeye.api.application.dto.RealtimeStationArrivalRequestModel
import click.metroeye.api.presentation.v1.dto.request.RealtimeStationArrivalRequest
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import org.springframework.http.MediaType
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
    @GetMapping(path = ["/{stationName}/arrivals/realtime"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRealtimeArrivalsByStation(
        @PathVariable stationName: String,
        realtimeStationArrivalRequest: RealtimeStationArrivalRequest
    ): Mono<ResponseEntity<ApiResponse<Map<String, Any?>>>> {
        val realtimeStationArrivalRequestModel = RealtimeStationArrivalRequestModel(
            stationName,
            realtimeStationArrivalRequest.lineName
        )

        return subwayService.getArrivalsByStation(realtimeStationArrivalRequestModel)
            .map { subwayApiResponse ->
                ResponseEntity.ok(
                    ApiResponse(
                        "조회되었습니다.",
                        "SUCCESS",
                        subwayApiResponse
                    )
                )
            }
    }
}