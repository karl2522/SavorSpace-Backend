package com.example.usersavorspace.dtos;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long commentID;
    private Integer recipeID;
    private Integer userID;
    private String username;
    private String userEmail;
    private String userImageURL;
    private String content;
    private LocalDateTime createdAt;
    private Boolean flagged;

    // Getters and Setters
    public Long getCommentID() { return commentID; }
    public void setCommentID(Long commentID) { this.commentID = commentID; }

    public Integer getRecipeID() { return recipeID; }
    public void setRecipeID(Integer recipeID) { this.recipeID = recipeID; }

    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserImageURL() { return userImageURL; }
    public void setUserImageURL(String userImageURL) { this.userImageURL = userImageURL; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getFlagged() { return flagged; }
    public void setFlagged(Boolean flagged) { this.flagged = flagged; }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "commentID=" + commentID +
                ", username='" + username + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
