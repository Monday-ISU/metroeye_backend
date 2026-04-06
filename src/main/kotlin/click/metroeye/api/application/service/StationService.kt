package click.metroeye.api.application.service

import click.metroeye.api.domain.Station
import click.metroeye.api.infrastructure.persistence.StationQueryRepository
import click.metroeye.api.presentation.v1.dto.response.StationResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class StationService(
    private val stationQueryRepository: StationQueryRepository
) {
    companion object {
        private const val LINE_SUFFIX = "호선"
    }

    @Transactional(readOnly = true)
    fun getStations(line: String?): Mono<List<StationResponse>> {
        val trimmedLine = line?.trim().orEmpty()

        return if (trimmedLine.isNotEmpty()) {
            getStationsByLine(trimmedLine)
        } else {
            stationQueryRepository.loadStations()
                .map { it.toResponse() }
                .collectList()
        }
    }

    private fun getStationsByLine(lineInput: String): Mono<List<StationResponse>> {
        val lineKeys = buildLineKeyCandidates(lineInput)
        val lineId = lineInput.toLongOrNull()

        val stationsById = if (lineId != null) {
            stationQueryRepository.loadStationsByLineId(lineId)
        } else {
            Flux.empty()
        }

        return stationsById.collectList()
            .flatMap { stations ->
                if (stations.isNotEmpty()) {
                    Mono.just(stations)
                } else {
                    loadStationsByLineKeys(lineKeys).collectList()
                }
            }
            .map { stations -> stations.map { it.toResponse() } }
    }

    private fun loadStationsByLineKeys(lineKeys: List<String>): Flux<Station> {
        if (lineKeys.isEmpty()) {
            return Flux.empty()
        }

        return Flux.fromIterable(lineKeys)
            .concatMap { lineKey -> stationQueryRepository.loadStationsByLineKey(lineKey) }
            .distinct { "${it.lineId}:${it.stationCode}" }
    }

    private fun buildLineKeyCandidates(lineInput: String): List<String> {
        val trimmedLine = lineInput.trim()
        if (trimmedLine.isEmpty()) {
            return emptyList()
        }

        val candidates = LinkedHashSet<String>()
        candidates.add(trimmedLine)

        val withoutSuffix = trimmedLine.removeSuffix(LINE_SUFFIX)
        if (withoutSuffix != trimmedLine) {
            candidates.add(withoutSuffix)
        }

        if (withoutSuffix.all { it.isDigit() }) {
            val padded = withoutSuffix.padStart(2, '0')
            candidates.add(padded)
            candidates.add("${withoutSuffix}$LINE_SUFFIX")
            candidates.add("$padded$LINE_SUFFIX")
            if (withoutSuffix.length <= 2) {
                candidates.add("10$padded")
            }
        }

        return candidates.toList()
    }

    private fun Station.toResponse(): StationResponse {
        return StationResponse(
            name = name,
            stationCode = stationCode,
            lineId = lineId,
            lineName = lineName
        )
    }
}
