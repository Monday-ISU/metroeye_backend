package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Device
import click.metroeye.api.infrastructure.persistence.entity.DeviceEntity
import click.metroeye.api.infrastructure.persistence.repository.DeviceRepository
import org.springframework.stereotype.Repository

@Repository
class DeviceRepositoryAdapter(
    private val deviceRepository: DeviceRepository
) {
    suspend fun loadDevice(uuid: String): Device? {
        val loadedDeviceEntity = deviceRepository.findByUuid(uuid) ?: return null

        return Device.of(
            id = loadedDeviceEntity.id,
            uuid = loadedDeviceEntity.uuid,
            secret = loadedDeviceEntity.secret,
            osType = loadedDeviceEntity.osType,
            refreshToken = loadedDeviceEntity.refreshToken
        )
    }

    suspend fun saveDevice(device: Device): Device {
        val deviceEntity = DeviceEntity(
            id = device.id,
            uuid = device.uuid,
            secret = device.secret,
            osType = device.osType.name,
            refreshToken = device.refreshToken
        )

        val savedDeviceEntity = deviceRepository.save(deviceEntity)
        return Device.of(
            id = savedDeviceEntity.id,
            uuid = savedDeviceEntity.uuid,
            secret = savedDeviceEntity.secret,
            osType = savedDeviceEntity.osType,
            refreshToken = savedDeviceEntity.refreshToken
        )
    }
}