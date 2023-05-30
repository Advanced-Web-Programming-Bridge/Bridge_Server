package gachon.bridge.exerciseservice.repository;

import gachon.bridge.exerciseservice.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ExerciseRepository extends JpaRepository <Exercise, UUID> {

//    @Query("select exerciseIdx, exercise_date, exercise_area, exercise_name, exercise_target_count, exercise_did_count, achieved, created_at, updated_at, status " +
//            "from Exercise where userIdx = :userIdx and exercise_date = :date")
    Optional<List<Exercise>> findAllByUserIdxAndExerciseDate(UUID userIdx,Date exercise_date);

}
