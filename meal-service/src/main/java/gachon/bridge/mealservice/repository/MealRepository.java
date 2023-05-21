package gachon.bridge.mealservice.repository;

import gachon.bridge.mealservice.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

}
