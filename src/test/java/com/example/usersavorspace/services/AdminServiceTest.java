package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Admin;
import com.example.usersavorspace.repositories.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAdmins() {
        Admin admin1 = new Admin();
        admin1.setAdminId(1L);
        admin1.setEmail("admin1@example.com");
        admin1.setPassword("password1");
        admin1.setUsername("admin1");

        Admin admin2 = new Admin();
        admin2.setAdminId(2L);
        admin2.setEmail("admin2@example.com");
        admin2.setPassword("password2");
        admin2.setUsername("admin2");

        List<Admin> admins = Arrays.asList(admin1, admin2);

        when(adminRepository.findAll()).thenReturn(admins);

        List<Admin> result = adminService.getAllAdmins();
        assertEquals(2, result.size());
        verify(adminRepository, times(1)).findAll();
    }

    @Test
    void testGetAdminById() {
        Admin admin = new Admin();
        admin.setAdminId(1L);
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setUsername("admin");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

       /* Optional<Admin> result = adminService.getAdminById(1L);
        assertTrue(result.isPresent());
        assertEquals("admin@example.com", result.get().getEmail());
        verify(adminRepository, times(1)).findById(1L);*/
    }

    @Test
    void testCreateAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setUsername("admin");

        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = adminService.createAdmin(admin);
        assertEquals("admin@example.com", result.getEmail());
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void testUpdateAdmin() {
        Admin admin = new Admin();
        admin.setAdminId(1L);
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setUsername("admin");

        Admin updatedAdmin = new Admin();
        updatedAdmin.setEmail("updated@example.com");
        updatedAdmin.setPassword("updatedPassword");
        updatedAdmin.setUsername("updatedAdmin");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = adminService.updateAdmin(1L, updatedAdmin);
        assertEquals("updated@example.com", result.getEmail());
        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void testDeleteAdmin() {
        Admin admin = new Admin();
        admin.setAdminId(1L);
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setUsername("admin");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        doNothing().when(adminRepository).delete(admin);

        adminService.deleteAdmin(1L);
        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, times(1)).delete(admin);
    }
}