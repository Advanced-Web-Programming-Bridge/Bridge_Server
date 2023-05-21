package gachon.bridge.userservice.service;

import gachon.bridge.userservice.base.BaseErrorCode;
import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.*;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.utils.AES256Util;
import gachon.bridge.userservice.utils.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AES256Util aes256Util;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 식별자를 통해 사용자의 정보 가져오기
     *
     * @param userIdx 사용자의 식별자
     * @return 해당 식별자를 가진 사용자
     * @throws BaseException 사용자가 존재하지 않거나 회원 탈퇴한 경우
     */
    public User getUser(UUID userIdx) throws BaseException {
        try {
            User user = getUserById(userIdx);
            checkUserExpired(user);

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
     * @return 사용자의 식별자, token(access token, refresh token), 로그인 성공 시간이 들어있는 dto
     * @throws BaseException 유효하지 않은 사용자, 비밀번호 불일치의 경우
     */
    public LoginResponseDto signIn(LoginRequestDto dto) throws BaseException {
        try {
            User user = userRepository.findByUserId(dto.getId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.INVALID_USER));

            checkUserExpired(user);
            checkPassword(user, dto.getPw());

            // Access token 발급
            String accessToken = jwtTokenProvider.createAccessToken(user.getUserIdx());
            log.info("User with ID '{}' has been issued an access token '{}'.", user.getUserId(), accessToken);

            return new LoginResponseDto(user.getUserIdx(), new Token(accessToken, getRefreshToken(user)));

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    /***
     *  회원 가입
     *
     * @param dto 사용자의 아이디, 비밀번호, (인증된) 이메일이 들어있는 dto
     * @return 사용자의 식별자와 회원 가입 성공 시간이 들어있는 dto
     * @throws BaseException ID의 길이가 0자이거나 45자를 넘어가는 경우, 이미 존재하는 아이디의 경우
     */
    public SignUpResponseDto join(SignUpRequestDto dto) throws BaseException {
        try {
            verifyIdLength(dto.getId());

            // id 중복 확인
            if (userRepository.findByUserId(dto.getId()).isPresent())
                throw new BaseException(BaseErrorCode.EXIST_ID);

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
     * @param dto   사용자의 식별자, 현재 비밀번호, 바꾸고 싶은 비밀번호가 들어있는 dto
     * @return 사용자의 식별자, 비밀번호 변경 시간이 들어있는 dto
     * @throws BaseException 유효하지 않은 사용자, 토큰과 사용자 정보가 일치하지 않은 경우, 비밀번호 불일치의 경우
     */
    public ChangePasswordResponseDto changePassword(String token, ChangePasswordRequestDto dto) throws BaseException {
        try {
            UUID userIdx = UUID.fromString(dto.getUserIdx());
            User user = getUser(userIdx);

            checkAuthentication(token, user, dto.getCurrentPw());

            // 이전 비밀번호와 같은 지 확인
            if (dto.getCurrentPw().equals(dto.getNewPw()))
                throw new BaseException(BaseErrorCode.SAME_PW);

            // 비밀번호 저장
            user.setPw(aes256Util.encrypt(dto.getNewPw()));
            user.setUpdatedAt(LocalDateTime.now());

            return new ChangePasswordResponseDto(userIdx);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    /***
     * 회원 탈퇴
     *
     * @param token 사용자의 access token
     * @param dto   사용자의 식별자, 비밀번호가 들어있는 dto
     * @return 정상적으로 탈퇴가 되었다는 문구가 들어있는 dto
     * @throws BaseException 유효하지 않은 사용자, 토큰과 사용자 정보가 일치하지 않은 경우, 비밀번호 불일치의 경우
     */
    public AccountDeletionResponseDTO leave(String token, AccountDeletionRequestDTO dto) throws BaseException {
        try {
            UUID userIdx = UUID.fromString(dto.getUserIdx());
            User user = getUser(userIdx);

            checkAuthentication(token, user, dto.getPw());

            // 회원 탈퇴
            user.setExpired(true);
            user.setUpdatedAt(LocalDateTime.now());

            return new AccountDeletionResponseDTO();

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        }
    }

    /**
     * 사용자 식별자를 통해 사용자 가져오기
     *
     * @param userId 사용자 아이디
     * @return 해당 아이디를 가진 사용자
     * @throws BaseException 사용자의 식별자를 통해 사용자를 조회할 수 없는 경우
     */
    private User getUserById(UUID userId) throws BaseException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseErrorCode.INVALID_USER));
    }

    /**
     * 사용자의 회원 탈퇴 여부 확인.
     *
     * @param user 사용자 객체
     * @throws BaseException 회원 탈퇴를 한 경우 발생하는 예외
     */
    private void checkUserExpired(User user) throws BaseException {
        if (user.getExpired())
            throw new BaseException(BaseErrorCode.INVALID_USER);
    }

    /**
     * Refresh token 가져오기
     *
     * @param user 사용자
     * @return Refresh token
     */
    private String getRefreshToken(User user) {
        String refreshToken = user.getToken();

        if (refreshToken == null || jwtTokenProvider.expired(refreshToken)) {
            refreshToken = jwtTokenProvider.createRefreshToken(user.getUserIdx());

            log.info("User with ID '{}' has been issued an refresh token '{}'.", user.getUserId(), refreshToken);

            user.setToken(refreshToken);
            user.setUpdatedAt(LocalDateTime.now());
        }

        return refreshToken;
    }

    /**
     * 사용자 아이디의 유효성 검사
     *
     * @param userId 사용자 아이디
     * @throws BaseException 아이디의 길이가 0자이거나 45자를 넘어가는 경우
     */
    private void verifyIdLength(String userId) throws BaseException {
        if (userId.length() == 0)
            throw new BaseException(BaseErrorCode.ID_TOO_SHORT);

        if (userId.length() > 45)
            throw new BaseException(BaseErrorCode.ID_TOO_LONG);
    }

    /**
     * 토큰과 사용자 정보의 유효성 검사
     *
     * @param token    사용자의 (access) token
     * @param user     사용자 정보
     * @param password 비밀번호
     * @throws BaseException 토큰과 사용자 정보가 일치하지 않거나 비밀번호가 일치하지 않는 경우
     */
    private void checkAuthentication(String token, User user, String password) throws BaseException {
        if (!jwtTokenProvider.getUserIdx(token.split(" ")[1]).equals(user.getUserIdx()))
            throw new BaseException(BaseErrorCode.INVALID_INFORMATION);

        checkPassword(user, password);
    }

    /**
     * 주어진 비밀번호가 사용자의 실제 비밀번호와 일치하는지 확인
     *
     * @param user     사용자 정보
     * @param password 비밀번호
     * @throws BaseException 비밀번호가 일치하지 않는 경우
     */
    private void checkPassword(User user, String password) throws BaseException {
        String decryptedPassword = aes256Util.decrypt(user.getPw());
        if (!decryptedPassword.equals(password)) {
            throw new BaseException(BaseErrorCode.INVALID_PW);
        }
    }

}
