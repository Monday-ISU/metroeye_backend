package click.metroeye.api.decorator

import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CachedContentServerHttpResponse(
    delegate: ServerHttpResponse
) : ServerHttpResponseDecorator(delegate) {
    companion object {
        private const val MAX_CONTENT_LENGTH = 1024
    }

    private val cachedContent = StringBuilder()
    private var totalByteCount = 0
    private var isLoggable = true

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void?> {
        val contentType = headers.contentType

        if (contentType?.includes(MediaType.APPLICATION_JSON) != true) {
            return super.writeWith(body)
        }

        val flux = Flux.from(body)
            .map { buffer ->
                if (!isLoggable) {
                    return@map buffer
                }

                val byteCount = buffer.readableByteCount()

                if (totalByteCount + byteCount > MAX_CONTENT_LENGTH) {
                    isLoggable = false
                    cachedContent.clear()
                    return@map buffer
                }

                totalByteCount += byteCount
                val bytes = ByteArray(byteCount)
                buffer.read(bytes)
                cachedContent.append(String(bytes, Charsets.UTF_8))

                buffer.factory().wrap(bytes)
            }

        return super.writeWith(flux)
    }

    fun getCachedContent(): String = cachedContent.toString()
}