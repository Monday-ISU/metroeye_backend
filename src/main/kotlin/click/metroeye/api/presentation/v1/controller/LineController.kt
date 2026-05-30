package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.service.LineService
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.LineResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "clientMessage": "조회되었습니다.",
                                        "serverMessage": "SUCCESS",
                                        "data": [
                                            {
                                                "id": 0,
                                                "name": "string",
                                                "code": "string",
                                                "color": "string"
                                            }
                                        ]
                                    }
                                """,
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping
    suspend fun getLines(): ResponseEntity<ApiResponse<List<LineResponse>>> {
        val lineResponses = lineService.getLines()

        return ResponseEntity.ok(
            ApiResponse(
                clientMessage = "조회되었습니다.",
                serverMessage = "Success",
                data = lineResponses
            )
        )
    }
}