package com.example.usersavorspace.controllers;

import com.example.usersavorspace.entities.Rating;
import com.example.usersavorspace.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{ratingID}")
    public Optional<Rating> getRatingById(@PathVariable Long ratingID) {
        return ratingService.getRatingById(ratingID);
    }

    @PostMapping
    public Rating addRating(@RequestBody Rating rating) {
        return ratingService.addRating(rating);
    }

    @PutMapping("/{ratingID}")
    public Rating updateRating(@PathVariable Long ratingID, @RequestBody Rating rating) {
        return ratingService.updateRating(ratingID, rating);
    }

    @DeleteMapping("/{ratingID}")
    public void deleteRating(@PathVariable Long ratingID) {
        ratingService.deleteRating(ratingID);
    }
}
