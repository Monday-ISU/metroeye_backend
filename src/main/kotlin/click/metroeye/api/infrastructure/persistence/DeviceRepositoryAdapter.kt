package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Device
import click.metroeye.api.infrastructure.persistence.entity.DeviceEntity
import click.metroeye.api.infrastructure.persistence.repository.DeviceRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class DeviceRepositoryAdapter(
    private val deviceRepository: DeviceRepository
) {
    fun loadDevice(uuid: String): Mono<Device> {
        return deviceRepository.findByUuid(uuid)
            .map { loadedDeviceEntity ->
                Device.of(
                    loadedDeviceEntity.id,
                    loadedDeviceEntity.uuid,
                    loadedDeviceEntity.secret,
                    loadedDeviceEntity.osType,
                    loadedDeviceEntity.refreshToken
                )
            }
    }

    fun saveDevice(device: Device): Mono<Device> {
        val deviceEntity = DeviceEntity(
            device.id,
            device.uuid,
            device.secret,
            device.osType.name,
            device.refreshToken
        )

        return deviceRepository.save(deviceEntity)
            .map { savedDeviceEntity ->
                Device.of(
                    savedDeviceEntity.id,
                    savedDeviceEntity.uuid,
                    savedDeviceEntity.secret,
                    savedDeviceEntity.osType,
                    savedDeviceEntity.refreshToken
                )
            }
    }
}