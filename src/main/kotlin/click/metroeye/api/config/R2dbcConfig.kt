package click.metroeye.api.config

import click.metroeye.api.properties.R2dbcProperties
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.r2dbc.connection.R2dbcTransactionManager

@Configuration
@EnableConfigurationProperties(R2dbcProperties::class)
class R2dbcConfig {
    @Primary
    @Bean("masterConnectionFactory")
    fun masterConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory {
        val master = r2dbcProperties.master
        val options = ConnectionFactoryOptions
            .parse(master.url)
            .mutate()
            .option(ConnectionFactoryOptions.USER, master.username)
            .option(ConnectionFactoryOptions.PASSWORD, master.password)
            .build()

        val connectionFactory = ConnectionFactories.get(options)

        return if (master.pool.enabled) {
            val pool = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(master.pool.initialSize)
                .maxIdleTime(master.pool.maxIdleTime)
                .maxLifeTime(master.pool.maxLifeTime)
                .build()

            ConnectionPool(pool)
        } else {
            connectionFactory
        }
    }

    @Bean("slaveConnectionFactory")
    fun slaveConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory {
        val slave = r2dbcProperties.slave
        val options = ConnectionFactoryOptions
            .parse(slave.url)
            .mutate()
            .option(ConnectionFactoryOptions.USER, slave.username)
            .option(ConnectionFactoryOptions.PASSWORD, slave.password)
            .build()

        val connectionFactory = ConnectionFactories.get(options)

        return if (slave.pool.enabled) {
            val pool = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(slave.pool.initialSize)
                .maxIdleTime(slave.pool.maxIdleTime)
                .maxLifeTime(slave.pool.maxLifeTime)
                .build()

            ConnectionPool(pool)
        } else {
            connectionFactory
        }
    }

    @Bean
    fun transactionManager(
        @Qualifier("masterConnectionFactory") masterConnectionFactory: ConnectionFactory,
    ): R2dbcTransactionManager {
        return R2dbcTransactionManager(masterConnectionFactory)
    }
}