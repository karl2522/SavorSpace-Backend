package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByStatus(String status);
    List<Report> findByRecipe_RecipeID(Integer recipeId);
    List<Report> findByReportedBy_Id(Integer userId);
    boolean existsByRecipe_RecipeIDAndReportedBy_Id(Integer recipeId, Integer userId);
    void deleteByRecipe_RecipeID(Integer recipeId);
    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.recipe WHERE r.status = 'PENDING'")
    List<Report> findAllPendingReportsWithRecipe();
}