package gachon.bridge.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    private String accessToken;
    private String refreshToken;
}