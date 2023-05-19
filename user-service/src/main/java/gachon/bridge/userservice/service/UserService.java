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
     * 식별자를 통해 사용자의 정보 가져오기
     *
     * @param userIdx 사용자의 식별자
     * @return 해당 식별자를 가진 사용자
     * @throws BaseException
     */
    public User getUser(UUID userIdx) throws BaseException {
        try {
            User user = userRepository.findById(userIdx)
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 회원 탈퇴를 한 적이 있는지 확인
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
     * @param dto 사용자의 아이디, 비밀번호가 들어있는 dto
     * @return 사용자의 식별자, token(access token, refresh token)이 들어있는 dto
     * @throws BaseException
     */
    public LoginResponseDto signIn(LoginRequestDto dto) throws BaseException {
        try {
            User user = userRepository.findByUserId(dto.getId())
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 회원 탈퇴를 한 적이 있는지 확인
            if (user.getExpired()) throw new BaseException(INVALID_USER);

            // DB에 저장된 비밀번호와 일치하는지 확인
            if (!aes256Util.decrypt(user.getPw()).equals(dto.getPw()))
                throw new BaseException(INVALID_PW);

            // Access token 발급
            String accessToken = jwtTokenProvider.createAccessToken(user.getUserIdx());
            log.info("{}의 아이디를 가진 유저에게 '{}' access token을 발급하였습니다", user.getUserId(), accessToken);

            String refreshToken;

            if (user.getToken() == null) { // Refresh token이 없는 경우
                // Refresh token 발급 및 DB에 설정
                refreshToken = jwtTokenProvider.createRefreshToken(user.getUserIdx());
                user.setToken(refreshToken);
                user.setUpdatedAt(new Date());

                log.info("{}의 아이디를 가진 유저에게 '{}' refresh token을 발급하였습니다", user.getUserId(), refreshToken);

            } else { // Refresh token이 있는 경우
                refreshToken = user.getToken();

                if (jwtTokenProvider.expired(refreshToken)) { // DB에 저장된 refresh token이 만료된 경우
                    // Refresh token 재발급
                    String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserIdx());
                    log.info("{}의 아이디를 가진 유저에게 '{}' refresh token을 재발급하였습니다", user.getUserId(), refreshToken);

                    // DB에 설정
                    user.setToken(newRefreshToken);
                    user.setUpdatedAt(new Date());
                }
            }

            return new LoginResponseDto(user.getUserIdx(), new Token(accessToken, refreshToken));

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    /***
     *  회원 가입
     *
     * @param dto 사용자의 아이디, 비밀번호, 이메일이 들어있는 dto
     * @return 사용자의 식별자와 정상적으로 회원 가입이 되었다는 문구가 들어있는 dto
     * @throws BaseException
     */
    public SignUpResponseDto join(SignUpRequestDto dto) throws BaseException {
        try {
            // id 길이 확인 (0 < id.length() <= 45)
            if (dto.getId().length() == 0) throw new BaseException(ID_TOO_SHORT);
            if (dto.getId().length() > 45) throw new BaseException(ID_TOO_LONG);

            // id 중복 확인
            if (userRepository.findByUserId(dto.getId()).isPresent())
                throw new BaseException(EXIST_ID);

            // Todo: 유효한 이메일인지 확인

            // 회원 가입
            User user = new User(dto.getId(), aes256Util.encrypt(dto.getPw()), dto.getEmail());
            userRepository.save(user);

            return new SignUpResponseDto(user.getUserIdx());

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    /***
     * 비밀번호 변경
     *
     * @param token 사용자의 access token
     * @param dto 사용자의 식별자, 현재 비밀번호, 바꾸고 싶은 비밀번호가 들어있는 dto
     * @return 사용자의 식별자, 비밀번호 변경 시간이 들어있는 dto
     * @throws BaseException
     */
    public ChangePasswordResponseDto changePassword(String token, ChangePasswordRequestDto dto) throws BaseException {
        try {
            // 받은 사용자의 식별자를 통해 사용자가 있는지 확인
            User user = userRepository.findById(dto.getUserIdx())
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 회원 탈퇴를 한 적이 있는지 확인
            if (user.getExpired()) throw new BaseException(INVALID_USER);

            // 토큰 안에 있는 user 정보와 dto안에 있는 user 정보가 일치하는지 확인
            if (!jwtTokenProvider.getUserIdx(token.split(" ")[1]).equals(dto.getUserIdx()))
                throw new BaseException(INVALID_INFORMATION);

            // 올바른 비밀번호인지 확인
            if (!aes256Util.decrypt(user.getPw()).equals(dto.getCurrentPw()))
                throw new BaseException(INVALID_PW);

            // 암호화된 비밀번호 저장
            user.setPw(aes256Util.encrypt(dto.getNewPw()));
            user.setUpdatedAt(new Date());

            return new ChangePasswordResponseDto(user.getUserIdx());

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    /***
     * 회원 탈퇴
     *
     * @param token : 사용자의 access token
     * @param dto : 사용자의 식별자, 비밀번호가 들어있는 dto
     * @return 정상적으로 탈퇴가 되었다는 문구가 들어있는 dto
     * @throws BaseException
     */
    public AccountDeletionResponseDTO deactivateAccount(String token, AccountDeletionRequestDTO dto) throws BaseException {
        try {
            // 받은 사용자의 식별자를 통해 사용자가 있는지 확인
            User user = userRepository.findById(dto.getUserIdx())
                    .orElseThrow(() -> new BaseException(INVALID_USER));

            // 회원 탈퇴를 한 적이 있는지 확인
            if (user.getExpired()) throw new BaseException(INVALID_USER);

            // 토큰 안에 있는 user 정보와 dto안에 있는 user 정보가 일치하는지 확인
            if (!jwtTokenProvider.getUserIdx(token.split(" ")[1]).equals(dto.getUserIdx()))
                throw new BaseException(INVALID_INFORMATION);

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
