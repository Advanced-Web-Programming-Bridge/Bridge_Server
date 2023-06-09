package gachon.bridge.userservice.controller;

import gachon.bridge.userservice.base.BaseResponse;
import gachon.bridge.userservice.domain.User;
import gachon.bridge.userservice.repository.UserRepository;
import gachon.bridge.userservice.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class UserFeignController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @GetMapping("/feign/user/{userIdx}")
    public BaseResponse<String> findUser(@PathVariable UUID userIdx){
        Optional<User> user = userRepository.findByUserId(userIdx.toString());

        return user.map(value -> new BaseResponse<>(value.getUserIdx().toString())).orElseGet(() -> new BaseResponse<>("Not Exist"));
    }

    @GetMapping("/feign/user/userIdx/{jwt}")
    public BaseResponse<UUID> getUserIdxFromJwt(@PathVariable String jwt){
        UUID userIdx = jwtTokenProvider.getUserIdx(jwt);

        return new BaseResponse<>(userIdx);
    }
}
