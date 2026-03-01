package click.metroeye.api.exception

import click.metroeye.api.constants.ErrorCode

class InvalidAuthException(
    val errorCode: ErrorCode,
    clientMessage: String? = null,
    serverMessage: String? = null
): RuntimeException(clientMessage ?: errorCode.clientMessage) {
    val clientMessage: String = clientMessage ?: errorCode.clientMessage
    val serverMessage: String = serverMessage ?: errorCode.serverMessage
}