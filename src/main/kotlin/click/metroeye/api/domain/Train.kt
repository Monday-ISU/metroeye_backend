package click.metroeye.api.domain

import click.metroeye.api.constants.TranType

class Train(
    val trainId: Long,
    val destinationStationName: String,
    val trainType: TranType
) {
}