package click.metroeye.api.infrastructure.persistence.repository

import click.metroeye.api.infrastructure.persistence.entity.LineEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface LineRepository: CoroutineCrudRepository<LineEntity, Long> {
    suspend fun findAllByOrderByDisplayOrderAsc(): List<LineEntity>
}