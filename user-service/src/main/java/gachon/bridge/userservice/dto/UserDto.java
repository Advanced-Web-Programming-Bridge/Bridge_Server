package gachon.bridge.userservice.dto;

import gachon.bridge.userservice.domain.User;
import lombok.Data;

@Data
public class UserDto {
    private String userId;
    private String email;

    public UserDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
    }
}
