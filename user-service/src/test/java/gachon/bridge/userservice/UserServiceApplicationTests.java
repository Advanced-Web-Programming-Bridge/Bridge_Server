package gachon.bridge.userservice;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.LoginRequestDto;
import gachon.bridge.userservice.dto.LoginResponseDto;
import gachon.bridge.userservice.dto.UserDto;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import static gachon.bridge.userservice.base.BaseErrorCode.INVALID_USER;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Commit
    void 데이터_추가용() {
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);
    }

    // --------------- Get user method  ---------------
    @Test
    void 유저_정보_가져오기() throws BaseException {
        // given
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);

        // when
        UserDto findUser = userService.getUserByUserId("testId");

        // then
        assertEquals(findUser.getUserId(), user.getUserId());
        assertEquals(findUser.getEmail(), user.getEmail());
    }

    @Test
    void 존재하지_않는_유저_정보_가져오기() {
        // given, when, then
        try {
            userService.getUserByUserId("testId");
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
        }
    }

    @Test
    void 회원_탈퇴_한_유저_정보_가져오기() {
        // given
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);

        user.setExpired(true);

        // when, then
        try {
            userService.getUserByUserId("testId");
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
        }
    }

    // --------------- 로그인 ---------------

    @Test
    void 로그인() throws Exception {
        // given
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);

        // when
        LoginRequestDto request = new LoginRequestDto(user.getUserId(), user.getPw());
        LoginResponseDto response;

        try {
            response = userService.signIn(request);
        } catch (BaseException e) {
            throw e;
        }

        // then
        assertEquals(user.getUserIdx(), response.getUserIdx());
        assertNotNull(response.getToken().getAccessToken());
        assertNotNull(response.getToken().getRefreshToken());
    }

    @Test
    void 존재하지_않는_회원으로_로그인() {
        // given
        // when, then
        LoginRequestDto request = new LoginRequestDto("test", "test");

        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
        }
    }

    @Test
    void 회원_탈퇴한_계정으로_로그인() {
        // given
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);

        user.setExpired(true);

        // when, then
        LoginRequestDto request = new LoginRequestDto(user.getUserId(), user.getPw());

        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
        }
    }

    @Test
    void 올바르지_않은_계정으로_로그인() {
        // given
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);

        user.setPw("testtestpw");

        // when, then
        LoginRequestDto request = new LoginRequestDto(user.getUserId(), user.getPw());

        try {
            userService.signIn(request);
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
        }
    }

}