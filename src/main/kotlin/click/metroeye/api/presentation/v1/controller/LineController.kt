package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.service.LineService
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.LineResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/lines")
@Tag(name = "Line API", description = "호선 API")
class LineController(
    private val lineService: LineService
) {
    @Operation(
        summary = "호선 목록 조회 API",
        method = "GET",
        description = """
*호선 목록을 조회합니다.*            
"""
    )
    @GetMapping
    fun getLines(): Mono<ResponseEntity<ApiResponse<List<LineResponse>>>> {
        return lineService.getLines()
            .map { lineResponses ->
                ResponseEntity.ok(
                    ApiResponse(
                        "조회되었습니다.",
                        "SUCCESS",
                        lineResponses
                    )
                )
            }
    }
}