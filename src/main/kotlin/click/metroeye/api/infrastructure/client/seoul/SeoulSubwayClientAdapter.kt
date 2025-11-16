package click.metroeye.api.infrastructure.client.seoul

import click.metroeye.api.infrastructure.client.common.WebClientAdapter
import click.metroeye.api.infrastructure.client.seoul.dto.RealtimeArrivalInfo
import click.metroeye.api.infrastructure.client.seoul.dto.SeoulSubwayApiResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

@Component
class SeoulSubwayClientAdapter(
    private val webClientAdapter: WebClientAdapter,
    private val objectMapper: ObjectMapper,

    @Value("\${external-api.seoul-public-data.subway.api-key}")
    private val apiKey: String
) {
    fun getArrivalsByStation(startIndex: Int, endIndex: Int, stationName: String): SeoulSubwayApiResponse<List<RealtimeArrivalInfo>> {
        val response: String? = webClientAdapter.get(
            "http://swopenapi.seoul.go.kr/api/subway/$apiKey/json/realtimeStationArrival/$startIndex/$endIndex/$stationName",
            requestParams = emptyMap(),
            responseType = object : ParameterizedTypeReference<String>() {}
        )

        if(StringUtils.hasText(response)) {
            val jsonNode = objectMapper.readTree(response)

            if(jsonNode.has("errorMessage") && jsonNode.has("realtimeArrivalList")) {
                val message = jsonNode.get("errorMessage").get("message").asText()
                val data = objectMapper.readValue(
                    jsonNode.get("realtimeArrivalList").toString(),
                    object : TypeReference<List<RealtimeArrivalInfo>>() {}
                )

                return SeoulSubwayApiResponse(
                    true,
                    message,
                    data
                )
            } else {
                val message = jsonNode.get("message").asText()

                return SeoulSubwayApiResponse(
                    false,
                    message,
                    null
                )
            }
        }

        return SeoulSubwayApiResponse(
            false,
            "외부 API로부터 빈 응답을 받았습니다.",
            null
        )
    }
}