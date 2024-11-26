package com.example.usersavorspace.services;

import com.example.usersavorspace.dtos.CommentDTO;
import com.example.usersavorspace.entities.Comment;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.exceptions.ResourceNotFoundException;
import com.example.usersavorspace.exceptions.UnauthorizedException;
import com.example.usersavorspace.repositories.CommentRepository;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<CommentDTO> getCommentsByRecipe(Integer recipeId) {
        try {
            //System.out.println("Looking up recipe with ID: " + recipeId);

            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

            //System.out.println("Found recipe, fetching comments");

            List<Comment> comments = commentRepository.findByRecipeOrderByCreatedAtDesc(recipe);
            //System.out.println("Found " + comments.size() + " comments");

            return comments.stream()
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error in getCommentsByRecipe: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public CommentDTO createComment(Integer recipeId, String content, Authentication authentication) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User user = userRepository.findByEmailAndDeletedFalse(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setRecipe(recipe);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setFlagged(false);

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        try {
            CommentDTO dto = new CommentDTO();
            dto.setCommentID(comment.getCommentID());
            dto.setContent(comment.getContent());
            dto.setCreatedAt(comment.getCreatedAt());

            if (comment.getRecipe() != null) {
                dto.setRecipeID(comment.getRecipe().getRecipeID());
            }

            if (comment.getUser() != null) {
                User user = comment.getUser();
                dto.setUserID(user.getId());
                dto.setUsername(user.getFullName()); // Use fullName instead of email
                dto.setUserEmail(user.getEmail());
                dto.setUserImageURL(user.getImageURL());
            }

            dto.setFlagged(Boolean.valueOf(comment.getFlagged()));

            // Debug logging
            /*System.out.println("Converting Comment to DTO:");
            System.out.println("CommentID: " + dto.getCommentID());
            System.out.println("Username: " + dto.getUsername());
            System.out.println("UserEmail: " + dto.getUserEmail());
            System.out.println("UserImageURL: " + dto.getUserImageURL());
            System.out.println("CreatedAt: " + dto.getCreatedAt());*/

            return dto;
        } catch (Exception e) {
            System.err.println("Error converting comment: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void deleteComment(Long commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Get the current user
        User currentUser = userRepository.findByEmailAndDeletedFalse(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the current user is the owner of the comment
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        commentRepository.deleteById(commentId);
    }

    public Comment toggleCommentFlag(Long commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Since we're using primitive boolean now, no need to check for null
        comment.setFlagged(!comment.getFlagged());

        log.info("Toggling flag for comment {}: {} -> {}", commentId, comment.getFlagged(), !comment.getFlagged());

        Comment savedComment = commentRepository.save(comment);

        log.info("Saved comment {} with flag status: {}", savedComment.getCommentID(), savedComment.getFlagged());

        return savedComment;
    }

    public List<CommentDTO> getFlaggedComments() {
        return commentRepository.findByIsFlaggedTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        commentRepository.deleteById(commentId);
    }
}