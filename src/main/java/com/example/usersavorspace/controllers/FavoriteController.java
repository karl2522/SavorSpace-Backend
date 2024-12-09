package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.FavoriteDTO;
import com.example.usersavorspace.entities.Favorite;
import com.example.usersavorspace.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:5173")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    private FavoriteDTO convertToDTO(Favorite favorite) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId());
        dto.setUserId(favorite.getUser().getId());
        dto.setRecipeId(favorite.getRecipe().getRecipeID());
        dto.setRecipeTitle(favorite.getRecipe().getTitle());
        dto.setRecipeImageUrl(favorite.getRecipe().getImageURL());
        dto.setCreatedAt(favorite.getCreatedAt());
        return dto;
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Integer recipeId,
            @RequestAttribute("userId") Integer userId) {
        boolean added = favoriteService.addFavorite(userId, recipeId);
        return ResponseEntity.ok(Map.of("success", added));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Integer recipeId,
            @RequestAttribute("userId") Integer userId) {
        favoriteService.removeFavorite(userId, recipeId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/check/{recipeId}")
    public ResponseEntity<?> checkFavorite(
            @PathVariable Integer recipeId,
            @RequestAttribute("userId") Integer userId) {
        boolean isFavorite = favoriteService.isFavorite(userId, recipeId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    @GetMapping("/user")
    public ResponseEntity<List<FavoriteDTO>> getUserFavorites(
            @RequestAttribute("userId") Integer userId) {
        List<Favorite> favorites = favoriteService.getUserFavorites(userId);
        List<FavoriteDTO> favoriteDTOs = favorites.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favoriteDTOs);
    }
}