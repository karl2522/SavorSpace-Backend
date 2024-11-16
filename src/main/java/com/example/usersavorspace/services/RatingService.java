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
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RatingDTO addOrUpdateRating(Integer userId, Integer recipeId, int rating) {

        if(rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Rating ratingEntity = ratingRepository
                .findByUserIdAndRecipeRecipeID(userId, recipeId)
                .orElse(new Rating());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        ratingEntity.setUser(user);
        ratingEntity.setRecipe(recipe);
        ratingEntity.setRating(rating);
        
        ratingEntity = ratingRepository.save(ratingEntity);

        Double averageRating = ratingRepository.getAverageRatingForRecipe(recipeId);
        Integer totalRatings = ratingRepository.countByRecipeRecipeID(recipeId);

        RatingDTO dto = new RatingDTO();
        dto.setRatingID(ratingEntity.getRatingID());
        dto.setRecipeID(recipeId);
        dto.setUserID(userId);
        dto.setAverageRating(averageRating);
        dto.setTotalRatings(totalRatings);

        return dto;
    }

    public RatingDTO getRatingForRecipe(Integer recipeId, Integer userId) {
        Rating rating = ratingRepository
                .findByUserIdAndRecipeRecipeID(userId, recipeId)
                .orElse(null);

        RatingDTO dto = new RatingDTO();
        dto.setRecipeID(recipeId);
        dto.setUserID(userId);

        if(rating != null) {
            dto.setRatingID(rating.getRatingID());
            dto.setRating(rating.getRating());
        }

        dto.setAverageRating(ratingRepository.getAverageRatingForRecipe(recipeId));
        dto.setTotalRatings(ratingRepository.countByRecipeRecipeID(recipeId));

        return dto;
    }
}
