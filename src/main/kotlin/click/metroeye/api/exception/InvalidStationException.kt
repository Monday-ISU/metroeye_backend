package click.metroeye.api.exception

import click.metroeye.api.constants.ErrorCode

class InvalidStationException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.serverMessage) {
    val clientMessage: String = errorCode.clientMessage
    val serverMessage: String = errorCode.serverMessage
}
