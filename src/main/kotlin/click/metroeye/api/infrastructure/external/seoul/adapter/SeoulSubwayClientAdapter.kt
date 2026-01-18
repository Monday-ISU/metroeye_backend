package click.metroeye.api.infrastructure.external.seoul.adapter

import click.metroeye.api.infrastructure.http.adapter.WebClientAdapter
import click.metroeye.api.infrastructure.external.seoul.dto.RealtimeArrivalResponse
import click.metroeye.api.infrastructure.external.seoul.dto.RealtimePositionResponse
import click.metroeye.api.infrastructure.external.seoul.dto.SeoulSubwayApiResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SeoulSubwayClientAdapter(
    private val webClientAdapter: WebClientAdapter,
    private val objectMapper: ObjectMapper,

    @Value("\${external-api.seoul-public-data.subway.api-key}")
    private val apiKey: String
) {
    fun getRealtimeArrivalsByStation(
        startIndex: Int,
        endIndex: Int,
        station: String
    ): Mono<SeoulSubwayApiResponse<List<RealtimeArrivalResponse>>> {
        return webClientAdapter.get(
            "http://swopenapi.seoul.go.kr/api/subway/$apiKey/json/realtimeStationArrival/$startIndex/$endIndex/$station",
            requestParams = emptyMap(),
            responseType = object : ParameterizedTypeReference<String>() {}
        ).map { response ->
            val jsonNode = objectMapper.readTree(response)

            if (jsonNode.has("errorMessage") && jsonNode.has("realtimeArrivalList")) {
                val message = jsonNode.get("errorMessage").get("message").asText()
                val data = objectMapper.readValue(
                    jsonNode.get("realtimeArrivalList").toString(),
                    object : TypeReference<List<RealtimeArrivalResponse>>() {}
                )

                SeoulSubwayApiResponse(
                    true,
                    message,
                    data
                )
            } else {
                val message = jsonNode.get("message").asText()

                SeoulSubwayApiResponse(
                    false,
                    message,
                    null
                )
            }
        }
            .defaultIfEmpty(
                SeoulSubwayApiResponse(
                    false,
                    "외부 API로부터 빈 응답을 받았습니다.",
                    null
                )
            )
    }

    fun getRealtimePositionsByLine(
        startIndex: Int,
        endIndex: Int,
        line: String
    ): Mono<SeoulSubwayApiResponse<List<RealtimePositionResponse>>> {
        return webClientAdapter.get(
            "http://swopenapi.seoul.go.kr/api/subway/$apiKey/json/realtimePosition/$startIndex/$endIndex/$line",
            requestParams = emptyMap(),
            responseType = object : ParameterizedTypeReference<String>() {}
        ).map { response ->
            val jsonNode = objectMapper.readTree(response)

            if (jsonNode.has("errorMessage") && jsonNode.has("realtimePositionList")) {
                val message = jsonNode.get("errorMessage").get("message").asText()
                val data = objectMapper.readValue(
                    jsonNode.get("realtimePositionList").toString(),
                    object : TypeReference<List<RealtimePositionResponse>>() {}
                )

                SeoulSubwayApiResponse(
                    true,
                    message,
                    data
                )
            } else {
                val message = jsonNode.get("message").asText()

                SeoulSubwayApiResponse(
                    false,
                    message,
                    null
                )
            }
        }
            .defaultIfEmpty(
                SeoulSubwayApiResponse(
                    false,
                    "외부 API로부터 빈 응답을 받았습니다.",
                    null
                )
            )
    }
}