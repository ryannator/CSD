package csd.tariff.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;
import csd.tariff.backend.service.JwtService; 

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwt;

  public AuthController(AuthenticationManager authManager, JwtService jwt) {
    this.authManager = authManager; this.jwt = jwt;
  }

  public record SigninRequest(String email, String password) {}
  public record SigninResponse(String token) {}

  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody SigninRequest req) {
    try {
      Authentication auth = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.email(), req.password()));
      String token = jwt.generate(auth.getName(), auth.getAuthorities());
      return ResponseEntity.ok(new SigninResponse(token));
    } catch (AuthenticationException ex) {
      return ResponseEntity.status(401).body(java.util.Map.of("error","Invalid credentials"));
    }
  }
}