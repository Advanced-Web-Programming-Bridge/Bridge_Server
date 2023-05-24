package gachon.bridge.exerciseservice.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    //400 : Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "요청 값을 확인해주세요"),
    EXIST_EXERCISE(HttpStatus.BAD_REQUEST.value(), "중복된 운동입니다."),
    BAD_DATE_FORMAT(HttpStatus.BAD_REQUEST.value(), "날짜 형식을 확인해 주세요 yy-MM-dd"),
    NO_TOKEN(HttpStatus.BAD_REQUEST.value(), "jwt 토큰이 없습니다."),
    NOT_EXIST_EXIST(HttpStatus.BAD_REQUEST.value(), "운동 기록이 존재하지 않습니다."),


    //404 not Found
    NO_EXERCISE_EXIST(HttpStatus.NOT_FOUND.value(), "요청한 운동이 존재하지 않습니다."),
    

    //500 Internal Server
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 에러");

    private final int code;
    private final String message;
}
