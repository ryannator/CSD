package csd.tariff.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.UserRepository;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

  private final UserRepository users;

  public UserController(UserRepository users) {
    this.users = users;
  }

  public record UserSummary(Long id, String username, String email, User.Role role) {}

  @GetMapping
  public ResponseEntity<List<UserSummary>> getAllUsers() {
    List<UserSummary> result = users.findAll().stream()
        .map(user -> new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
        .toList();
    return ResponseEntity.ok(result);
  }
}