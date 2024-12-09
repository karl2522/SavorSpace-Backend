package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.RecipeVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeVersionRepository extends JpaRepository<RecipeVersion, Integer> {
    List<RecipeVersion> findByOriginalRecipe_RecipeID(Integer recipeId);
    List<RecipeVersion> findByForkedRecipe_RecipeID(Integer recipeId);
}