package gachon.bridge.userservice.controller;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.base.BaseResponse;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.dto.*;
import gachon.bridge.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static gachon.bridge.userservice.base.BaseErrorCode.INVALID_UUID_FORMAT;
import static gachon.bridge.userservice.utils.UUIDValidator.validUUIDFormat;

@RestController
@RequestMapping("/api/auths")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{userIdx}")
    public BaseResponse getUserInfo(@PathVariable UUID userIdx) {
        try {
            User data = userService.getUser(userIdx);
            log.info("{}의 아이디를 가진 유저를 찾았습니다", data.getUserId());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("{}의 user index를 가진 유저를 찾는 데 실패하였습니다", userIdx);
            return new BaseResponse<>(e);
        }
    }

    @PostMapping("/join")
    public BaseResponse join(@RequestBody SignUpRequestDto dto) {
        try {
            SignUpResponseDto data = userService.join(dto);
            log.info("사용자 '{}'가 성공적으로 가입되었습니다.", dto.getId());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("'{}'의 이메일을 가진 유저가 회원 가입 하는 데 실패하였습니다.", dto.getEmail());
            return new BaseResponse<>(e);
        }
    }

    @PostMapping("/login")
    public BaseResponse signIn(@RequestBody LoginRequestDto dto) {
        try {
            LoginResponseDto data = userService.signIn(dto);
            log.info("{}의 아이디를 가진 유저가 로그인에 성공하였습니다", dto.getId());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("{}의 아이디를 가진 유저가 로그인 하는 데 실패하였습니다", dto.getId());
            return new BaseResponse<>(e);
        }
    }

    @PatchMapping("/change/pw")
    public BaseResponse changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequestDto dto) {
        try {
            if (!validUUIDFormat(dto.getUserIdx()))
                throw new IllegalArgumentException();

            ChangePasswordResponseDto data = userService.changePassword(token, dto);
            log.info("'{}'의 식별자를 가진 유저가 비밀번호를 변경 하는 데 성공하였습니다", dto.getUserIdx());

            return new BaseResponse<>(data);

        } catch (IllegalArgumentException e) {
            log.error("Attempted to change password with an invalid identifier: {}", dto.getUserIdx());
            return new BaseResponse<>(new BaseException(INVALID_UUID_FORMAT));

        } catch (BaseException e) {
            log.error("'{}'의 식별자를 가진 유저가 비밀번호를 변경 하는 데 실패하였습니다", dto.getUserIdx());
            return new BaseResponse<>(e);
        }
    }

    @PatchMapping("/deactivation")
    public BaseResponse deactivateAccount(@RequestHeader("Authorization") String token, @RequestBody AccountDeletionRequestDTO dto) {
        try {
            if (!validUUIDFormat(dto.getUserIdx()))
                throw new IllegalArgumentException();

            AccountDeletionResponseDTO data = userService.leave(token, dto);
            log.info("'{}'의 식별자를 가진 유저가 회원 탈퇴에 성공하였습니다", dto.getUserIdx());

            return new BaseResponse<>(data);

        } catch (IllegalArgumentException e) {
            log.error("Attempted to perform a member withdrawal with an invalid identifier: {}", dto.getUserIdx());
            return new BaseResponse<>(new BaseException(INVALID_UUID_FORMAT));

        } catch (BaseException e) {
            log.error("'{}'의 식별자를 가진 유저가 회원 탈퇴 하는 데 실패하였습니다", dto.getUserIdx());
            return new BaseResponse<>(e);
        }
    }
}