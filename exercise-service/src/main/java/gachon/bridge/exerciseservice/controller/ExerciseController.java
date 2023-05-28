package gachon.bridge.exerciseservice.controller;

import gachon.bridge.exerciseservice.base.BaseErrorCode;
import gachon.bridge.exerciseservice.base.BaseException;
import gachon.bridge.exerciseservice.base.BaseResponse;
import gachon.bridge.exerciseservice.dto.*;
import gachon.bridge.exerciseservice.feign.ExerciseFeignClient;
import gachon.bridge.exerciseservice.service.ExerciseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
@Slf4j
public class ExerciseController {
    private final ExerciseService  exerciseService;
    private final ExerciseFeignClient exerciseFeignClient;

    @Autowired
    public ExerciseController(ExerciseService exerciseService, ExerciseFeignClient exerciseFeignClient) {
        this.exerciseService = exerciseService;
        this.exerciseFeignClient = exerciseFeignClient;
    }

    /**
     * 운동 추가 API
     * [POST] ./api/exercises/{date}
     * */
    @PostMapping("/{date}")
    public BaseResponse<PostExerciseRes> postExercise(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @RequestBody PostExerciseReq req, @PathVariable String date) {
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = exerciseFeignClient.getUserIdxFromJwt(jwt.split(" ")[1]).getData();

            if(!userIdx.toString().equals(req.getUserIdx())) throw new BaseException(BaseErrorCode.INVALID_USER_IDX);

            return new BaseResponse<>(exerciseService.postExercise(req, userIdx, date));

        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }

    /**
     * 운동 목표 수정 API
     * [PATCH] ./api/exercises/{date}/goal
     * */

    @PatchMapping("/{date}/goal")
    public BaseResponse<PatchTargetCountRes> patchGoal(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @RequestBody PatchTargetCountReq req, @PathVariable String date) {
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = exerciseFeignClient.getUserIdxFromJwt(jwt.split(" ")[1]).getData();

            if(!userIdx.toString().equals(req.getUserIdx())) throw new BaseException(BaseErrorCode.INVALID_USER_IDX);

            return new BaseResponse<>(exerciseService.patchGoal(req));

        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }

    /**
     * 운동 완료 횟수 수정 API
     * [PATCH] ./api/exercises/{date}/done
     * */
    @PatchMapping("/{date}/done")
    public BaseResponse<PatchDoneCountRes> patchDone(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @RequestBody PatchDoneCountReq req, @PathVariable String date) {
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = exerciseFeignClient.getUserIdxFromJwt(jwt.split(" ")[1]).getData();

            if(!userIdx.toString().equals(req.getUserIdx())) throw new BaseException(BaseErrorCode.INVALID_USER_IDX);

            return new BaseResponse<>(exerciseService.patchDone(req));
        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }

    /**
     * 운동 한 리스트 조회 API
     * [GET] ./api/exercises/done/{date}?user-idx = ?
     * */
    @GetMapping("/done/{date}")
    public BaseResponse<GetExerciseListDoneRes> getDoneList(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @PathVariable("date") String date, @RequestParam("user-idx") String reqUserIdx){
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = exerciseFeignClient.getUserIdxFromJwt(jwt.split(" ")[1]).getData();

            if(!userIdx.toString().equals(reqUserIdx)) throw new BaseException(BaseErrorCode.INVALID_USER_IDX);

            return new BaseResponse<>(exerciseService.getDoneList(userIdx, date));
        }catch (BaseException exception){
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }

    /**
     * 운동 할 리스트 조회
     * [GET] ./api/exercises/{date}?user-idx = ?
     * */
    @GetMapping("/{date}")
    public BaseResponse<GetExerciseListRes> getExerciseList(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt, @PathVariable("date") String date, @RequestParam("user-idx") String reqUserIdx) {
        try{
            if(jwt == null) throw new BaseException(BaseErrorCode.NO_TOKEN);
            UUID userIdx = exerciseFeignClient.getUserIdxFromJwt(jwt.split(" ")[1]).getData();

            if(!userIdx.toString().equals(reqUserIdx)) throw new BaseException(BaseErrorCode.INVALID_USER_IDX);

            return new BaseResponse<>(exerciseService.getExerciseList(userIdx, date));
        }catch (BaseException exception) {
            log.error(exception.getMessage());
            return new BaseResponse<>(exception);
        }
    }
}
