package com.taskmang.service;

import java.util.List;
import java.util.Optional;

import com.taskmang.dto.request.SignupRequest;
import com.taskmang.dto.response.UserResponse;
import com.taskmang.entity.User;

public interface UserService {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	UserResponse registerUser(SignupRequest signUpRequest);
	UserResponse updateUser(Long userId, SignupRequest signUpRequest);
	void deleteUser(Long userId);
	void deactivateUser(Long userId);
	void activateUser(Long userId);
	List<UserResponse> getAllUsers();
}
