package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
