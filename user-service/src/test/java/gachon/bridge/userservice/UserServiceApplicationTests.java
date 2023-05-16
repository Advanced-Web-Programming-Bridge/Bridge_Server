package gachon.bridge.userservice;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static gachon.bridge.userservice.base.BaseErrorCode.INVALID_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceApplicationTests {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    // --------------- Get user method  ---------------
    @Test
    void 유저_정보_가져오기() throws BaseException {
        // given
        User user = new User("testId", "testPw", "test@email.com");
        userRepository.save(user);

        // when
        User findUser = userService.getUserByUserId("testId");

        // then
        assertEquals(findUser, user);
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

}
