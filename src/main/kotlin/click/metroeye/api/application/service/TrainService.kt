package click.metroeye.api.application.service

import click.metroeye.api.constants.ErrorCode
import click.metroeye.api.exception.InvalidStationException
import click.metroeye.api.infrastructure.external.seoul.SeoulSubwayClientAdapter
import click.metroeye.api.infrastructure.persistence.LineRepositoryAdapter
import click.metroeye.api.infrastructure.persistence.StationRepositoryAdapter
import click.metroeye.api.presentation.v1.dto.response.TrainResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class TrainService(
    private val stationRepositoryAdapter: StationRepositoryAdapter,
    private val lineRepositoryAdapter: LineRepositoryAdapter,
    private val seoulSubwayClientAdapter: SeoulSubwayClientAdapter
) {
    suspend fun getTrains(stationId: Long, lineId: Long): List<TrainResponse> {
        val station = stationRepositoryAdapter.loadStationById(stationId)
            ?: throw InvalidStationException(ErrorCode.STATION_NOT_FOUND)

        if (!stationRepositoryAdapter.existsStationOnLine(stationId, lineId)) {
            throw InvalidStationException(ErrorCode.STATION_LINE_NOT_MATCH)
        }

        val line = lineRepositoryAdapter.loadLineById(lineId)
            ?: throw InvalidStationException(ErrorCode.STATION_NOT_FOUND)

        val (arrivalResponse, positionResponse) = coroutineScope {
            val arrivals = async {
                seoulSubwayClientAdapter
                    .getRealtimeArrivalsByStation(1, 1000, station.name)
                    .awaitSingleOrNull()
            }
            val positions = async {
                seoulSubwayClientAdapter
                    .getRealtimePositionsByLine(1, 1000, line.name)
                    .awaitSingleOrNull()
            }
            arrivals.await() to positions.await()
        }

        val arrivals = arrivalResponse?.data ?: return emptyList()
        val positionTrainNos = positionResponse?.data
            ?.mapNotNull { it.trainNo }
            ?.toSet()
            ?: emptySet()

        return arrivals
            .filter { it.btrainNo != null && it.btrainNo in positionTrainNos }
            .map { arrival ->
                TrainResponse(
                    subwayId = arrival.subwayId,
                    updnLine = arrival.updnLine,
                    statnFid = arrival.statnFid,
                    statnTid = arrival.statnTid,
                    statnId = arrival.statnId,
                    btrainNo = arrival.btrainNo,
                    btrainSttus = arrival.btrainSttus,
                    barvlDt = arrival.barvlDt,
                    bstatnNm = arrival.bstatnNm,
                    arvlMsg2 = arrival.arvlMsg2,
                    arvlMsg3 = arrival.arvlMsg3,
                    arvlCd = arrival.arvlCd,
                    lstcarAt = arrival.lstcarAt
                )
            }
    }
}
