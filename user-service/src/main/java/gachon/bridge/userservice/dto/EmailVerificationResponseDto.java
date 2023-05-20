package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EmailVerificationResponseDto {
    private Date time;

    public EmailVerificationResponseDto() {
        this.time = new Date();
    }
}