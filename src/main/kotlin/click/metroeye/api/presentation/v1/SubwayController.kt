package click.metroeye.api.presentation.v1

import click.metroeye.api.application.SubwayService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/subways")
class SubwayController(
    private val subwayService: SubwayService
) {
    @GetMapping(path = ["/arrivals"], produces = [APPLICATION_JSON_VALUE])
    fun getArrivals(
        @RequestParam(value = "startIndex") startIndex: Int,
        @RequestParam(value = "endIndex") endIndex: Int,
        @RequestParam(value = "station") station: String
    ): ResponseEntity<Map<String, Any?>> {
        val subwayApiResponse = subwayService.getArrivalsByStation(startIndex, endIndex, station)

       return ResponseEntity.ok(mapOf(
            "status" to 200,
            "client_message" to "조회되었습니다.",
            "server_message" to "SUCCESS",
            "data" to subwayApiResponse
        ))
    }
}