package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SignUpResponseDto {
    private UUID userIdx;
    private Date time;

    public SignUpResponseDto(UUID userIdx) {
        this.userIdx = userIdx;
        this.time = new Date();
    }
}
