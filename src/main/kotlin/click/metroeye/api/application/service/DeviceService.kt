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

@Service
class DeviceService(
    private val deviceRepositoryAdapter: DeviceRepositoryAdapter,
    private val secureRandomSecretAdapter: SecureRandomSecretAdapter,
    private val jsonWebTokenAdapter: JsonWebTokenAdapter,

    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {
    @Transactional
    suspend fun createDevice(createDeviceRequestModel: CreateDeviceRequestModel): CreateDeviceResponse {
        val uuid = createDeviceRequestModel.uuid
        val secret = secureRandomSecretAdapter.generate(32)
        val accessToken = jsonWebTokenAdapter.generate(
            uuid,
            mapOf("type" to "ACCESS"),
            Device.ACCESS_TOKEN_EXPIRATION_SECONDS * 1000,
            jwtSecret
        )
        val refreshToken = jsonWebTokenAdapter.generate(
            uuid,
            mapOf("type" to "REFRESH"),
            Device.REFRESH_TOKEN_EXPIRATION_SECONDS * 1000,
            jwtSecret
        )

        val loadedDevice = deviceRepositoryAdapter.loadDevice(uuid)?.apply {
            updateSecret(secret)
            updateRefreshToken(refreshToken)
        } ?: Device.create(uuid, secret, createDeviceRequestModel.osType, refreshToken)
        val savedDevice = deviceRepositoryAdapter.saveDevice(loadedDevice)

        return CreateDeviceResponse(
            secret,
            accessToken,
            refreshToken,
            Device.ACCESS_TOKEN_EXPIRATION_SECONDS
        )
    }
}