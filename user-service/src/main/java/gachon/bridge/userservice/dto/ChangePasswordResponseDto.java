package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ChangePasswordResponseDto {
    UUID userIdx;
    Date time;

    public ChangePasswordResponseDto(UUID userIdx) {
        this.userIdx = userIdx;
        this.time = new Date();
    }
}