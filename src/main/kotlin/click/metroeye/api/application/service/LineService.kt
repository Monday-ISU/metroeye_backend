package click.metroeye.api.application.service

import click.metroeye.api.infrastructure.persistence.LineRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.LineResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class LineService(
    private val lineRepositoryAdapter: LineRepositoryAdapter
) {
    @Transactional(readOnly = true)
    fun getLines(): Mono<List<LineResponse>> {
        return lineRepositoryAdapter.loadLines()
            .map { loadedLines ->
                loadedLines.map { loadedLine ->
                    LineResponse(
                        loadedLine.idx,
                        loadedLine.name,
                        loadedLine.code,
                        loadedLine.color
                    )
                }
            }
    }
}