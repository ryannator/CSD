package csd.tariff.backend.dto;

import csd.tariff.backend.model.User;

/**
 * Data Transfer Objects for authentication operations
 */
public class RegistrationDTOs {
    
    /**
     * Request DTO for user sign in
     */
    public record SigninRequest(String email, String password) {}
    
    /**
     * Response DTO for sign in response containing JWT token
     */
    public record SigninResponse(String token) {}
    
    /**
     * Request DTO for user registration
     */
    public record RegisterRequest(String username, String email, String password) {}
    
    /**
     * Response DTO for user information
     */
    public record UserResponse(Long id, String username, String email, User.Role role) {}
    
    /**
     * Response DTO for registration response containing user information
     */
    public record RegisterResponse(UserResponse user) {}
}
