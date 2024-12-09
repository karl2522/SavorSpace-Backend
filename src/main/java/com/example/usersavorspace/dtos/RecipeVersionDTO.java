package com.example.usersavorspace.dtos;

import com.example.usersavorspace.entities.RecipeVersion;

import java.time.LocalDateTime;

public class RecipeVersionDTO {
    private Integer id;
    private Integer originalRecipeId;
    private String originalRecipeTitle;
    private Integer forkedRecipeId;
    private String forkedRecipeTitle;
    private String userName;
    private LocalDateTime forkedAt;
    private String changeDescription;
    private String versionNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOriginalRecipeId() {
        return originalRecipeId;
    }

    public void setOriginalRecipeId(Integer originalRecipeId) {
        this.originalRecipeId = originalRecipeId;
    }

    public String getOriginalRecipeTitle() {
        return originalRecipeTitle;
    }

    public void setOriginalRecipeTitle(String originalRecipeTitle) {
        this.originalRecipeTitle = originalRecipeTitle;
    }

    public Integer getForkedRecipeId() {
        return forkedRecipeId;
    }

    public void setForkedRecipeId(Integer forkedRecipeId) {
        this.forkedRecipeId = forkedRecipeId;
    }

    public String getForkedRecipeTitle() {
        return forkedRecipeTitle;
    }

    public void setForkedRecipeTitle(String forkedRecipeTitle) {
        this.forkedRecipeTitle = forkedRecipeTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public static RecipeVersionDTO fromEntity(RecipeVersion version) {
        RecipeVersionDTO dto = new RecipeVersionDTO();
        dto.setId(version.getId());
        dto.setOriginalRecipeId(version.getOriginalRecipe().getRecipeID());
        dto.setOriginalRecipeTitle(version.getOriginalRecipe().getTitle());
        dto.setForkedRecipeId(version.getForkedRecipe().getRecipeID());
        dto.setForkedRecipeTitle(version.getForkedRecipe().getTitle());
        dto.setUserName(version.getUser().getFullName());
        dto.setForkedAt(version.getForkedAt());
        dto.setChangeDescription(version.getChangeDescription());
        dto.setVersionNumber(version.getVersionNumber());
        return dto;
    }
}