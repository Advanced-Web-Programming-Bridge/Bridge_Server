package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SignUpResponseDto {
    private UUID userIdx;
    private String content;

    public SignUpResponseDto(UUID userIdx) {
        String content = new Date() + "에 정상적으로 회원 가입 되었습니다.";
        this.content = content;
        this.userIdx = userIdx;
    }
}
