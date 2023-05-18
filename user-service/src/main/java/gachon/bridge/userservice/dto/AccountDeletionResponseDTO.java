package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDeletionResponseDTO {
    private String content;

    public AccountDeletionResponseDTO() {
        String content = new Date() + "에 정상적으로 회원 탈퇴가 되었습니다.";
        this.content = content;
    }
}