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
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "인증 헤더 누락",
                                description = "인증(Authorization) 헤더에 액세스 토큰(accessToken)을 포함하지 않은 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증 정보가 필요합니다.",
                                        "serverMessage": "Authorization header is missing.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "엑세스 토큰 만료",
                                description = "만료된 엑세스 토큰(accessToken)으로 요청할 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Access token has expired.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "엑세스 토큰 형식 오류",
                                description = "엑세스 토큰(accessToken) 형식이 유효하지 않을 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Access token is invalid.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "엑세스 토큰 UUID 누락",
                                description = "엑세스 토큰(accessToken)에서 기기 고유 번호(uuid)를 확인할 수 없는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Access token subject is missing.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "엑세스 토큰 타입 누락",
                                description = "엑세스 토큰(accessToken)에서 타입(type)를 확인할 수 없는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Token type is missing.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "엑세스 토큰 타입 불일치",
                                description = "액세스 토큰(accessToken)이 아닌 다른 타입의 토큰으로 인증 요청한 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Token is not an access token.",
                                        "data": null
                                    }
                                """
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
                serverMessage = "SUCCESS",
                data = lineResponses
            )
        )
    }
}