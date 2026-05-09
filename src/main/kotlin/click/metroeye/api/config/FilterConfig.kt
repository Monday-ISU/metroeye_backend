package click.metroeye.api.config

import click.metroeye.api.filter.LogFilter
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
}