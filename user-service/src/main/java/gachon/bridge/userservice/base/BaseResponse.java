package gachon.bridge.userservice.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "data"})
public class BaseResponse<T> {

    private boolean isSuccess;
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public BaseResponse(T data) {
        this.isSuccess = true;
        this.code = 200;
        this.message = "요청 성공";
        this.data = data;
    }

    public BaseResponse(BaseException exception) {
        this.isSuccess = false;
        this.code = exception.errorCode.getStatus();
        this.message = exception.errorCode.getMessage();
    }
}