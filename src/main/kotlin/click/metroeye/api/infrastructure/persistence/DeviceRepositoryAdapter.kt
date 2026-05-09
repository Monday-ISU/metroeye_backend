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
            loadedDeviceEntity.id,
            loadedDeviceEntity.uuid,
            loadedDeviceEntity.secret,
            loadedDeviceEntity.osType,
            loadedDeviceEntity.refreshToken
        )
    }

    suspend fun saveDevice(device: Device): Device {
        val deviceEntity = DeviceEntity(
            device.id,
            device.uuid,
            device.secret,
            device.osType.name,
            device.refreshToken
        )

        val savedDeviceEntity = deviceRepository.save(deviceEntity)
        return Device.of(
            savedDeviceEntity.id,
            savedDeviceEntity.uuid,
            savedDeviceEntity.secret,
            savedDeviceEntity.osType,
            savedDeviceEntity.refreshToken
        )
    }
}