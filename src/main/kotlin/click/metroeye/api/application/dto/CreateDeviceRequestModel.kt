package click.metroeye.api.application.dto

import click.metroeye.api.constants.ErrorCode
import click.metroeye.api.constants.OsType
import click.metroeye.api.exception.InvalidDeviceException

class CreateDeviceRequestModel private constructor(
    val uuid: String,
    val osType: OsType
) {
    companion object {
        fun of(uuid: String, osType: String): CreateDeviceRequestModel {
            if (uuid.isBlank()) {
                throw InvalidDeviceException(
                    ErrorCode.REQUIRED_VALUE_MISSING,
                    "기기 고유 번호가 누락되었습니다.",
                    "CreateDeviceRequestModel.uuid is blank."
                )
            }

            try {
                OsType.validate(osType)
            } catch (e: IllegalArgumentException) {
                throw InvalidDeviceException(
                    ErrorCode.INVALID_REQUEST_FORMAT,
                    e.message ?: "지원하지 않는 OS 유형입니다.",
                    "CreateDeviceRequestModel.osType is invalid."
                )
            }

            return CreateDeviceRequestModel(uuid, OsType.valueOf(osType))
        }
    }
}