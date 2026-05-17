package click.metroeye.api.application.service

import click.metroeye.api.infrastructure.persistence.LineRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.LineResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LineService(
    private val lineRepositoryAdapter: LineRepositoryAdapter
) {
    @Transactional(readOnly = true)
    suspend fun getLines(): List<LineResponse> {
        val loadedLines = lineRepositoryAdapter.loadLines()

        return loadedLines.map { loadedLine ->
            LineResponse(
                lineId = loadedLine.id,
                lineName = loadedLine.name,
                color = loadedLine.color
            )
        }
    }
}