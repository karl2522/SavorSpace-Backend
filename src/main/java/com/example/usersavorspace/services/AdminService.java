package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Admin;
import com.example.usersavorspace.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }

    public Admin updateAdmin(Long adminId, Admin adminDetails) {
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin != null) {
            admin.setUsername(adminDetails.getUsername());
            admin.setEmail(adminDetails.getEmail());
            admin.setPassword(adminDetails.getPassword());
            return adminRepository.save(admin);
        }
        return null;
    }

    public void deleteAdmin(Long adminId) {
        adminRepository.deleteById(adminId);
    }

    public Admin getAdminByEmail(String email) {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        return adminOptional.orElse(null);
    }
}