package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.service.DeviceService
import click.metroeye.api.application.dto.CreateDeviceRequestModel
import click.metroeye.api.presentation.v1.dto.request.CreateDeviceRequest
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.CreateDeviceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/devices")
@Tag(name = "Device API", description = "기기 API")
class DeviceController(
    private val deviceService: DeviceService
) {
    @Operation(
        summary = "신규 기기 등록 및 인증 토큰 발급 API",
        method = "POST",
        description = "신규 기기를 등록하고 인증 토큰을 발급합니다."
    )
    @PostMapping
    fun createDevice(
        @RequestBody createDeviceRequest: CreateDeviceRequest
    ): Mono<ResponseEntity<ApiResponse<CreateDeviceResponse>>> {
        val createDeviceRequestModel = CreateDeviceRequestModel.of(createDeviceRequest.uuid, createDeviceRequest.osType)

        return deviceService.createDevice(createDeviceRequestModel)
            .map { createDeviceResponse ->
                ResponseEntity.ok(
                    ApiResponse(
                        "등록되었습니다.",
                        "SUCCESS",
                        createDeviceResponse
                    )
                )
            }
    }
}