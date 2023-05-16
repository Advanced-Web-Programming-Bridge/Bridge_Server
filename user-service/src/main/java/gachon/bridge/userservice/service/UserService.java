package gachon.bridge.userservice.service;

import gachon.bridge.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Todo: User 정보 가져오기
    // Todo: 로그인
    // Todo: 회원 가입
    // Todo: 회원 탈퇴
}
