package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.RatingDTO;
import com.example.usersavorspace.entities.Rating;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PostMapping("/rate")
    public ResponseEntity<RatingDTO> rateRecipe(
            @RequestParam Integer recipeId,  // Changed from recipeID to match service
            @RequestParam int rating,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RatingDTO ratingDTO = ratingService.addOrUpdateRating(user.getId(), recipeId, rating);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<RatingDTO> getRating(
            @PathVariable Integer recipeId,  // Changed from recipeID
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RatingDTO ratingDTO = ratingService.getRatingForRecipe(recipeId, user.getId());
        return ResponseEntity.ok(ratingDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}