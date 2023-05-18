package gachon.bridge.userservice;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.AccountDeletionRequestDTO;
import gachon.bridge.userservice.dto.LoginRequestDto;
import gachon.bridge.userservice.dto.LoginResponseDto;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.service.UserService;
import gachon.bridge.userservice.utils.AES256Util;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static gachon.bridge.userservice.base.BaseErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AES256Util aes256Util;

    private final String id = "testId";
    private final String pw = "testPw";
    private final String email = "test@email.com";

    // Todo : 회원가입 method 구현 이후 삭제
//    @Test
//    @Commit
//    void 데이터_추가용() throws Exception {
//        // DB 안에 있는 모든 내용을 지우고
//        List<User> all = userRepository.findAll();
//
//        for (User user : all) {
//            userRepository.delete(user);
//        }
//
//        // 데이터를 추가한다
//        User user = new User("testId", aes256Util.encrypt("testPw"), "test@email.com");
//        userRepository.save(user);
//    }

    // --------------- Get user method  ---------------
    @Test
    void 유저_정보_가져오기() throws BaseException {
        // given
        User user = new User(id, pw, email);
        userRepository.save(user);

        // when
//        UserDto findUser = userService.getUserByUserId("testId");
        User findUser = userService.getUser(user.getUserIdx());

        // then
        assertEquals(findUser.getUserId(), user.getUserId());
        assertEquals(findUser.getEmail(), user.getEmail());
    }

    @Test
    void 존재하지_않는_유저_정보_가져오기() {
        // given, when, then
        try {
//            userService.getUserByUserId("testId");
            userService.getUser(UUID.randomUUID());
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("존재하지 않는 회원의 정보를 조회하면 안 됩니다.");
    }

    @Test
    void 회원_탈퇴_한_유저_정보_가져오기() {
        // given
        User user = new User(id, pw, email);
        userRepository.save(user);

        user.setExpired(true);

        // when, then
        try {
//            userService.getUserByUserId("testId");
            userService.getUser(user.getUserIdx());
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("탈퇴한 회원의 정보를 조회하면 안 됩니다.");
    }

    // --------------- 로그인 ---------------

    @Test
    void 로그인() {
        // given
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호 암호화 실패");
        }

        userRepository.save(user);

        // when
        LoginRequestDto request = new LoginRequestDto(id, pw);
        LoginResponseDto response = null;

        try {
            response = userService.signIn(request);
        } catch (BaseException e) {
            fail();
        }

        // then
        assertEquals(user.getUserIdx(), response.getUserIdx());
        assertNotNull(response.getToken().getAccessToken());
        assertNotNull(response.getToken().getRefreshToken());
    }

    @Test
    void 존재하지_않는_회원으로_로그인() {
        // given, when, then
        LoginRequestDto request = new LoginRequestDto("test", "test");

        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("존재하지 않는 계정이 로그인되면 안 됩니다.");
    }

    @Test
    void 회원_탈퇴한_계정으로_로그인() {
        // given
        User user = new User(id, pw, email);
        userRepository.save(user);

        user.setExpired(true);

        // when, then
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
    void 올바르지_않은_계정으로_로그인() {
        // given
        User user = null;
        try {
            user = new User(id, aes256Util.encrypt(pw), email);
        } catch (Exception e) {
            fail("비밀번호 암호화 실패");
        }

        userRepository.save(user);

        // when, then
        LoginRequestDto request = new LoginRequestDto(user.getUserId(), "Invalid pw");

        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_PW, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 계정이 로그인되면 안 됩니다.");
    }

    // --------------- 회원 탈퇴 ---------------
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

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(user.getUserIdx(), pw);

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
    void 올바르지_않은_비밀번호로_회원탈퇴() {
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

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(user.getUserIdx(), "invalid_pw");

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

    @Test
    void 올바르지_않은_정보로_회원탈퇴() {
        // given : 토큰에 들어있는 user index와, dto로 받은 user index가 서로 다른 경우
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

        AccountDeletionRequestDTO dto = new AccountDeletionRequestDTO(UUID.randomUUID(), "invalid_pw");

        // when : 회원 탈퇴를 하면
        // then : Invalid user exception이 터지며 회원 탈퇴가 되면 안 된다
        try {
            userService.deactivateAccount(token, dto);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("올바르지 않은 정보로 회원 탈퇴가 되면 안 됩니다.");
    }

}