package gachon.bridge.mealservice.feign;


import gachon.bridge.mealservice.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("userservice")
public interface MealFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/feign/findUser/{userIdx}", consumes = "application/json")
    BaseResponse<String> findUser(@PathVariable("userIdx") UUID userIdx);
}
