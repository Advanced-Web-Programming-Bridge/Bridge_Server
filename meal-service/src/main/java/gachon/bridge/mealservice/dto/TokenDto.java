package gachon.bridge.mealservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
