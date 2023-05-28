package gachon.bridge.exerciseservice.service;

import gachon.bridge.exerciseservice.base.BaseErrorCode;
import gachon.bridge.exerciseservice.base.BaseException;
import gachon.bridge.exerciseservice.dto.*;
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date targetDate;

        try{
            targetDate = df.parse(date);
        }catch (ParseException exception){
            throw new BaseException(BaseErrorCode.BAD_DATE_FORMAT);
        }

        return targetDate;
    }

    /**
     * 운동 추가 API - Service
     * */
    public PostExerciseRes postExercise(PostExerciseReq req, UUID userIdx, String date) throws BaseException {
        Date targetDate = toDate(date);
        ExerciseDto target = req.getExercise();

        Exercise entity = Exercise
                .builder()
                .userIdx(userIdx)
                .exercise_date(targetDate)
                .exercise_area(target.getCategory())
                .exercise_name(target.getName())
                .exercise_target_count(target.getGoal())
                .build();

        UUID insertedIdx = exerciseRepository.save(entity).getExerciseIdx();

        return new PostExerciseRes(insertedIdx.toString(), targetDate.toString());
    }

    /**
     * 운동 목표 수정 API - Service
     * */
    public PatchTargetCountRes patchGoal(PatchTargetCountReq req) throws BaseException {
        UUID targetIdx = UUID.fromString(req.getExerciseIdx());

        Optional<Exercise> OptionalEntity = exerciseRepository.findById(targetIdx);
        OptionalEntity.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        Exercise entity = OptionalEntity.get();
        entity.updateGoalCount(req.getExerciseTargetCount());

        exerciseRepository.save(entity);

        return PatchTargetCountRes
                .builder()
                .exerciseIdx(entity.getExerciseIdx().toString())
                .exerciseDate(entity.getExerciseDate().toString())
                .exerciseTargetCount(entity.getExercise_target_count())
                .build();
    }

    public PatchDoneCountRes patchDone(PatchDoneCountReq req) throws BaseException {
        UUID targetIdx = UUID.fromString(req.getExerciseIdx());

        Optional<Exercise> OptionalEntity = exerciseRepository.findById(targetIdx);
        OptionalEntity.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        Exercise entity = OptionalEntity.get();

        entity.updateDoneCount(req.getModifyDoneCount());

        exerciseRepository.save(entity);

        return PatchDoneCountRes
                .builder()
                .exerciseIdx(entity.getExerciseIdx().toString())
                .exerciseDate(entity.getExerciseDate().toString())
                .exerciseDoneCount(entity.getExercise_did_count())
                .build();
    }

    /**
     * 운동 한 리스트 조회 API - Service
     * */
    public GetExerciseListDoneRes getDoneList(UUID userIdx, String date) throws BaseException {

        Optional<List<Exercise>> optionalList = exerciseRepository.findAllByUserIdxAndExerciseDate(userIdx, toDate(date));
        optionalList.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        List<Exercise> entityList = optionalList.get();

        HashMap<String, List<String>> exerciseHash = new HashMap<>();

        for(Exercise element : entityList){
            String area = element.getExercise_area();

            if(exerciseHash.containsKey(area)){
                exerciseHash.get(area).add(element.getExercise_name());
            }
            else{
                exerciseHash.put(area, new ArrayList<>());
                exerciseHash.get(area).add((element.getExercise_name()));
            }
        }

        List<ExerciseDoneListDto> subList = new ArrayList<>();

        for(String key : exerciseHash.keySet()){

            List<String> list = new ArrayList<>(exerciseHash.get(key));

            subList.add(new ExerciseDoneListDto(key, list));
        }

        return GetExerciseListDoneRes
                .builder()
                .exerciseDate(date)
                .exercise(subList)
                .build();
    }

    public GetExerciseListRes getExerciseList(UUID userIdx, String date) throws BaseException {
        Optional<List<Exercise>> optionalList = exerciseRepository.findAllByUserIdxAndExerciseDate(userIdx, toDate(date));
        optionalList.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        List<Exercise> entityList = optionalList.get();

        HashMap<String, List<GetExerciseDto>> exerciseHash = new HashMap<>();

        for(Exercise element : entityList) {
            String area = element.getExercise_area();

            if(exerciseHash.containsKey(area)){
                exerciseHash.get(area).add(GetExerciseDto
                        .builder()
                                .name(element.getExercise_name())
                                .goal(element.getExercise_target_count())
                                .done(element.getExercise_did_count())
                        .build());
            }
            else{
                exerciseHash.put(area, new ArrayList<>());
                exerciseHash.get(area).add(GetExerciseDto
                        .builder()
                        .name(element.getExercise_name())
                        .goal(element.getExercise_target_count())
                        .done(element.getExercise_did_count())
                        .build());
            }
        }

        List<ExerciseListDto> subList = new ArrayList<>();

        for(String key : exerciseHash.keySet()){
            List<GetExerciseDto> list = exerciseHash.get(key);

            subList.add(new ExerciseListDto(key, list));
        }

        return GetExerciseListRes
                .builder()
                .exerciseDate(date)
                .exercise(subList)
                .build();
    }
}
