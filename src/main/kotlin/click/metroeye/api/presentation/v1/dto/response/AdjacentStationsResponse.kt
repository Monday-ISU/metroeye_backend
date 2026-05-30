package click.metroeye.api.presentation.v1.dto.response

import click.metroeye.api.constants.DirectionType

data class AdjacentStationsResponse(
    val directionType: DirectionType,
    val directionIndex: Int,
    val stationCodes: List<String>
) {
}
