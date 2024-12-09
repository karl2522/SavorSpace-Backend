package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.Report;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.ReportRepository;
import com.example.usersavorspace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public ReportService(ReportRepository reportRepository, RecipeRepository recipeRepository, UserRepository userRepository, NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public Report createReport(Integer recipeId, Integer userId, String reason) {
        // Check if user has already reported this recipe
        if (reportRepository.existsByRecipe_RecipeIDAndReportedBy_Id(recipeId, userId)) {
            throw new RuntimeException("You have already reported this recipe");
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = new Report();
        report.setRecipe(recipe);
        report.setReportedBy(user);
        report.setReason(reason);
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus("PENDING");

        notificationService.createReportNotification(
                recipe.getUser(),
                recipe,
                user.getUsername()
        );

        return reportRepository.save(report);
    }

    public List<Report> getReportsByRecipe(Integer recipeId) {
        return reportRepository.findByRecipe_RecipeID(recipeId);
    }

    public List<Report> getReportsByUser(Integer userId) {
        return reportRepository.findByReportedBy_Id(userId);
    }

    public Report updateReportStatus(Integer reportId, String status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(status);
        return reportRepository.save(report);
    }

    public void deleteReport(Integer reportId) {
        reportRepository.deleteById(reportId);
    }

    public List<Report> getAllPendingReports() {
        // Use the new repository method
        return reportRepository.findAllPendingReportsWithRecipe();
    }

    @Transactional
    public void deleteReportsByRecipeId(Integer recipeId) {
        reportRepository.deleteByRecipe_RecipeID(recipeId);
    }

    @Transactional
    public void handleRecipeDelete(Integer reportId, Integer recipeId) {
        try {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new RuntimeException("Report not found"));
            report.setStatus("DELETE");
            reportRepository.save(report);

            reportRepository.deleteByRecipe_RecipeID(recipeId);

            recipeRepository.deleteById(recipeId);
        } catch (Exception e) {
            throw new RuntimeException("Error in delete process: " + e.getMessage());
        }
    }

}