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
            @RequestParam Integer recipeID,
            @RequestParam int rating) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Integer userId = ((User) userDetails).getId();

        RatingDTO ratingDTO = ratingService.addOrUpdateRating(userId, recipeID, rating);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/recipe/{recipeId}/user/{userId}")
    public ResponseEntity<RatingDTO> getRating(
            @PathVariable Integer recipeID,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((User) userDetails).getId();
        RatingDTO ratingDTO = ratingService.getRatingForRecipe(userId, recipeID);
        return ResponseEntity.ok(ratingDTO);
    }
}
