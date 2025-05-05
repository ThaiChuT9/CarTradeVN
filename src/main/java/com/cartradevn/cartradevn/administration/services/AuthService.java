package com.cartradevn.cartradevn.administration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cartradevn.cartradevn.administration.controller.UserResponseDTO;
import com.cartradevn.cartradevn.administration.dto.LoginDTO;
import com.cartradevn.cartradevn.administration.dto.RegisterDTO;
import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;

import lombok.extern.java.Log;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponseDTO convertToResponeDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().toString());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public UserResponseDTO register(RegisterDTO registerDTO) {
        // Check if the username or email already exists
        if (userRepo.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepo.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Create a new user entity and set its properties
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());
        user.setCreatedAt(System.currentTimeMillis()); // Set the createdAt timestamp

        // Save the user to the database
        User savedUser = userRepo.save(user);
        return convertToResponeDTO(savedUser);
    }

    public UserResponseDTO login(LoginDTO loginDTO) {
        // Find the user by username
        User user = userRepo.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("Tên đăng nhập không tồn tại");
        }

        // Check if the password is correct
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        return convertToResponeDTO(user);
    }
}
