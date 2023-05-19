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

import java.util.UUID;

import static gachon.bridge.userservice.base.BaseErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceAccountDeletionTests {

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
    void 회원_탈퇴() {
        // given : 정상적인 회원과 토큰이 주어졌을 때
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        LoginRequestDto request = new LoginRequestDto(id, pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인 실패");
        }

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(user.getUserIdx().toString(), pw);

        // when : 회원 탈퇴를 하면
        try {
            userService.deactivateAccount(token, dto);
        } catch (Exception e) {
            e.printStackTrace();
            fail("회원 탈퇴 실패");
        }

        // then : 회원 조회를 했을 때 회원이 존재하지 않는 Exception이 터져야 한다.
        try {
            userService.getUser(user.getUserIdx());
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("탈퇴한 회원이 조회되면 안 됩니다.");
    }

    @Test
    void 존재하지_않는_회원_회원_탈퇴() {
        // given : 존재하지 않는 회원을
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        LoginRequestDto request = new LoginRequestDto(id, pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인 실패");
        }

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(user.getUserIdx().toString(), "invalid_pw");

        userRepository.delete(user); // 토큰 발급 후 user 삭제 -> 존재하지 않는 회원으로 만들어버림

        // when : 회원 탈퇴할 때
        // then : 존재하지 않는 회원 예외가 터져야 한다.
        try {
            userService.deactivateAccount(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 회원 탈퇴가 되면 안 됩니다.");
    }

    @Test
    void 탈퇴한_회원_회원_탈퇴() {
        // given : 탈퇴한 회원을
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        LoginRequestDto request = new LoginRequestDto(id, pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인 실패");
        }

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(user.getUserIdx().toString(), "invalid_pw");

        user.setExpired(true);

        // when : 회원 탈퇴할 때
        // then : 존재하지 않는 회원 예외가 터져야 한다.
        try {
            userService.deactivateAccount(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 회원 탈퇴가 되면 안 됩니다.");
    }

    @Test
    void 올바르지_않은_정보로_회원_탈퇴() {
        // given : 토큰에 들어있는 user index와, dto로 받은 user index가 서로 다른 경우
        User user = null;
        User anotherUser = null;

        try {
            user = new User(id, aes256Util.encrypt(pw), email);
            anotherUser = new User("another_user", aes256Util.encrypt("another_password"), "another_email");
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);
        userRepository.save(anotherUser);

        LoginRequestDto request = new LoginRequestDto(id, pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인 실패");
        }

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(anotherUser.getUserIdx().toString(), anotherUser.getPw());

        // when : 회원 탈퇴를 하면
        // then : 올바르지 않은 정보 예외가 터지며 회원 탈퇴가 되면 안 된다
        try {
            userService.deactivateAccount(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_INFORMATION, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 회원 탈퇴가 되면 안 됩니다.");
    }

    @Test
    void 올바르지_않은_비밀번호로_회원_탈퇴() {
        // given : 올바른 회원 id와 토큰, 올바르지 않은 비밀번호가 요청으로 왔을 때
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        LoginRequestDto request = new LoginRequestDto(id, pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인 실패");
        }

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(user.getUserIdx().toString(), "invalid_pw");

        // when : 회원 탈퇴를 하면
        // then : 회원 탈퇴가 되면 안 된다
        try {
            userService.deactivateAccount(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_PW, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 회원 탈퇴가 되면 안 됩니다.");
    }

}