package gachon.bridge.userservice.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    // 암호화, 복호화 에러
    ENCRYPTION_ERROR(500, "암호화하는 과정에서 에러가 발생하였습니다."),
    DECRYPTION_ERROR(500, "복호화하는 과정에서 에러가 발생하였습니다."),

    /**
     * 1000 ~ 1500
     * Auth Token Error
     */
    INVALID_JWT_TOKEN(1000, "권한이 없는 토큰입니다."),
    CORRUPTED_TOKEN(1001, "유효하지않은 토큰입니다."),
    EXPIRED_TOKEN(1002, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(1003, "지원하지 않는 토큰입니다."),
    TOKEN_NOT_EXIST(1004, "토큰이 존재하지 않습니다."),

    /**
     * 1501 ~ 1999
     * Auth Controller Error
     */

    // Id
    ID_TOO_SHORT(1500, "유저 아이디가 너무 짧습니다"),
    ID_TOO_LONG(1501, "유저 아이디가 너무 깁니다."),
    EXIST_ID(1502, "이미 존재하는 아이디 입니다."),

    // Password
    PW_TOO_SHORT(1503, "비밀번호가 너무 짧습니다."),
    PW_TOO_LONG(1504, "비밀번호가 너무 깁니다."),
    INVALID_PW(1505, "비밀번호가 일치 하지 않습니다."),

    // User
    INVALID_USER(1506, "존재하지 않는 회원입니다."),

    // Email
    INVALID_EMAIL(1507, "유효하지 않는 이메일입니다."),

    /**
     * 9000 Database Error
     */
    DATABASE_ERROR(9000, "Database Error");

    private final Integer status;
    private final String message;
}