package click.metroeye.api.filter.decorator

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import reactor.core.publisher.Flux

class CachedContentServerHttpRequest(
    delegate: ServerHttpRequest
) : ServerHttpRequestDecorator(delegate) {
    companion object {
        private const val MAX_CONTENT_LENGTH = 1024
    }

    private var cachedContent: String = ""
    private var totalByteCount: Int = 0
    private var isLoggable: Boolean = true

    override fun getBody(): Flux<DataBuffer> {
        if (headers.contentType?.includes(MediaType.APPLICATION_JSON) != true) {
            return super.getBody()
        }

        val stringBuffer = StringBuffer()

        return super.getBody()
            .map { buffer ->
                if (!isLoggable) {
                    return@map buffer
                }

                val byteCount = buffer.readableByteCount()

                if (totalByteCount + byteCount > MAX_CONTENT_LENGTH) {
                    isLoggable = false
                    cachedContent = ""
                    return@map buffer
                }

                totalByteCount += byteCount
                val bytes = ByteArray(byteCount)
                buffer.read(bytes)
                stringBuffer.append(String(bytes, Charsets.UTF_8))
                cachedContent = stringBuffer.toString()
                buffer.factory().wrap(bytes)
            }
    }

    fun getCachedContent(): String = cachedContent
}