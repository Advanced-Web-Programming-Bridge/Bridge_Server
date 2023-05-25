package gachon.bridge.mealservice.service;

import gachon.bridge.mealservice.base.BaseErrorCode;
import gachon.bridge.mealservice.base.BaseException;
import gachon.bridge.mealservice.dto.*;
import gachon.bridge.mealservice.entity.Calories;
import gachon.bridge.mealservice.entity.Meal;
import gachon.bridge.mealservice.entity.MealTime;
import gachon.bridge.mealservice.repository.CaloriesRepository;
import gachon.bridge.mealservice.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MealService {
    MealRepository mealRepository;
    CaloriesRepository caloriesRepository;
    @Autowired
    public MealService(MealRepository mealRepository, CaloriesRepository caloriesRepository) {
        this.mealRepository = mealRepository;
        this.caloriesRepository = caloriesRepository;
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
     * 특정 일자에 먹은 칼로리 API (Meal page 구성 API) - service
     * */
    public GetMealCalRes getMealCalories(UUID userIdx, String date) throws BaseException {

        Date targetDate = toDate(date);

        //대상 Meal 가져오기
        Optional<Meal> meal = mealRepository.findByUserIdxAndDate(userIdx, targetDate);
        meal.orElseThrow(() -> new BaseException(BaseErrorCode.NO_MEAL_EXIST));

        GetMealCalRes res = new GetMealCalRes();
        double calSum = 0;

        Optional<List<Calories>> calories = caloriesRepository.findAllByMealIdx(meal.get());
        calories.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        //가져온 대상 Meal 에서 칼로리 가져오기
        for(Calories element : calories.get()){
          MealTime time = element.getTime();
          double currentCal = element.getCalories();

          switch (time.name()) {
              case "MORNING" :
                  res.setMorningCal(currentCal);
              case "LUNCH" :
                  res.setLunchCal(currentCal);
              case "DINNER" :
                  res.setLunchCal(currentCal);
          }

            calSum += currentCal;
        }

        res.setAllCal(calSum);
        res.setMealIdx(meal.get().getMealIdx());
        return res;
    }

    /**
     * 얼만큼 칼로리를 섭취했는지 입력하는 API - service
     * */
    public PostMealRes postMeal(UUID userIdx, String date, PostMealReq req) throws BaseException {
        //Date 변환
        Date targetDate = toDate(date);

        //기록 존재 여부
        if(mealRepository.existsByUserIdxAndDate(userIdx, targetDate))
            throw new BaseException(BaseErrorCode.EXIST_MEAL);

        //Meal insert
        Meal meal = Meal.builder()
                .userIdx(userIdx)
                .date(targetDate).build();

        UUID mealIdx = mealRepository.save(meal).getMealIdx();

        Optional<Meal> insertedMeal = mealRepository.findById(mealIdx);
        insertedMeal.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        //Calories inert
        batchSave(insertedMeal.get(), req.getMorningCal(), req.getLunchCal(), req.getDinnerCal());

        return new PostMealRes(mealIdx, targetDate.toString(), req.getMorningCal(), req.getLunchCal(), req.getDinnerCal());
    }

    /**
     * 얼만큼 칼로리를 섭취했는지 수정하는 API - Service
     * */
    public PatchMealRes patchMeal(UUID userIdx, PatchMealReq req, String date) throws BaseException {
        //Date 변환
        Date targetDate = toDate(date);

        //기록 존재 여부
        if(!mealRepository.existsByUserIdxAndDate(userIdx, targetDate))
            throw new BaseException(BaseErrorCode.NO_MEAL_EXIST);

        Optional<Meal> targetMeal = mealRepository.findByUserIdxAndDate(userIdx, targetDate);
        targetMeal.orElseThrow(() -> new BaseException(BaseErrorCode.DATABASE_ERROR));

        //Calories update
        batchSave(targetMeal.get(), req.getMorningCal(), req.getLunchCal(), req.getDinnerCal());

        return new PatchMealRes(targetMeal.get().getMealIdx(), targetDate.toString(), req.getMorningCal(), req.getLunchCal(), req.getDinnerCal());

    }

    /**
     * postMeal() / patchMeal() 공통 업데이트/인서트 메서드
     * */
    private void batchSave(Meal targetMeal, double morningCal, double lunchCal, double dinnerCal) {
        Calories morning = Calories
                .builder()
                .mealIdx(targetMeal)
                .calories(morningCal)
                .time(MealTime.MORNING).build();
        caloriesRepository.save(morning);

        Calories lunch = Calories
                .builder()
                .mealIdx(targetMeal)
                .calories(lunchCal)
                .time(MealTime.LUNCH).build();
        caloriesRepository.save(lunch);

        Calories dinner = Calories
                .builder()
                .mealIdx(targetMeal)
                .calories(dinnerCal)
                .time(MealTime.DINNER).build();
        caloriesRepository.save(dinner);
    }

    /**
     * 목표 칼로리 수정 API - Service
     * */
    public PatchGoalRes patchMealGoal(UUID userIdx, String date, PatchGoalReq req) throws BaseException {
        Date targetDate = toDate(date);

        Optional<Meal> meal = mealRepository.findById(UUID.fromString(req.getMealIdx()));
        meal.orElseThrow(() -> new BaseException(BaseErrorCode.NOT_EXIST_MEAL));

        meal.get().updateMealGoal(req.getGoal());

        mealRepository.save(meal.get());

        return new PatchGoalRes(meal.get().getMealIdx(), req.getGoal());
    }
}
