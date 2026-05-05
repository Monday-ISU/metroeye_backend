package click.metroeye.api.util

import org.springframework.http.HttpHeaders

fun HttpHeaders.toHeadersString(): String = buildString {
    forEach { key, values ->
        append("$key: ${values.joinToString(", ")}, ")
    }
}.trimEnd(',', ' ')

fun HttpHeaders.getClientIp(): String =
    (getFirst("X-Forwarded-For")
        ?: getFirst("Proxy-Client-Ip")
        ?: getFirst("WL-Proxy-Client-Ip")
        ?: getFirst("Http-Client-Ip")
        ?: getFirst("Http-X-Forwarded-For")
        ?: "").split(",")[0].trim()