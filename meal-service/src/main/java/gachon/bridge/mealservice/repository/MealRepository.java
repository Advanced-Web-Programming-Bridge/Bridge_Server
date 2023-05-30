package gachon.bridge.mealservice.repository;

import gachon.bridge.mealservice.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {
    Optional<Meal> findByUserIdxAndDate(UUID userIdx, Date date);

    boolean existsByUserIdxAndDate(UUID userIdx, Date date);
}
