package gachon.bridge.exerciseservice.feign;

import gachon.bridge.exerciseservice.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("user-service")
public interface ExerciseFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/feign/user/{userIdx}", consumes = "application/json")
    BaseResponse<String> findUser(@PathVariable("userIdx") UUID userIdx);

    @RequestMapping(method = RequestMethod.GET, value = "/feign/user/userIdx/{jwt}", consumes = "application/json")
    BaseResponse<UUID> getUserIdxFromJwt(@PathVariable("jwt") String jwt);
}
