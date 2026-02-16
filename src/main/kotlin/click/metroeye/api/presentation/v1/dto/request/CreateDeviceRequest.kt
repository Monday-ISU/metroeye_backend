package click.metroeye.api.presentation.v1.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class CreateDeviceRequest(
    @Schema(description = "기기 고유 번호")
    val uuid: String,
    @Schema(description = "OS 유형(ANDROID, IOS)")
    val osType: String
)