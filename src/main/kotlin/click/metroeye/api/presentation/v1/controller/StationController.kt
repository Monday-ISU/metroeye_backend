package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.service.StationService
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.StationResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

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
*line 파라미터가 있으면 해당 호선의 역을 조회하고, 없으면 전체 역을 조회합니다.*
### [Query Parameter]
- **line**: 호선 ID/코드/이름 (예: 2, 02, 2호선)
"""
    )
    @GetMapping
    fun getStations(
        @RequestParam(required = false) line: String?
    ): Mono<ResponseEntity<ApiResponse<List<StationResponse>>>> {
        return stationService.getStations(line)
            .map { stationResponses ->
                ResponseEntity.ok(
                    ApiResponse(
                        "조회되었습니다.",
                        "SUCCESS",
                        stationResponses
                    )
                )
            }
    }
}
