package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
