package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.service.StationService
import click.metroeye.api.application.service.TrainService
import click.metroeye.api.presentation.v1.dto.response.AdjacentStationsResponse
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.StationResponse
import click.metroeye.api.presentation.v1.dto.response.TrainResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stations")
@Tag(name = "Station API", description = "역 API")
class StationController(
    private val stationService: StationService,
    private val trainService: TrainService
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
                                                "stationId": 0,
                                                "stationName": "string",
                                                "stationCode": "string",
                                                "lineId": 0
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
    suspend fun getStations(
        @Parameter(description = "호선 ID")
        @RequestParam(required = false) lineId: Long?
    ): ResponseEntity<ApiResponse<List<StationResponse>>> {
        val stationResponses = stationService.getStations(lineId)

        return ResponseEntity.ok(
            ApiResponse(
                clientMessage = "조회되었습니다.",
                serverMessage = "Success",
                data = stationResponses
            )
        )
    }

    @Operation(
        summary = "인접 역 목록 조회 API",
        method = "GET",
        description = """
*기준 역의 앞뒤 최대 3개의 인접 역 목록을 조회합니다.*

### [Path Variable]

- **stationId**: 역 ID

### [Query Parameter]

- **lineId**: 호선 ID
- **size**: 조회할 인접 역 수
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
                                                "directionType": "string",
                                                "stationCodes": [
                                                    "string"
                                                ]
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
    @GetMapping("/{stationId}/adjacent-stations")
    suspend fun getAdjacentStations(
        @Parameter(description = "역 ID", required = true)
        @PathVariable(value = "stationId") stationId: Long,
        @Parameter(description = "호선 ID", required = true)
        @RequestParam(value = "lineId") lineId: Long,
        @Parameter(description = "조회할 인접 역 수", required = true)
        @RequestParam(value = "size") size: Int,
    ): ResponseEntity<ApiResponse<List<AdjacentStationsResponse>>> {
        val adjacentStationsResponses = stationService.getAdjacentStations(lineId, stationId, size)

        return ResponseEntity.ok(
            ApiResponse(
                clientMessage = "조회되었습니다.",
                serverMessage = "Success",
                data = adjacentStationsResponses
            )
        )
    }

    @Operation(
        summary = "실시간 열차 도착 정보 조회 API",
        method = "GET",
        description = """
*역의 실시간 열차 도착 정보를 조회합니다.*

### [Path Variable]

- **stationId**: 역 ID
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
                                                "updnLine": "0",
                                                "statnFid": "1001000123",
                                                "statnTid": "1001000125",
                                                "statnId": "1001000124",
                                                "btrainSttus": "일반",
                                                "btrainNo": "1234",
                                                "arvlCd": "2",
                                                "lstcarAt": "0"
                                            }
                                        ]
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/{stationId}/trains")
    suspend fun getTrains(
        @Parameter(description = "역 ID", required = true)
        @PathVariable(value = "stationId") stationId: Long
    ): ResponseEntity<ApiResponse<List<TrainResponse>>> {
        val trainResponses = trainService.getTrains(stationId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            ApiResponse(
                clientMessage = "조회되었습니다.",
                serverMessage = "Success",
                data = trainResponses
            )
        )
    }
}
