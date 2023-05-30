package gachon.bridge.userservice;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
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

import static gachon.bridge.userservice.base.BaseErrorCode.INVALID_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceLookUpTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AES256Util aes256Util;

    private final String id = "testId";
    private final String pw = "testPw";
    private final String email = "test@email.com";

    // --------------- Get user method  ---------------
    @Test
    void 유저_정보_가져오기() throws BaseException {
        // given : 정상적인 유저 정보를
        User user = new User(id, pw, email);
        userRepository.save(user);

        // when : 조회하는 요청이 오면
        User findUser = userService.getUser(user.getUserIdx());

        // then : 정상적으로 유저 정보를 받는다.
        assertEquals(findUser.getUserId(), user.getUserId());
        assertEquals(findUser.getEmail(), user.getEmail());
    }

    @Test
    void 존재하지_않는_유저_정보_가져오기() {
        // given : 존재하지 않는 유저의 정보를
        // when : 조회하는 요청이 오면
        // then : 존재하지 않는 유저 예외가 터진다.
        try {
            userService.getUser(UUID.randomUUID());
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("존재하지 않는 회원의 정보를 조회하면 안 됩니다.");
    }

    @Test
    void 회원_탈퇴_한_유저_정보_가져오기() {
        // given : 회원 탈퇴한 유저의 정보를
        User user = new User(id, pw, email);
        userRepository.save(user);

        user.setExpired(true);

        // when : 조회하는 요청이 오면
        // then : 존재하지 않는 유저 예외가 터진다.
        try {
            userService.getUser(user.getUserIdx());
        } catch (BaseException e) {
            assertEquals(INVALID_USER, e.getErrorCode());
            return;
        }

        fail("탈퇴한 회원의 정보를 조회하면 안 됩니다.");
    }
}