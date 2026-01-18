package click.metroeye.api.util

import org.springframework.http.server.reactive.ServerHttpRequest

object RequestHeaderUtils {
    fun getStringHeaders(request: ServerHttpRequest): String {
        val headers = request.headers

        val headerMap = headers.entries.associate {
            (key, values) -> key to values.joinToString(",")
        }

        return headerMap.toString()
    }

    fun getClientIp(request: ServerHttpRequest): String {
        val headers = request.headers

        var clientIp: String? = headers.getFirst("X-Forwarded-For")
            ?: headers.getFirst("Proxy-Client-IP")
            ?: headers.getFirst("WL-Proxy-Client-IP")
            ?: headers.getFirst("HTTP_CLIENT_IP")
            ?: headers.getFirst("HTTP_X_FORWARDED_FOR")

        if (clientIp.isNullOrBlank()) {
            clientIp = request.remoteAddress?.address?.hostAddress
        }

        if (!clientIp.isNullOrBlank() && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0]
        }

        return clientIp ?: ""
    }
}