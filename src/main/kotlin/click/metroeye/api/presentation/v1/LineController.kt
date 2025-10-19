package click.metroeye.api.presentation.v1

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/lines")
class LineController {
    @GetMapping()
    fun getLines(): ResponseEntity<Map<String, Any>> {
        var lines = listOf(
            mapOf("idx" to 1, "line_name" to "1호선"),
            mapOf("idx" to 2, "line_name" to "2호선"),
            mapOf("idx" to 3, "line_name" to "3호선"),
            mapOf("idx" to 4, "line_name" to "4호선")
        )
        return ResponseEntity.ok(mapOf(
            "status" to 200,
            "client_message" to "조회되었습니다.",
            "server_message" to "SUCCESS",
            "data" to lines
        ))
    }
}