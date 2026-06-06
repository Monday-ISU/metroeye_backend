package click.metroeye.api.constants

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val clientMessage: String,
    val serverMessage: String,
    val status: HttpStatus
) {
    // 공통
    REQUIRED_VALUE_MISSING(
        "COMMON001",
        "필수 값이 누락되었습니다.",
        "Required value is missing.",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_REQUEST_FORMAT(
        "COMMON002",
        "요청 형식이 올바르지 않습니다.",
        "Invalid request format.",
        HttpStatus.BAD_REQUEST
    ),

    RESOURCE_NOT_FOUND(
        "COMMON003",
        "요청한 정보를 찾을 수 없습니다.",
        "Requested resource not found.",
        HttpStatus.NOT_FOUND
    ),

    INTERNAL_SERVER_ERROR(
        "COMMON999",
        "서버 오류가 발생했습니다.",
        "Unexpected internal server error.",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    // 인증
    AUTHENTICATION_REQUIRED(
        "AUTH001",
        "인증 정보가 필요합니다.",
        "Authorization header is missing.",
        HttpStatus.UNAUTHORIZED
    ),

    AUTHENTICATION_FAILED(
        "AUTH002",
        "인증에 실패했습니다.",
        "Authentication failed.",
        HttpStatus.UNAUTHORIZED
    ),

    INVALID_TOKEN(
        "AUTH003",
        "유효하지 않은 토큰입니다.",
        "Invalid token.",
        HttpStatus.UNAUTHORIZED
    ),

    EXPIRED_TOKEN(
        "AUTH004",
        "토큰이 만료되었습니다.",
        "Token has expired.",
        HttpStatus.UNAUTHORIZED
    )
}