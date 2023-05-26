package gachon.bridge.mealservice.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    //400 : Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "요청 값을 확인해주세요"),
    BAD_DATE_FORMAT(HttpStatus.BAD_REQUEST.value(), "날짜 형식을 확인해 주세요 yy-MM-dd"),
    NO_TOKEN(HttpStatus.BAD_REQUEST.value(), "jwt 토큰이 없습니다."),
    EXIST_MEAL(HttpStatus.BAD_REQUEST.value(), "이미 식단 기록이 존재합니다."),
    NOT_EXIST_MEAL(HttpStatus.BAD_REQUEST.value(), "식단 기록이 존재하지 않습니다."),

    //404 Not Found
    NO_MEAL_EXIST(HttpStatus.NOT_FOUND.value(), "요청한 식단이 존재하지 않습니다."),

    //500 Internal Server
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 에러"),
    FEIGN_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Feign Client Error");
    private final Integer status;
    private final String message;
}
