package click.metroeye.api.config

import click.metroeye.api.filter.LogFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.WebFilter

@Configuration
class FilterConfig {
    @Bean
    fun logFilter(): WebFilter {
        return LogFilter()
    }
}