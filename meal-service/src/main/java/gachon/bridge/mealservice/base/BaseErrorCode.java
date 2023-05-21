package gachon.bridge.mealservice.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    //400 : Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "요청 값을 확인해주세요");

    private final Integer status;
    private final String message;
}
