package gachon.bridge.userservice.dto;

import gachon.bridge.userservice.domain.User;
import lombok.Data;

@Data
public class UserDto {
    private String userId;
    private String pw;
    private String email;

    public UserDto(User user) {
        this.userId = user.getUserId();
        this.pw = user.getPw();
        this.email = user.getEmail();
    }
}
