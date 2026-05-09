package click.metroeye.api.infrastructure.persistence.repository

import click.metroeye.api.infrastructure.persistence.entity.DeviceEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface DeviceRepository: CoroutineCrudRepository<DeviceEntity, Long> {
    suspend fun findByUuid(uuid: String): DeviceEntity?
}