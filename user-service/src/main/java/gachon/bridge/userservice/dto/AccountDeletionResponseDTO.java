package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDeletionResponseDTO {
    private Date time;

    public AccountDeletionResponseDTO() {
        this.time = new Date();
    }
}