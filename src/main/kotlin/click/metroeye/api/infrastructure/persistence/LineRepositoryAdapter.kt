package click.metroeye.api.infrastructure.persistence

import click.metroeye.api.domain.Line
import click.metroeye.api.infrastructure.persistence.repository.LineRepository
import org.springframework.stereotype.Repository

@Repository
class LineRepositoryAdapter(
    private val lineRepository: LineRepository
) {
    suspend fun loadLines(): List<Line> {
        val loadedLineEntities = lineRepository.findAllByOrderByDisplayOrderAsc()

        return loadedLineEntities.map { loadedLineEntity ->
            Line.of(
                id = loadedLineEntity.id,
                name = loadedLineEntity.name,
                color = loadedLineEntity.color
            )
        }
    }
}