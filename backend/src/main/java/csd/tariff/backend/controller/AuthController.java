package csd.tariff.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.tariff.backend.dto.RegistrationDTOs.RegisterRequest;
import csd.tariff.backend.dto.RegistrationDTOs.RegisterResponse;
import csd.tariff.backend.dto.RegistrationDTOs.SigninRequest;
import csd.tariff.backend.dto.RegistrationDTOs.SigninResponse;
import csd.tariff.backend.dto.RegistrationDTOs.UserResponse;
import csd.tariff.backend.model.User;
import csd.tariff.backend.service.JwtService;
import csd.tariff.backend.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwt;
  private final UserService userService;

  public AuthController(AuthenticationManager authManager, JwtService jwt, UserService userService) {
    this.authManager = authManager; 
    this.jwt = jwt;
    this.userService = userService;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody SigninRequest req) {
    try {
      Authentication auth =
          authManager.authenticate(
              new UsernamePasswordAuthenticationToken(req.email(), req.password()));
      String token = jwt.generate(auth.getName(), auth.getAuthorities());
      return ResponseEntity.ok(new SigninResponse(token));
    } catch (AuthenticationException ex) {
      return ResponseEntity.status(401).body(java.util.Map.of("error", "Invalid credentials"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    try {
      User savedUser = userService.createUser(
          req.username(),
          req.email(), 
          req.password(), 
          User.Role.USER
      );
      
      UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
      return ResponseEntity.ok(new RegisterResponse(userResponse));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(500).body(java.util.Map.of("error", "Registration failed: " + ex.getMessage()));
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    // For JWT-based authentication, logout is typically handled client-side
    // by removing the token. This endpoint can be used for logging purposes
    // or to invalidate tokens if using a token blacklist.
    return ResponseEntity.ok(java.util.Map.of("message", "Logged out successfully"));
  }
}