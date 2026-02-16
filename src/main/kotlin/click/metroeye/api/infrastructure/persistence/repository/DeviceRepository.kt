package click.metroeye.api.infrastructure.persistence.repository

import click.metroeye.api.infrastructure.persistence.entity.DeviceEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface DeviceRepository: ReactiveCrudRepository<DeviceEntity, Long> {
    fun findByUuid(uuid: String): Mono<DeviceEntity>
}