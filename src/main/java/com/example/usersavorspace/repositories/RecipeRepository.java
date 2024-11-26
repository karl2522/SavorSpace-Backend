package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByUserOrderByCreatedAtDesc(User user);
    Page<Recipe> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<Recipe> findByRecipeIDAndUser(int recipeID, User user);
    boolean existsByRecipeIDAndUser(int recipeID, User user);
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByUserId(Integer userId);

    @Query("SELECT r FROM Recipe r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Recipe> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    @Query("SELECT r FROM Recipe r LEFT JOIN r.comments c " +
            "WHERE r.user.id = :userId " +
            "GROUP BY r " +
            "ORDER BY COUNT(c) DESC")
    List<Recipe> findMostCommentedRecipesByUser(Integer userId, Pageable pageable);
}

