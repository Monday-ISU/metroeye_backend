package click.metroeye.api.presentation.v1.dto

data class RealtimeStationArrivalRequest(
    val line: String,
    val direction: String
)
