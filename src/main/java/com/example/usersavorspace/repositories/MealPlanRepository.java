package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUserIdAndYearAndMonth(Integer userId, Integer year, Integer month);
    List<MealPlan> findByUserId(Integer userId);
    void deleteAllByRecipe_RecipeID(Integer recipeId);

    @Query("SELECT mp FROM MealPlan mp WHERE mp.user.id = :userId " +
            "AND (mp.year > :startYear OR " +
            "(mp.year = :startYear AND mp.month > :startMonth) OR " +
            "(mp.year = :startYear AND mp.month = :startMonth AND mp.day >= :startDay)) " +
            "AND (mp.year < :endYear OR " +
            "(mp.year = :endYear AND mp.month < :endMonth) OR " +
            "(mp.year = :endYear AND mp.month = :endMonth AND mp.day <= :endDay))")
    List<MealPlan> findMealPlansBetweenDates(
            @Param("userId") Integer userId,
            @Param("startYear") int startYear,
            @Param("startMonth") int startMonth,
            @Param("startDay") int startDay,
            @Param("endYear") int endYear,
            @Param("endMonth") int endMonth,
            @Param("endDay") int endDay
    );
}