package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByRecipientIdAndIsReadFalse(Integer userId);
}
