package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

}

