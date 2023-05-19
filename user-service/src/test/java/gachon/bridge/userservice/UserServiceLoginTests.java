package gachon.bridge.userservice;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.*;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.service.UserService;
import gachon.bridge.userservice.utils.AES256Util;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static gachon.bridge.userservice.base.BaseErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceLoginTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AES256Util aes256Util;

    private final String id = "testId";
    private final String pw = "testPw";
    private final String email = "test@email.com";

    @Test
    void 로그인() {
        // given : 정상적인 데이터로
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        // when : 로그인을 요청하면
        LoginRequestDto request = new LoginRequestDto(id, pw);
        LoginResponseDto response = null;

        try {
            response = userService.signIn(request);
        } catch (BaseException e) {
            fail("로그인이 되어야 합니다.");
        }

        // then : access token과 refresh token을 발급받는다.
        assertNotNull(response.getToken().getAccessToken());
        assertNotNull(response.getToken().getRefreshToken());
    }

    @Test
    void 존재하지_않는_회원으로_로그인() {
        // given : 존재하지 않는 회원 정보로
        LoginRequestDto request = new LoginRequestDto("test", "test");

        // when : 로그인을 요청하면
        // then : 존재하지 않는 회원 예외가 발생한다.
        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("존재하지 않는 계정이 로그인되면 안 됩니다.");
    }

    @Test
    void 탈퇴한_계정으로_로그인() {
        // given : 탈퇴한 회원 정보로
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        user.setExpired(true);

        // when : 로그인을 요청하면
        // then : 존재하지 않는 회원 예외가 발생한다.
        LoginRequestDto request = new LoginRequestDto(id, pw);
        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("탈퇴한 계정이 로그인되면 안 됩니다.");
    }

    @Test
    void 다른_비밀번호로_로그인() {
        // given : 올바르지 않은 정보로 (비밀번호가 다른 정보로)
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호 암호화 실패");
        }

        userRepository.save(user);

        // when : 로그인을 요청하면
        // then : 올바르지 않은 비밀번호 예외가 발생해야 한다.
        LoginRequestDto request = new LoginRequestDto(user.getUserId(), "Invalid pw");
        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_PW, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 계정이 로그인되면 안 됩니다.");
    }

}