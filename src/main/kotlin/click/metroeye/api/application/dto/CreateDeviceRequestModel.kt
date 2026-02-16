package click.metroeye.api.application.dto

import click.metroeye.api.constants.OsType
import click.metroeye.api.exception.ApiException
import org.springframework.http.HttpStatus

class CreateDeviceRequestModel private constructor(
    val uuid: String,
    val osType: OsType
) {
    companion object {
        fun of(uuid: String, osType: String): CreateDeviceRequestModel {
            if (uuid.isBlank()) {
                throw ApiException(
                    HttpStatus.BAD_REQUEST,
                    "기기 고유 번호가 누락되었습니다.",
                    "CreateDeviceRequestModel.uuid is blank."
                )
            }

            try {
                OsType.validate(osType)
            } catch (e: IllegalArgumentException) {
                throw ApiException(
                    HttpStatus.BAD_REQUEST,
                    e.message ?: "지원하지 않는 OS 유형입니다.",
                    "CreateDeviceRequestModel.osType is invalid."
                )
            }

            return CreateDeviceRequestModel(uuid, OsType.valueOf(osType))
        }
    }
}