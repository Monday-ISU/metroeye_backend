package click.metroeye.api.filter.decorator

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import reactor.core.publisher.Flux

class CachedContentServerHttpRequest(
    delegate: ServerHttpRequest,
    private val requestBodyBytes: ByteArray
) : ServerHttpRequestDecorator(delegate) {
    override fun getBody(): Flux<DataBuffer> {
        if (requestBodyBytes.isEmpty()) return super.getBody()
        return Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(requestBodyBytes))
    }

    fun bodyAsString(): String = String(requestBodyBytes, Charsets.UTF_8)
}