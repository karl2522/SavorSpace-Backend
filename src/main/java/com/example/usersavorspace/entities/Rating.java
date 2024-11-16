package com.example.usersavorspace.entities;

import jakarta.persistence.*;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingID;

    @ManyToOne
    @JoinColumn(name = "recipeid", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    private int rating;

    // Getters and Setters
    public Long getRatingID() {
        return ratingID;
    }

    public void setRatingID(Long ratingID) {
        this.ratingID = ratingID;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
