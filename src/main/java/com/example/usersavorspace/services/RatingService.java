package com.example.usersavorspace.services;

import com.example.usersavorspace.dtos.RatingDTO;
import com.example.usersavorspace.entities.Rating;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.RatingRepository;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public RatingDTO addOrUpdateRating(Integer userId, Integer recipeId, int rating) {
        // Validate rating value
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Find or create rating
        Rating ratingEntity = ratingRepository
                .findByUserIdAndRecipeRecipeID(userId, recipeId)
                .orElse(new Rating());

        boolean isNewRating = ratingEntity.getRatingID() == null;

        // Get user and recipe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with ID: " + recipeId));

        // Set rating properties
        ratingEntity.setUser(user);
        ratingEntity.setRecipe(recipe);
        ratingEntity.setRating(rating);

        // Save rating
        ratingEntity = ratingRepository.save(ratingEntity);

        // Get updated statistics
        Double averageRating = ratingRepository.getAverageRatingForRecipe(recipeId);
        Integer totalRatings = ratingRepository.countByRecipeRecipeID(recipeId);

        // Update recipe's average rating
        recipe.setAverageRating(averageRating != null ? averageRating : 0.0);
        recipeRepository.save(recipe);

        if(isNewRating && !user.getId().equals(recipe.getUser().getId())) {
            notificationService.createRatingNotification(recipe.getUser(), recipe, user.getFullName());
        }

        // Create and return DTO
        return createRatingDTO(ratingEntity, averageRating, totalRatings);
    }

    public RatingDTO getRatingForRecipe(Integer recipeId, Integer userId) {
        Rating rating = ratingRepository
                .findByUserIdAndRecipeRecipeID(userId, recipeId)
                .orElse(null);

        Double averageRating = ratingRepository.getAverageRatingForRecipe(recipeId);
        Integer totalRatings = ratingRepository.countByRecipeRecipeID(recipeId);

        return createRatingDTO(rating, averageRating, totalRatings);
    }

    private RatingDTO createRatingDTO(Rating rating, Double averageRating, Integer totalRatings) {
        RatingDTO dto = new RatingDTO();
        if (rating != null) {
            dto.setRatingID(rating.getRatingID());
            dto.setRecipeID(rating.getRecipe().getRecipeID());
            dto.setUserID(rating.getUser().getId());
            dto.setRating(rating.getRating());
        }
        dto.setAverageRating(averageRating != null ? averageRating : 0.0);
        dto.setTotalRatings(totalRatings != null ? totalRatings : 0);
        return dto;
    }
}
