package com.taskmang.service.impl;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmang.dto.request.SignupRequest;
import com.taskmang.dto.response.UserResponse;
import com.taskmang.entity.Role;
import com.taskmang.entity.User;
import com.taskmang.enums.ERole;
import com.taskmang.exception.BadRequestException;
import com.taskmang.exception.ResourceNotFoundException;
import com.taskmang.repository.RoleRepository;
import com.taskmang.repository.UserRepository;
import com.taskmang.service.AuditLogService;
import com.taskmang.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserResponse registerUser(SignupRequest signUpRequest) {
        if (existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            roles.add(userRole);
        } else {
            signUpRequest.getRoles().forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        auditLogService.logAction("CREATE", "USER", savedUser.getId(), null, 
            userToString(savedUser), "system");

        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, SignupRequest signUpRequest) {
        // Validate input
        if (signUpRequest == null) {
            throw new BadRequestException("User update request cannot be null");
        }

        // Find existing user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Track changes for audit log
        String oldUser = userToString(user);
        boolean changesMade = false;

        // Update username if changed and available
        if (signUpRequest.getUsername() != null && !signUpRequest.getUsername().equals(user.getUsername())) {
            if (existsByUsername(signUpRequest.getUsername())) {
                throw new BadRequestException("Username is already taken");
            }
            user.setUsername(signUpRequest.getUsername());
            changesMade = true;
        }

        // Update email if changed and available
        if (signUpRequest.getEmail() != null && !signUpRequest.getEmail().equals(user.getEmail())) {
            if (existsByEmail(signUpRequest.getEmail())) {
                throw new BadRequestException("Email is already in use");
            }
            user.setEmail(signUpRequest.getEmail());
            changesMade = true;
        }

        // Update password if provided
        if (signUpRequest.getPassword() != null && !signUpRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            changesMade = true;
        }

        // Update roles if provided
        if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            signUpRequest.getRoles().forEach(role -> {
                switch (role.toLowerCase()) { // Case-insensitive check
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found"));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));
                        roles.add(userRole);
                }
            });
            user.setRoles(roles);
            changesMade = true;
        }

        // Only save and audit if changes were made
        if (changesMade) {
            User updatedUser = userRepository.save(user);
            auditLogService.logAction("UPDATE", "USER", userId, oldUser, 
                userToString(updatedUser), "system");
            return modelMapper.map(updatedUser, UserResponse.class);
        }

        // Return existing user if no changes
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        auditLogService.logAction("DELETE", "USER", userId, 
            userToString(user), null, "system");

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String oldUser = userToString(user);
        user.setActive(false);
        User updatedUser = userRepository.save(user);

        auditLogService.logAction("DEACTIVATE", "USER", userId, 
            oldUser, userToString(updatedUser), "system");
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String oldUser = userToString(user);
        user.setActive(true);
        User updatedUser = userRepository.save(user);

        auditLogService.logAction("ACTIVATE", "USER", userId, 
            oldUser, userToString(updatedUser), "system");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    private String userToString(User user) {
        return String.format("User[id=%d, username='%s', email='%s', active=%b]", 
                user.getId(), user.getUsername(), user.getEmail(), user.getActive());
    }
}