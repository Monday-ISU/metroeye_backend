package click.metroeye.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetroeyeApplication

fun main(args: Array<String>) {
	runApplication<MetroeyeApplication>(*args)
}
