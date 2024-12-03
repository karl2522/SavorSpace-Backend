package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Comment;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRecipeOrderByCreatedAtDesc(Recipe recipe);
    List<Comment> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT c FROM Comment c WHERE c.recipe.recipeID = :recipeId ORDER BY c.createdAt DESC")
    List<Comment> findByRecipeIdOrderByCreatedAtDesc(@Param("recipeId") Integer recipeId);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Comment> findByUserIdAndDeletedFalse(Integer userId);
    List<Comment> findByUserId(Integer userId);
    List<Comment> findByDeletedFalse();
    long countByUserId(Integer userId);

    // Replace findByIsFlaggedTrue with a query that checks flaggedByUsers
    @Query("SELECT c FROM Comment c WHERE SIZE(c.flaggedByUsers) > 0")
    List<Comment> findByIsFlaggedTrue();

    // Add useful queries for the new flag system
    @Query("SELECT c FROM Comment c WHERE :user MEMBER OF c.flaggedByUsers")
    List<Comment> findCommentsFlaggedByUser(@Param("user") User user);

    @Query("SELECT COUNT(u) FROM Comment c JOIN c.flaggedByUsers u WHERE c.commentID = :commentId")
    int countFlagsByCommentId(@Param("commentId") Long commentId);
}