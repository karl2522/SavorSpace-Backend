package com.example.usersavorspace.controllers;

import com.example.usersavorspace.entities.Admin;
import com.example.usersavorspace.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/admins") // Base URL for admin-related requests
public class AdminController {
    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody Admin adminDetails) {
        Admin admin = adminService.getAdminByEmail(adminDetails.getEmail());
        if (admin != null && admin.getPassword().equals(adminDetails.getPassword())) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }
    // Create a new admin
    @PostMapping("/register") // Endpoint to register a new admin
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }
    // Get all admins
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }
    // Get a specific admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }
    // Update an existing admin
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin adminDetails) {
        Admin updatedAdmin = adminService.updateAdmin(id, adminDetails);
        return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
    }
    // Delete an admin by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}