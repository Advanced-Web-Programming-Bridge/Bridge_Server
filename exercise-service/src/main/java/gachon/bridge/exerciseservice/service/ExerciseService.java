package gachon.bridge.exerciseservice.service;

import gachon.bridge.exerciseservice.base.BaseErrorCode;
import gachon.bridge.exerciseservice.base.BaseException;
import gachon.bridge.exerciseservice.dto.GetExerciseListRes;
import gachon.bridge.exerciseservice.dto.PatchExerciseTargetCountReq;
import gachon.bridge.exerciseservice.dto.PatchExerciseTargetCountRes;
import gachon.bridge.exerciseservice.entity.Exercise;
import gachon.bridge.exerciseservice.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExerciseService {

    ExerciseRepository exerciseRepository;

    @Autowired
    public  ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * String to Date 메서드
     * */
    private Date toDate(String date) throws BaseException {
        DateFormat df = new SimpleDateFormat("yy-MM-dd");
        Date targetDate;

        try{
            targetDate = df.parse(date);
        }catch (ParseException exception){
            throw new BaseException(BaseErrorCode.BAD_DATE_FORMAT);
        }

        return targetDate;
    }

    public GetExerciseListRes getExerciseList(UUID userIdx, String date) throws  BaseException {
        Date targetDate = toDate(date);

        Optional<List<Exercise>> exercise = exerciseRepository.findAllByUserIdxAndExercise_date(userIdx, targetDate);
        exercise.orElseThrow(() -> new BaseException(BaseErrorCode.NO_EXERCISE_EXIST));

        Optional<List<Exercise>> exEntity = exerciseRepository.findAllByUserIdxAndExercise_date(userIdx, targetDate);
        List<GetExerciseListRes> exList = new ArrayList<>();

        GetExerciseListRes res = new GetExerciseListRes();

        for (GetExerciseListRes element : exList){

        }
        return res;
    }

    public PatchExerciseTargetCountRes patchExerciseGoal(UUID userIdx, String exerciseDate, PatchExerciseTargetCountReq req) throws BaseException {
        Date targetDate = toDate(exerciseDate);
        Optional<List<Exercise>> exercise = exerciseRepository.findAllByUserIdxAndExercise_date(userIdx, targetDate);
        exercise.orElseThrow(() -> new BaseException(BaseErrorCode.NOT_EXIST_EXERCISE));

        List<Exercise> exercises = exercise.get();
        for (Exercise exercise : exercises) {
            // 업데이트해야 할 필요한 로직을 구현
            exercise.updateExerciseGoal(req.getExerciseTargetCount());
            // exercise 엔티티를 저장하여 변경 사항을 업데이트
            exerciseRepository.save(exercise);
        }
        return new PatchExerciseTargetCountRes(exercise.get().getExerciseIdx(), req.getExerciseTargetCount());
    }

}
