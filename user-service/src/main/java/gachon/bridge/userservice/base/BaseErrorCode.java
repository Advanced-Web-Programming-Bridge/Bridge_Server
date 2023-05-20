package gachon.bridge.userservice.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {
    // 400 Bad request
    INVALID_UUID_FORMAT(400, "올바르지 않은 UUID 형식입니다."),
    ID_TOO_SHORT(400, "유저 아이디가 너무 짧습니다"),
    ID_TOO_LONG(400, "유저 아이디가 너무 깁니다."),

    PW_TOO_SHORT(400, "비밀번호가 너무 짧습니다."),
    PW_TOO_LONG(400, "비밀번호가 너무 깁니다."),
    SAME_PW(400, "이전 비밀번호와 같습니다."),
    INVALID_PW(400, "비밀번호가 일치 하지 않습니다."),
    INVALID_INFORMATION(400, "올바르지 않은 정보입니다."),
    INVALID_EMAIL(400, "유효하지 않는 이메일입니다."),

    // 401 Unauthorized
    CORRUPTED_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(401, "지원하지 않는 토큰입니다."),
    TOKEN_NOT_EXIST(401, "토큰이 존재하지 않습니다."),

    // 403 Forbidden
    INVALID_JWT_TOKEN(403, "권한이 없는 토큰입니다."),

    // 404 Not Found
    INVALID_USER(404, "존재하지 않는 회원입니다."),

    // 409 Conflict
    EXIST_ID(409, "이미 존재하는 아이디 입니다."),

    // 500 Internal Server Error
    ENCRYPTION_ERROR(500, "암호화하는 과정에서 에러가 발생하였습니다."),
    DECRYPTION_ERROR(500, "복호화하는 과정에서 에러가 발생하였습니다."),
    DATABASE_ERROR(500, "Database Error"),
    EMAIL_SEND_ERROR(500, "이메일을 보내는 과정에서 에러가 발생하였습니다.");

    private final Integer status;
    private final String message;
}