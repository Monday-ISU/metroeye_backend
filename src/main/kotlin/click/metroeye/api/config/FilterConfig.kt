package click.metroeye.api.config

import click.metroeye.api.filter.ApiFilter
import click.metroeye.api.filter.LogFilter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.server.WebFilter

@Configuration
class FilterConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun logFilter(): WebFilter = LogFilter()

    @Bean
    fun apiFilter(
        @Value("\${app.min-version}") minVersion: String,
        @Value("\${app.maintenance}") isMaintenance: Boolean,
        objectMapper: ObjectMapper
    ): WebFilter = ApiFilter(minVersion, isMaintenance, objectMapper)
}