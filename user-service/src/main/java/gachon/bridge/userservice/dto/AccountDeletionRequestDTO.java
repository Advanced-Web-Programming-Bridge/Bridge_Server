package gachon.bridge.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AccountDeletionRequestDTO {
    private UUID userIdx; // 토큰이 발행되었다는 것은 존재하는 유저 -> user index로 요청
    private String pw;
}
