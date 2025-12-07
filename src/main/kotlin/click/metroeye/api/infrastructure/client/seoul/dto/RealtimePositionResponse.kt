package click.metroeye.api.infrastructure.client.seoul.dto

data class RealtimePositionResponse(
    val beginRow: Int?,
    val endRow: Int?,
    val curPage: Int?,
    val pageRow: Int?,
    val totalCount: Int?,
    val rowNum: Int?,
    val selectedCount: Int?,
    val subwayId: String?,
    val subwayNm: String?,
    val statnId: String?,
    val statnNm: String?,
    val trainNo: String?,
    val lastRecptnDt: String?,
    val recptnDt: String?,
    val updnLine: String?,
    val statnTid: String?,
    val statnTnm: String?,
    val trainSttus: String?,
    val directAt: String?,
    val lstcarAt: String?
)