package gachon.bridge.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    UUID userIdx;
    Token token;
    Date time;

    public LoginResponseDto(UUID userIdx, Token token) {
        this.userIdx = userIdx;
        this.token = token;
        this.time = new Date();
    }
}
