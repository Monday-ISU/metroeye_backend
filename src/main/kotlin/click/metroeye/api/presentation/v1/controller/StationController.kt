package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.service.StationService
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.StationResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stations")
@Tag(name = "Station API", description = "역 API")
class StationController(
    private val stationService: StationService
) {
    @Operation(
        summary = "역 목록 조회 API",
        method = "GET",
        description = """
*역 목록을 조회합니다.*

### [Query Parameter]

- **lineId**: 호선 ID
"""
    )
    @GetMapping
    suspend fun getStations(
        @Parameter(description = "호선 ID")
        @RequestParam(required = false) lineId: Long?
    ): ResponseEntity<ApiResponse<List<StationResponse>>> {
        val stationResponses = stationService.getStations(lineId)

        return ResponseEntity.ok(
            ApiResponse(
                "조회되었습니다.",
                "SUCCESS",
                stationResponses
            )
        )
    }
}
