package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByUserOrderByCreatedAtDesc(User user);
    Page<Recipe> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<Recipe> findByRecipeIDAndUser(int recipeID, User user);
    boolean existsByRecipeIDAndUser(int recipeID, User user);
}

