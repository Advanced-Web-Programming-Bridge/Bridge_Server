package gachon.bridge.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequestDto {
    private String id;
    private String pw;
    private String email;
}
