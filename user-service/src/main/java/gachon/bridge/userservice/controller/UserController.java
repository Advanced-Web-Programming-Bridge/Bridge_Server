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

    @PatchMapping("/deactivation")
    public BaseResponse deactivateAccount(@RequestHeader("Authorization") String token, @RequestBody AccountDeletionRequestDTO dto) {
        try {
            AccountDeletionResponseDTO data = userService.deactivateAccount(token, dto);
            log.info("'{}'의 식별자를 가진 유저가 회원 탈퇴에 성공하였습니다", dto.getUserIdx());

            return new BaseResponse<>(data);

        } catch (BaseException e) {
            log.error("'{}'의 식별자를 가진 유저가 회원 탈퇴 하는 데 실패하였습니다", dto.getUserIdx());
            return new BaseResponse<>(e);
        }
    }

}
