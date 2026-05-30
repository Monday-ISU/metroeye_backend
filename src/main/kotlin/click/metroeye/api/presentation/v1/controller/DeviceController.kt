package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.dto.CreateDeviceRequestModel
import click.metroeye.api.application.service.DeviceService
import click.metroeye.api.presentation.v1.dto.request.CreateDeviceRequest
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.CreateDeviceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/devices")
@Tag(name = "Device API", description = "기기 API")
class DeviceController(
    private val deviceService: DeviceService
) {
    @Operation(
        summary = "신규 기기 등록 및 인증 토큰 발급 API",
        method = "POST",
        description = """
*신규 기기를 등록하고 인증 토큰을 발급합니다.*

### [Request Body]

- **uuid**: 기기 고유 번호
- **osType**: OS 유형 (ANDROID, IOS)
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
                                        "clientMessage": "등록되었습니다.",
                                        "serverMessage": "SUCCESS",
                                        "data": {
                                            "secret": "generated-secret-key",
                                            "accessToken": "json-web-token",
                                            "refreshToken": "json-web-token",
                                            "expiresIn": 1800
                                        }
                                    }
                                """
                            ),
                        ]
                    )
                ]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "기기 고유 번호 누락",
                                description = "필수 요청 값인 기기 고유 번호(uuid)가 누락된 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "기기 고유 번호가 누락되었습니다.",
                                        "serverMessage": "CreateDeviceRequestModel.uuid is blank.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "지원하지 않는 OS 유형",
                                description = "요청한 OS 유형이 ANDROID 또는 IOS가 아닌 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "지원하지 않는 OS 유형입니다.",
                                        "serverMessage": "CreateDeviceRequestModel.osType is invalid.",
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
    @PostMapping
    suspend fun createDevice(
        @RequestBody createDeviceRequest: CreateDeviceRequest
    ): ResponseEntity<ApiResponse<CreateDeviceResponse>> {
        val createDeviceRequestModel = CreateDeviceRequestModel.of(createDeviceRequest.uuid, createDeviceRequest.osType)
        val createDeviceResponse = deviceService.createDevice(createDeviceRequestModel)

        return ResponseEntity.ok(
            ApiResponse(
                clientMessage = "등록되었습니다.",
                serverMessage = "Success",
                data = createDeviceResponse
            )
        )
    }
}