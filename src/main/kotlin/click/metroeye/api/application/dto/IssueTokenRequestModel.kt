package click.metroeye.api.application.dto

import click.metroeye.api.constants.ErrorCode
import click.metroeye.api.constants.GrantType
import click.metroeye.api.exception.InvalidAuthException

class IssueTokenRequestModel(
    val grantType: GrantType,
    val uuid: String?,
    val secret: String?,
    val refreshToken: String?,
) {
    companion object {
        fun of(grantType: String, uuid: String?, secret: String?, refreshToken: String?): IssueTokenRequestModel {
            try {
                GrantType.validate(grantType)
            } catch (e: IllegalArgumentException) {
                throw InvalidAuthException(
                    ErrorCode.INVALID_REQUEST_FORMAT,
                    e.message ?: "지원하지 않는 인증 유형입니다.",
                    "IssueTokenRequestModel.grantType is invalid."
                )
            }

            when (GrantType.valueOf(grantType)) {
                GrantType.CLIENT_CREDENTIALS -> {
                    if (uuid.isNullOrBlank() || secret.isNullOrBlank()) {
                        throw InvalidAuthException(
                            ErrorCode.REQUIRED_VALUE_MISSING,
                            "기기 재인증을 위한 정보(기기 고유 번호, 기기 비밀키)가 누락되었습니다.",
                            "IssueTokenRequestModel.uuid or IssueTokenRequestModel.secret is missing."
                        )
                    }
                }
                GrantType.REFRESH_TOKEN -> {
                    if (refreshToken.isNullOrBlank()) {
                        throw InvalidAuthException(
                            ErrorCode.REQUIRED_VALUE_MISSING,
                            "엑세스 토큰 갱신을 위한 레프레시 토큰이 누락되었습니다.",
                            "IssueTokenRequestModel.refreshToken is missing."
                        )
                    }
                }
            }

            return IssueTokenRequestModel(
                GrantType.valueOf(grantType),
                uuid,
                secret,
                refreshToken
            )
        }
    }
}