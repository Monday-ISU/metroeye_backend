package click.metroeye.api.application.dto

import click.metroeye.api.exception.ApiException

class RealtimeStationArrivalRequestModel(
    val station: String,
    val line: String
) {
    init {
        if (line.isBlank()) {
            throw ApiException(
                "노선 정보가 누락되었습니다.",
                "RealtimeStationArrivalRequestModel.line is blank."
            )
        }
    }
}