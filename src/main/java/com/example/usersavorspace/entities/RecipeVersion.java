package com.example.usersavorspace.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_versions")
public class RecipeVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "original_recipe_id")
    private Recipe originalRecipe;

    @ManyToOne
    @JoinColumn(name = "forked_recipe_id")
    private Recipe forkedRecipe;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime forkedAt;

    private String changeDescription;

    @Column(name = "version_number")
    private String versionNumber;

    // Add getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Recipe getOriginalRecipe() {
        return originalRecipe;
    }

    public void setOriginalRecipe(Recipe originalRecipe) {
        this.originalRecipe = originalRecipe;
    }

    public Recipe getForkedRecipe() {
        return forkedRecipe;
    }

    public void setForkedRecipe(Recipe forkedRecipe) {
        this.forkedRecipe = forkedRecipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getForkedAt() {
        return forkedAt;
    }

    public void setForkedAt(LocalDateTime forkedAt) {
        this.forkedAt = forkedAt;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
}
