package gachon.bridge.userservice.controller;

import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.base.BaseResponse;
import gachon.bridge.userservice.dto.UserDto;
import gachon.bridge.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auths")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public BaseResponse<UserDto> getUserInfo(@PathVariable String id) {
        BaseResponse response;

        try {
            UserDto user = new UserDto(userService.getUserByUserId(id));
            logger.info("user \'" + id + "\' request getUserInfo method");

            response = new BaseResponse(user);

        } catch (BaseException e) {
            logger.error("Attempted to look up a user with id of \'" + id + "\' but no user with that id");
            response = new BaseResponse(e);
        }

        return response;
    }

}
