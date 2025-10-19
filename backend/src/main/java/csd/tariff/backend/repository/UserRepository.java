package csd.tariff.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import csd.tariff.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
