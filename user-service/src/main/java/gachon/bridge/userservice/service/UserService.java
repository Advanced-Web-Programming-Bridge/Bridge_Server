package gachon.bridge.userservice.service;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.*;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.utils.AES256Util;
import gachon.bridge.userservice.utils.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static gachon.bridge.userservice.base.BaseErrorCode.*;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AES256Util aes256Util;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, AES256Util aes256Util) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.aes256Util = aes256Util;
    }

    /**
     * 해당 id를 가진 User 정보 가져오기
     *
     * @param id : user id
     * @return user : 해당 id를 가진 User
     * @throws BaseException
     */
//    public UserDto getUserByUserId(String id) throws BaseException {
//        User user;
//
//        try {
//            user = userRepository.findByUserId(id)
//                    .orElseThrow(() -> new BaseException(INVALID_USER));
//
//            // 회원 탈퇴를 한 적이 있다면
//            if (user.getExpired()) throw new BaseException(INVALID_USER);
//
//            return new UserDto(user);
//
//        } catch (BaseException e) {
//            log.error(e.getErrorCode().getMessage());
//            throw e;
//        }
//    }

    /**
     * 해당 id를 가진 User 정보 가져오기
     *
     * @param id: user index
     * @return user : 해당 id를 가진 User
     * @throws BaseException
     */
    public User getUser(UUID id) throws BaseException {
        User user;

        try {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 회원 탈퇴를 한 적이 있다면
            if (user.getExpired()) throw new BaseException(INVALID_USER);

            return user;

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }


    /***
     * 로그인
     *
     * @param content : user id, pw가 들어있는 dto
     * @return user index, token(access token, refresh token)이 들어있는 dto
     * @throws BaseException
     */
    public LoginResponseDto signIn(LoginRequestDto content) throws BaseException {
        try {
            User user = userRepository.findByUserId(content.getId())
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 회원 탈퇴를 한 적이 있다면
            if (user.getExpired()) throw new BaseException(INVALID_USER);

            if (!aes256Util.decrypt(user.getPw()).equals(content.getPw()))
                throw new BaseException(INVALID_PW);

            String accessToken = jwtTokenProvider.createAccessToken(user.getUserIdx());
            log.info("{}의 아이디를 가진 유저에게 '{}' access token을 발급하였습니다", user.getUserId(), accessToken);

            String refreshToken;

            if (user.getToken() == null) { // refresh token이 없는 경우
                refreshToken = jwtTokenProvider.createRefreshToken(user.getUserIdx());
                user.setToken(refreshToken);

                log.info("{}의 아이디를 가진 유저에게 '{}' refresh token을 발급하였습니다", user.getUserId(), refreshToken);

            } else {
                refreshToken = user.getToken();

                if (jwtTokenProvider.expired(refreshToken)) { // DB에 저장된 refresh token이 만료된 경우
                    String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserIdx());
                    log.info("{}의 아이디를 가진 유저에게 '{}' refresh token을 재발급하였습니다", user.getUserId(), refreshToken);

                    user.setToken(newRefreshToken);
                }
            }

            user.setUpdatedAt(new Date());

            return new LoginResponseDto(user.getUserIdx(), new Token(accessToken, refreshToken));

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    // Todo: 회원 가입
    // Todo: 비밀번호 변경

    /***
     * 회원 탈퇴
     *
     * @param token : 사용자의 access token
     * @param dto : 사용자의 id와 pw가 들어있는 dto
     * @return 정상적으로 탈퇴가 되었다는 문구가 들어있는 dto
     * @throws BaseException
     */
    public AccountDeletionResponseDTO deactivateAccount(String token, AccountDeletionRequestDTO dto) throws BaseException {
        log.info(dto.toString());

        try {
            // 토큰이 발행되었다는 것은 존재하는 유저
            User user = userRepository.findById(dto.getUserIdx())
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 토큰 안에 있는 user 정보와 dto안에 있는 user 정보가 일치하는지 확인
            if (!jwtTokenProvider.getUserIdx(token.split(" ")[1]).equals(dto.getUserIdx()))
                throw new BaseException(INVALID_USER);

            // 올바른 비밀번호인지 확인
            if (!aes256Util.decrypt(user.getPw()).equals(dto.getPw()))
                throw new BaseException(INVALID_PW);

            // 회원 탈퇴
            user.setExpired(true);
            user.setUpdatedAt(new Date());

            return new AccountDeletionResponseDTO();

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

}
