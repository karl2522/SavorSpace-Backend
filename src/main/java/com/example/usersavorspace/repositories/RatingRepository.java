package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndRecipeRecipeID(Integer userId, Integer recipeID);
    boolean existsByUserIdAndRecipeRecipeID(Integer userId, Integer recipeID);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.recipe.recipeID = :recipeID")
    Double getAverageRatingForRecipe(@Param("recipeID") Integer recipeID);

    Integer countByRecipeRecipeID(Integer recipeID);
}
