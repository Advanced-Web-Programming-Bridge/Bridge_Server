package gachon.bridge.mealservice.repository;

import gachon.bridge.mealservice.entity.Calories;
import gachon.bridge.mealservice.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaloriesRepository extends JpaRepository<Calories, UUID> {
    Optional<Calories> searchCaloriesByMealIdxAndStatusIsTrue(UUID mealIdx);

    Optional<List<Calories>> findAllByMealIdx(Meal meal);
}
