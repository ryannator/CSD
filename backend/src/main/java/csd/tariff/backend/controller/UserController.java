package csd.tariff.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.tariff.backend.model.User;
import csd.tariff.backend.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  public record UserSummary(Long id, String username, String email, User.Role role) {}
  public record CreateUserRequest(String username, String email, String password, User.Role role) {}
  public record UpdateUserRequest(String username, String password, User.Role role) {}
  
  // Profile management DTOs
  public record UserResponse(Long id, String username, String email, User.Role role) {}
  public record UpdateProfileRequest(String username) {}
  public record ChangePasswordRequest(String currentPassword, String newPassword) {}

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserSummary>> getAllUsers() {
    List<UserSummary> result = userService.getAllUsers().stream()
        .map(user -> new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
        .toList();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserSummary> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    if (user.isPresent()) {
      User u = user.get();
      return ResponseEntity.ok(new UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.getRole()));
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest req) {
    try {
      User savedUser = userService.createUser(
          req.username(),
          req.email(), 
          req.password(), 
          req.role()
      );
      return ResponseEntity.ok(new UserSummary(savedUser.getId(), savedUser.getUsername(), 
                                               savedUser.getEmail(), savedUser.getRole()));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to create user: " + ex.getMessage()));
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
    try {
      User savedUser;
      
      // If password is provided, use full update method
      if (req.password() != null && !req.password().trim().isEmpty()) {
        savedUser = userService.updateUserUsernameWithPassword(
            id, 
            req.username(),
            req.password(), 
            req.role()
        );
      } else {
        // If no password provided, use basic update method
        savedUser = userService.updateUserUsername(id, req.username(), req.role());
      }
      
      return ResponseEntity.ok(new UserSummary(savedUser.getId(), savedUser.getUsername(), 
                                               savedUser.getEmail(), savedUser.getRole()));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to update user: " + ex.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.ok(java.util.Map.of("message", "User deleted successfully"));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(404).body(java.util.Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to delete user: " + ex.getMessage()));
    }
  }

  // Profile management endpoints (for authenticated users)
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Authentication auth) {
    try {
      User user = userService.getUserByEmail(auth.getName())
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
      return ResponseEntity.ok(userResponse);
    } catch (Exception ex) {
      return ResponseEntity.status(404).body(java.util.Map.of("error", "User not found"));
    }
  }

  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
    try {
      User savedUser = userService.updateProfile(auth.getName(), req.username());
      UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
      return ResponseEntity.ok(userResponse);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to update profile: " + ex.getMessage()));
    }
  }

  @PutMapping("/change-password")
  public ResponseEntity<?> changePassword(Authentication auth, @Valid @RequestBody ChangePasswordRequest req) {
    try {
      userService.changePassword(auth.getName(), req.currentPassword(), req.newPassword());
      return ResponseEntity.ok(java.util.Map.of("message", "Password updated successfully"));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to change password: " + ex.getMessage()));
    }
  }

  @DeleteMapping("/account")
  public ResponseEntity<?> deleteAccount(Authentication auth) {
    try {
      userService.deleteAccount(auth.getName());
      return ResponseEntity.ok(java.util.Map.of("message", "Account deleted successfully"));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to delete account: " + ex.getMessage()));
    }
  }
}