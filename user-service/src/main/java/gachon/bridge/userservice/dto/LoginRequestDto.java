package gachon.bridge.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {
    private String id;
    private String pw;
}