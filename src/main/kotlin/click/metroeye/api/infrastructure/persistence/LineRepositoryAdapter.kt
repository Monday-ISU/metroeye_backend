package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Line
import click.metroeye.api.infrastructure.persistence.repository.LineRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class LineRepositoryAdapter(
    private val lineRepository: LineRepository
) {
    fun loadLines(): Mono<List<Line>> {
        return lineRepository.findAllByOrderByDisplayOrderAsc()
            .map { loadedLineEntity ->
                Line(
                    loadedLineEntity.idx,
                    loadedLineEntity.name,
                    loadedLineEntity.code,
                    loadedLineEntity.color
                )
            }
            .collectList()
    }
}