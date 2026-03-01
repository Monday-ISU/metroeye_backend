package click.metroeye.api.exception

import click.metroeye.api.constants.ErrorCode

class InvalidDeviceException(
    val errorCode: ErrorCode,
    clientMessage: String? = null,
    serverMessage: String? = null
): RuntimeException(serverMessage ?: errorCode.serverMessage) {
    val clientMessage: String = clientMessage ?: errorCode.clientMessage
    val serverMessage: String = serverMessage ?: errorCode.serverMessage
}