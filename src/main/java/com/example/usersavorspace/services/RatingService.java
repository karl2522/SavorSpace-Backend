package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Rating;
import com.example.usersavorspace.repositories.RatingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(Long ratingID) {
        return ratingRepository.findById(ratingID);
    }

    public Rating addRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating updateRating(Long ratingID, Rating updatedRating) {
        if (!ratingRepository.existsById(ratingID)) {
            throw new EntityNotFoundException("Rating not found with id " + ratingID);
        }
        updatedRating.setRatingID(ratingID);
        return ratingRepository.save(updatedRating);
    }

    public void deleteRating(Long ratingID) {
        ratingRepository.deleteById(ratingID);
    }
}
