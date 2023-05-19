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
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{userIdx}")
    public BaseResponse<User> getUserInfo(@PathVariable UUID userIdx) {
        try {
            User data = userService.getUser(userIdx);
            log.info("Found a user with the identifier '{}'", data.getUserId());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("Failed to find a user with the identifier '{}'", userIdx);
            return new BaseResponse<>(e);
        }
    }

    @PostMapping("/join")
    public BaseResponse<SignUpResponseDto> join(@RequestBody SignUpRequestDto dto) {
        try {
            SignUpResponseDto data = userService.join(dto);
            log.info("User with the identifier '{}' has been successfully registered.", dto.getId());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("Failed to register a user with the email '{}'.", dto.getEmail());
            return new BaseResponse<>(e);
        }
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponseDto> signIn(@RequestBody LoginRequestDto dto) {
        try {
            LoginResponseDto data = userService.signIn(dto);
            log.info("User with the ID '{}' has successfully logged in.", dto.getId());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("Failed to log in for the user with the ID '{}'.", dto.getId());
            return new BaseResponse<>(e);
        }
    }

    @PatchMapping("/change/pw")
    public BaseResponse<ChangePasswordResponseDto> changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequestDto dto) {
        try {
            if (!validUUIDFormat(dto.getUserIdx()))
                throw new IllegalArgumentException();

            ChangePasswordResponseDto data = userService.changePassword(token, dto);
            log.info("User with the identifier '{}' has successfully changed the password.", dto.getUserIdx());

            return new BaseResponse<>(data);

        } catch (IllegalArgumentException e) {
            log.error("Attempted to change password with an invalid identifier: {}", dto.getUserIdx());
            return new BaseResponse<>(new BaseException(INVALID_UUID_FORMAT));

        } catch (BaseException e) {
            log.error("Failed to change the password for the user with the identifier '{}'.", dto.getUserIdx());
            return new BaseResponse<>(e);
        }
    }

    @PatchMapping("/deactivation")
    public BaseResponse<AccountDeletionResponseDTO> deactivateAccount(@RequestHeader("Authorization") String token, @RequestBody AccountDeletionRequestDTO dto) {
        try {
            if (!validUUIDFormat(dto.getUserIdx()))
                throw new IllegalArgumentException();

            AccountDeletionResponseDTO data = userService.leave(token, dto);
            log.info("User with the identifier '{}' has successfully withdrawn from membership.", dto.getUserIdx());

            return new BaseResponse<>(data);

        } catch (IllegalArgumentException e) {
            log.error("Attempted to perform a member withdrawal with an invalid identifier: {}", dto.getUserIdx());
            return new BaseResponse<>(new BaseException(INVALID_UUID_FORMAT));

        } catch (BaseException e) {
            log.error("Failed to withdraw the user with the identifier '{}'.", dto.getUserIdx());
            return new BaseResponse<>(e);
        }
    }
}