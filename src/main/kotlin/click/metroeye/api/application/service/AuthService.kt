package click.metroeye.api.application.service

import click.metroeye.api.application.dto.IssueTokenRequestModel
import click.metroeye.api.constants.ErrorCode
import click.metroeye.api.constants.GrantType
import click.metroeye.api.domain.Device
import click.metroeye.api.exception.InvalidAuthException
import click.metroeye.api.infrastructure.crypto.token.JsonWebTokenAdapter
import click.metroeye.api.infrastructure.persistence.DeviceRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.IssueTokenResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val deviceRepositoryAdapter: DeviceRepositoryAdapter,
    private val jsonWebTokenAdapter: JsonWebTokenAdapter,

    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {
    @Transactional
    suspend fun issueToken(issueTokenRequestModel: IssueTokenRequestModel): IssueTokenResponse {
        return when (issueTokenRequestModel.grantType) {
            GrantType.CLIENT_CREDENTIALS -> {
                val uuid = issueTokenRequestModel.uuid!!
                val secret = issueTokenRequestModel.secret!!

                val loadedDevice = deviceRepositoryAdapter.loadDevice(uuid)
                    ?: throw InvalidAuthException(
                        errorCode = ErrorCode.AUTHENTICATION_FAILED,
                        serverMessage = "Device not found."
                    )

                if (!loadedDevice.authenticate(secret)) {
                    throw InvalidAuthException(
                        errorCode = ErrorCode.AUTHENTICATION_FAILED,
                        serverMessage = "Device secret does not match."
                    )
                }

                val accessToken = jsonWebTokenAdapter.generate(
                    subject = uuid,
                    claims = mapOf("type" to "ACCESS"),
                    expirationMillis = Device.ACCESS_TOKEN_EXPIRATION_SECONDS * 1000,
                    secret = jwtSecret
                )
                val refreshToken = jsonWebTokenAdapter.generate(
                    subject = uuid,
                    claims = mapOf("type" to "REFRESH"),
                    expirationMillis = Device.REFRESH_TOKEN_EXPIRATION_SECONDS * 1000,
                    secret = jwtSecret
                )

                loadedDevice.updateRefreshToken(refreshToken)
                deviceRepositoryAdapter.saveDevice(loadedDevice)

                IssueTokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = Device.ACCESS_TOKEN_EXPIRATION_SECONDS
                )
            }
            else -> {
                val refreshToken = issueTokenRequestModel.refreshToken!!

                val claims = try {
                    jsonWebTokenAdapter.parseClaims(refreshToken, jwtSecret)
                } catch (e: ExpiredJwtException) {
                    throw InvalidAuthException(
                        errorCode = ErrorCode.EXPIRED_TOKEN,
                        serverMessage = "Refresh token has expired."
                    )
                } catch (e: JwtException) {
                    throw InvalidAuthException(
                        errorCode = ErrorCode.INVALID_TOKEN,
                        serverMessage = "Refresh token is invalid."
                    )
                }

                val uuid = claims.subject ?: throw InvalidAuthException(
                    errorCode = ErrorCode.INVALID_TOKEN,
                    serverMessage = "Refresh token subject is missing."
                )

                val tokenType = claims["type"] as? String ?: throw InvalidAuthException(
                    errorCode = ErrorCode.INVALID_TOKEN,
                    serverMessage = "Token type is missing."
                )

                if (tokenType != "REFRESH") {
                    throw InvalidAuthException(
                        errorCode = ErrorCode.INVALID_TOKEN,
                        serverMessage = "Token is not an refresh token."
                    )
                }

                val loadedDevice = deviceRepositoryAdapter.loadDevice(uuid)
                    ?: throw InvalidAuthException(
                        errorCode = ErrorCode.AUTHENTICATION_FAILED,
                        serverMessage = "Device not found."
                    )

                if (!loadedDevice.validateRefreshToken(refreshToken)) {
                    throw InvalidAuthException(
                        errorCode = ErrorCode.INVALID_TOKEN,
                        serverMessage = "Refresh token does not match."
                    )
                }

                val accessToken = jsonWebTokenAdapter.generate(
                    subject = uuid,
                    claims = mapOf("type" to "ACCESS"),
                    expirationMillis = Device.ACCESS_TOKEN_EXPIRATION_SECONDS * 1000,
                    secret = jwtSecret
                )

                IssueTokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = Device.ACCESS_TOKEN_EXPIRATION_SECONDS
                )
            }
        }
    }
}