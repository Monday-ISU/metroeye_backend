package click.metroeye.api.application.dto

import click.metroeye.api.exception.ApiException
import org.springframework.http.HttpStatus

class RealtimeStationArrivalRequestModel(
    val stationName: String,
    val lineName: String
) {
    init {
        if (lineName.isBlank()) {
            throw ApiException(
                HttpStatus.BAD_REQUEST,
                "노선 이름이 누락되었습니다.",
                "RealtimeStationArrivalRequestModel.lineName is blank."
            )
        }
    }
}