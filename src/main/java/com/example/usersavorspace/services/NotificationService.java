package com.example.usersavorspace.services;


import com.example.usersavorspace.entities.Notification;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(String username, Notification notification) {

        notification = notificationRepository.save(notification);

        // send notification to user
        messagingTemplate.convertAndSendToUser(
                username,
                "/topic/notifications",
                notification
        );
    }

    public void createCommentNotification(User recipient, Recipe recipe, String commenterName) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType("COMMENT");
        notification.setMessage(commenterName + " commented on your recipe: " + recipe.getTitle());
        notification.setRecipeId(recipe.getRecipeID());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        sendNotification(recipient.getUsername(), notification);
    }

    public void createRatingNotification(User recipient, Recipe recipe, String raterName) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType("RATING");
        notification.setMessage(raterName + " rated your recipe: " + recipe.getTitle());
        notification.setRecipeId(recipe.getRecipeID());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        sendNotification(recipient.getUsername(), notification);
    }

    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Integer userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

}
