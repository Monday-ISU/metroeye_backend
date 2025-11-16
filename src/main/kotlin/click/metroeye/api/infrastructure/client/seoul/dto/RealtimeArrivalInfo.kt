package click.metroeye.api.infrastructure.client.seoul.dto

data class RealtimeArrivalInfo(
    val subwayId: String,
    val updnLine: String,
    val trainLineNm: String,
    val statnFid: String,
    val statnTid: String,
    val statnId: String,
    val statnNm: String,
    val trnsitCo: String,
    val ordkey: String,
    val subwayList: String,
    val statnList: String,
    val btrainSttus: String,
    val barvlDt: String,
    val btrainNo: String,
    val bstatnId: String,
    val bstatnNm: String,
    val recptnDt: String,
    val arvlMsg2: String,
    val arvlMsg3: String,
    val arvlCd: String,
    val lstcarAt: String
)
