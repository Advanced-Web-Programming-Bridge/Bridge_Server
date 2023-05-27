package gachon.bridge.exerciseservice.repository;

import gachon.bridge.exerciseservice.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ExerciseRepository extends JpaRepository <Exercise, UUID> {
    Optional<List<Exercise>> findAllByUserIdxAndExercise_date(UUID userIdx, Date exercise_date);

    boolean existsByUserIdxAndExercise_date(UUID userIdx, Date exercise_date);
}
