package click.metroeye.api.application.service

import click.metroeye.api.application.dto.IssueTokenRequestModel
import click.metroeye.api.constants.GrantType
import click.metroeye.api.domain.Device
import click.metroeye.api.exception.ApiException
import click.metroeye.api.infrastructure.crypto.token.JsonWebTokenAdapter
import click.metroeye.api.infrastructure.persistence.DeviceRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.IssueTokenResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class AuthService(
    private val deviceRepositoryAdapter: DeviceRepositoryAdapter,
    private val jsonWebTokenAdapter: JsonWebTokenAdapter,

    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {
    @Transactional
    fun issueToken(issueTokenRequestModel: IssueTokenRequestModel): Mono<IssueTokenResponse> {
        return when (issueTokenRequestModel.grantType) {
            GrantType.CLIENT_CREDENTIALS -> {
                val uuid = issueTokenRequestModel.uuid!!
                val secret = issueTokenRequestModel.secret!!

                deviceRepositoryAdapter.loadDevice(uuid)
                    .flatMap { loadedDevice ->
                        if (!loadedDevice.authenticate(secret)) {
                            return@flatMap Mono.error(
                                ApiException(
                                    HttpStatus.UNAUTHORIZED,
                                    "인증에 실패했습니다.",
                                    "Device secret does not match."
                                )
                            )
                        }

                        val accessToken = jsonWebTokenAdapter.generate(
                            loadedDevice.uuid,
                            mapOf("type" to "ACCESS"),
                            Device.ACCESS_TOKEN_EXPIRATION_SECONDS * 1000,
                            jwtSecret
                        )

                        val refreshToken = jsonWebTokenAdapter.generate(
                            loadedDevice.uuid,
                            mapOf("type" to "REFRESH"),
                            Device.REFRESH_TOKEN_EXPIRATION_SECONDS * 1000,
                            jwtSecret
                        )

                        loadedDevice.updateRefreshToken(refreshToken)

                        deviceRepositoryAdapter.saveDevice(loadedDevice)
                            .map { savedDevice ->
                                IssueTokenResponse(
                                    accessToken,
                                    refreshToken,
                                    Device.ACCESS_TOKEN_EXPIRATION_SECONDS
                                )
                            }
                    }
                    .switchIfEmpty(
                        Mono.error(
                            ApiException(
                                HttpStatus.UNAUTHORIZED,
                                "인증에 실패했습니다.",
                                "Device not found."
                            )
                        )
                    )
            }
            else -> {
                val refreshToken = issueTokenRequestModel.refreshToken!!

                val claims = try {
                    jsonWebTokenAdapter.parseClaims(refreshToken, jwtSecret)
                } catch (e: ExpiredJwtException) {
                    throw ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "인증에 실패했습니다.",
                        "Refresh token has expired."
                    )
                } catch (e: JwtException) {
                    throw ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "인증에 실패했습니다.",
                        "Refresh token is invalid."
                    )
                }

                val uuid = claims.subject ?: throw ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "인증에 실패했습니다.",
                    "Refresh token subject is missing."
                )

                deviceRepositoryAdapter.loadDevice(uuid)
                    .flatMap { loadedDevice ->
                        if (!loadedDevice.validateRefreshToken(refreshToken)) {
                            return@flatMap Mono.error(
                                ApiException(
                                    HttpStatus.UNAUTHORIZED,
                                    "인증에 실패했습니다.",
                                    "Refresh token does not match."
                                )
                            )
                        }

                        val accessToken = jsonWebTokenAdapter.generate(
                            uuid,
                            mapOf("type" to "ACCESS"),
                            Device.ACCESS_TOKEN_EXPIRATION_SECONDS * 1000,
                            jwtSecret
                        )

                        Mono.just(
                            IssueTokenResponse(
                                accessToken,
                                refreshToken,
                                Device.ACCESS_TOKEN_EXPIRATION_SECONDS
                            )
                        )
                    }
                    .switchIfEmpty(
                        Mono.error(
                            ApiException(
                                HttpStatus.UNAUTHORIZED,
                                "인증에 실패했습니다.",
                                "Device not found."
                            )
                        )
                    )
            }
        }
    }
}