package gachon.bridge.userservice.service;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static gachon.bridge.userservice.base.BaseErrorCode.INVALID_USER;

@Service
public class UserService {

    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 해당 id를 가진 User 정보 가져오기
     *
     * @param id : user id
     * @return user : 해당 id를 가진 User
     * @throws BaseException
     */
    public User getUserByUserId(String id) throws BaseException {
        User user;

        try {
            user = userRepository.findByUserId(id)
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            if (user.getExpired()) new BaseException(INVALID_USER);

            return user;

        } catch (BaseException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    // Todo: 로그인
    // Todo: 회원 가입
    // Todo: 비밀번호 변경
    // Todo: 회원 탈퇴
}
