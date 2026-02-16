package click.metroeye.api.application.service

import click.metroeye.api.application.dto.CreateDeviceRequestModel
import click.metroeye.api.domain.Device
import click.metroeye.api.infrastructure.crypto.secret.SecureRandomSecretAdapter
import click.metroeye.api.infrastructure.crypto.token.JsonWebTokenAdapter
import click.metroeye.api.infrastructure.persistence.DeviceRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.CreateDeviceResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class DeviceService(
    private val deviceRepositoryAdapter: DeviceRepositoryAdapter,
    private val secureRandomSecretAdapter: SecureRandomSecretAdapter,
    private val jsonWebTokenAdapter: JsonWebTokenAdapter,

    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {
    @Transactional
    fun createDevice(createDeviceRequestModel: CreateDeviceRequestModel): Mono<CreateDeviceResponse> {
        val uuid = createDeviceRequestModel.uuid

        return deviceRepositoryAdapter.loadDevice(uuid)
            .flatMap { loadedDevice ->
                val secret = secureRandomSecretAdapter.generate(32)
                loadedDevice.updateSecret(secret)
                issueTokens(loadedDevice)
            }
            .switchIfEmpty(
                Mono.defer {
                    val uuid = createDeviceRequestModel.uuid
                    val secret = secureRandomSecretAdapter.generate(32)
                    val osType = createDeviceRequestModel.osType
                    val device = Device.create(uuid, secret, osType)
                    issueTokens(device)
                }
            )
    }

    private fun issueTokens(device: Device): Mono<CreateDeviceResponse> {
        val accessToken = jsonWebTokenAdapter.generate(
            device.uuid,
            mapOf("type" to "ACCESS"),
            Device.ACCESS_TOKEN_EXPIRATION_SECONDS * 1000,
            jwtSecret
        )

        val refreshToken = jsonWebTokenAdapter.generate(
            device.uuid,
            mapOf("type" to "REFRESH"),
            Device.REFRESH_TOKEN_EXPIRATION_SECONDS * 1000,
            jwtSecret
        )

        device.updateRefreshToken(refreshToken)

        return deviceRepositoryAdapter.saveDevice(device)
            .map { savedDevice ->
                CreateDeviceResponse(
                    savedDevice.secret,
                    accessToken,
                    savedDevice.refreshToken!!,
                    Device.ACCESS_TOKEN_EXPIRATION_SECONDS
                )
            }
    }
}