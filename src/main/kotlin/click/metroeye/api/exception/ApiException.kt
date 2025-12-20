package click.metroeye.api.exception

class ApiException(
    val clientMessage: String,
    val serverMessage: String
) : RuntimeException(serverMessage) {}