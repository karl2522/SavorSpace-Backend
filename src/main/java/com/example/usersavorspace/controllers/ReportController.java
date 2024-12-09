package com.example.usersavorspace.controllers;

import com.example.usersavorspace.entities.Report;
import com.example.usersavorspace.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<Report> reportRecipe(
            @PathVariable Integer recipeId,
            @RequestAttribute("userId") Integer userId,
            @RequestBody Map<String, String> payload) {

        Report report = reportService.createReport(recipeId, userId, payload.get("reason"));
        return ResponseEntity.ok(report);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<Report>> getReportsByRecipe(@PathVariable Integer recipeId) {
        List<Report> reports = reportService.getReportsByRecipe(recipeId);
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<Report> updateReportStatus(
            @PathVariable Integer reportId,
            @RequestBody Map<String, String> payload) {

        Report report = reportService.updateReportStatus(reportId, payload.get("status"));
        return ResponseEntity.ok(report);
    }
}