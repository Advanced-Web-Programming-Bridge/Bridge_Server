package gachon.bridge.mealservice.controller;

import gachon.bridge.mealservice.base.BaseErrorCode;
import gachon.bridge.mealservice.base.BaseException;
import gachon.bridge.mealservice.base.BaseResponse;
import gachon.bridge.mealservice.dto.*;
import gachon.bridge.mealservice.feign.MealFeignClient;
import gachon.bridge.mealservice.service.MealService;
import gachon.bridge.mealservice.utils.RegexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/meals")
@Slf4j
public class MealController {

    MealService mealService;
    MealFeignClient mealFeignClient;

    @Autowired
    public MealController(MealService mealService, MealFeignClient mealFeignClient) {
        this.mealService = mealService;
        this.mealFeignClient = mealFeignClient;
    }
    /**
     * 특정 일자에 먹은 칼로리 API (Meal page 구성 API)
     * */
    @GetMapping("/{date}")
    public BaseResponse<GetMealCalRes> getMealCalories(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @PathVariable("date") String date){
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = mealFeignClient.getUserIdxFromJwt(jwt).getData();


            return new BaseResponse<>(mealService.getMealCalories(userIdx, date));
        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }

    /**
     * 얼만큼 칼로리를 섭취했는지 입력하는 API
     * [POST] ./api/meals/{date}
     * */
    @PostMapping("/{date}")
    public BaseResponse<PostMealRes> postMeal(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @RequestBody PostMealReq req, @PathVariable("date") String date){
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = mealFeignClient.getUserIdxFromJwt(jwt).getData();

            return new BaseResponse<>(mealService.postMeal(userIdx, date, req));
        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }

    /**
     * 얼만큼 칼로리를 섭취했는지 수정하는 API
     * [PATCH] ./api/meals/{date}
     * */
    @PatchMapping("/{date}")
    public BaseResponse<PatchMealRes> patchMeal(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @RequestBody PatchMealReq req, @PathVariable("date") String date){
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = mealFeignClient.getUserIdxFromJwt(jwt).getData();

            return new BaseResponse<>(mealService.patchMeal(userIdx, req, date));
        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }
    /**
     * 목표 칼로리 수정 API
     * [PATCH] ./api/meals/{date}/goal
     * */
    @PatchMapping("/{date}/goal")
    public BaseResponse<PatchGoalRes> patchMealGoal(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @RequestBody PatchGoalReq req, @PathVariable("date") String date){
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = mealFeignClient.getUserIdxFromJwt(jwt).getData();

            return new BaseResponse<>(mealService.patchMealGoal(userIdx, date, req));


        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }
}
