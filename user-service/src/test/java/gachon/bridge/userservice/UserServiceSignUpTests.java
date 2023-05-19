package gachon.bridge.userservice;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.SignUpRequestDto;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static gachon.bridge.userservice.base.BaseErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceSignUpTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final String id = "testId";
    private final String pw = "testPw";
    private final String email = "test@email.com";

    // Todo: 유효한 이메일인지 확인하는 코드는 추후 작성
    // --------------- 회원 가입 ---------------

    @Test
    void 회원_가입() {
        // given : 정상적인 회원 가입 요청일 때 (서버에 없는 id이며 1 ~ 45자 사이의 id인 경우)
        SignUpRequestDto request = new SignUpRequestDto(id, pw, email);

        // when : 회원 가입을 하면
        // then : 정상적으로 DB에 저장되어야 한다.
        try {
            userService.join(request);
        } catch (BaseException e) {
            fail("정상적으로 회원 가입되어야 합니다.");
        }
    }

    @Test
    void 아이디_길이가_짧은_회원가입_요청() {
        // given : 길이가 짧은 id로
        String shortId = "";

        // when : 회원 가입을 하면
        // then : id 길이가 너무 짧다는 예외가 터져야 한다
        try {
            SignUpRequestDto request = new SignUpRequestDto(shortId, pw, email);
            userService.join(request);
        } catch (BaseException e) {
            assertEquals(ID_TOO_SHORT, e.getErrorCode());
            return;
        }

        fail("비정상적인 아이디는 회원 가입이 되면 안 됩니다.");
    }

    @Test
    void 아이디_길이가_긴_회원가입_요청() {
        // given : 길이가 긴 id로
        String longId = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

        // when : 회원 가입을 하면
        // then : id 길이가 너무 길다는 예외가 터져야 한다
        try {
            SignUpRequestDto request = new SignUpRequestDto(longId, pw, email);
            userService.join(request);
        } catch (BaseException e) {
            assertEquals(ID_TOO_LONG, e.getErrorCode());
            return;
        }

        fail("비정상적인 아이디는 회원 가입이 되면 안 됩니다.");
    }

    @Test
    void 중복_아이디로_회원가입_요청() {
        // given : 중복된 아이디로
        User user = new User(id, pw, email);
        userRepository.save(user);

        // when : 회원 가입을 하면
        // then : 이미 회원이 존재한다는 예외가 터져야 한다
        try {
            SignUpRequestDto request = new SignUpRequestDto(id, "another_pw", "another@email.com");
            userService.join(request);
        } catch (BaseException e) {
            assertEquals(EXIST_ID, e.getErrorCode());
            return;
        }

        fail("비정상적인 아이디는 회원 가입이 되면 안 됩니다.");
    }

    @Test
    void 회원_탈퇴한_아이디로_회원가입_요청() {
        // given : 중복된 아이디로
        User user = new User(id, pw, email);
        userRepository.save(user);

        user.setExpired(true);

        // when : 회원 가입을 하면
        // then : 이미 회원이 존재한다는 예외가 터져야 한다
        try {
            SignUpRequestDto request = new SignUpRequestDto(id, "another_pw", "another@email.com");
            userService.join(request);
        } catch (BaseException e) {
            assertEquals(EXIST_ID, e.getErrorCode());
            return;
        }

        fail("비정상적인 아이디는 회원 가입이 되면 안 됩니다.");
    }
}