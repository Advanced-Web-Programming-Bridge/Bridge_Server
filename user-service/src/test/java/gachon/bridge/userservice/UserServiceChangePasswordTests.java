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
class UserServiceChangePasswordTests {

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
    void 비밀번호_변경() {
        // given : 정상적인 데이터를 가지고
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
            fail("로그인하는 과정에서 에러가 발생하였습니다.");
        }

        String newPw = "new_password";
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(user.getUserIdx().toString(), pw, newPw);

        // when : 비밀번호 변경 요청을 하면
        try {
            userService.changePassword(token, dto);
        } catch (Exception e) {
            fail("성공적으로 비밀번호가 바뀌어야 합니다.");
        }

        // then : 비밀번호가 변경된다
        User findUser = userRepository.findById(user.getUserIdx()).get();
        try {
            aes256Util.decrypt(findUser.getPw()).equals(newPw);
        } catch (Exception e) {
            fail("비밀번호를 복호화하는데 실패하였습니다.");
        }
    }

    @Test
    void 존재하지_않는_회원으로_비밀번호_변경() {
        // given : 존재하지 않는 회원 정보로
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호를 암호화하는데 실패하였습니다.");
        }

        userRepository.save(user);

        LoginRequestDto request = new LoginRequestDto(user.getUserId(), pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인하는 과정에서 에러가 발생하였습니다.");
        }

        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(user.getUserIdx().toString(), pw, "newPw");

        userRepository.delete(user); // 토큰 발급 후 user 삭제 -> 존재하지 않는 회원으로 만들어버림

        // when : 회원 가입을 요청하면
        // then : 존재하지 않는 회원 예외가 발생한다.
        try {
            userService.changePassword(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 비밀번호 변경이 되면 안 됩니다.");
    }

    @Test
    void 탈퇴한_계정으로_비밀번호_변경() {
        // given : 탈퇴한 회원 정보로
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
            fail("로그인하는 과정에서 에러가 발생하였습니다.");
        }

        user.setExpired(true);

        String newPw = "new_password";
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(user.getUserIdx().toString(), pw, newPw);

        // when : 비밀번호 변경 요청을 하면
        // then : 존재하지 않는 회원 예외가 발생한다.
        try {
            userService.changePassword(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("탈퇴한 회원의 비밀번호를 변경할 수 없습니다.");
    }

    @Test
    void 다른사람의_아이디로_비밀번호_변경() {
        // given : 올바르지 않은 정보로 (토큰에 저장된 나의 아이디와 요청으로 보내는 아이디가 다른 경우)
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

        LoginRequestDto request = new LoginRequestDto(user.getUserId(), pw);

        String token = "";
        try {
            token = userService.signIn(request).getToken().getAccessToken();
            token = "Bearer " + token;
        } catch (Exception e) {
            fail("로그인하는 과정에서 에러가 발생하였습니다.");
        }

        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(anotherUser.getUserIdx().toString(), pw, "newPw");

        // when : 비밀번호 변경 요청을 하면
        // then : 올바르지 않은 유저 정보 예외가 터져야 한다
        try {
            userService.changePassword(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_INFORMATION, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 비밀번호 변경이 되면 안 됩니다.");
    }

    @Test
    void 올바르지_않은_비밀번호로_비밀번호_변경() {
        // given : 올바르지 않은 정보로 (같은 아이디이나 비밀번호가 다른 정보로)
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
            fail("로그인하는 과정에서 에러가 발생하였습니다.");
        }

        String newPw = "new_password";
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(user.getUserIdx().toString(), newPw, newPw);

        // when : 비밀번호 변경 요청을 하면
        // then : 올바르지 않은 비밀번호 예외가 발생해야 한다.
        try {
            userService.changePassword(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_PW, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 비밀번호 변경이 되면 안 됩니다.");
    }

}