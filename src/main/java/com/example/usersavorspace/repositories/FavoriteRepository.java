package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Favorite;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    Optional<Favorite> findByUserAndRecipe(User user, Recipe recipe);
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    void deleteByUserAndRecipe(User user, Recipe recipe);
}