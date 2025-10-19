package csd.tariff.backend.service;

import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class EmailUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public EmailUserDetailsService(UserRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
        return org.springframework.security.core.userdetails.User
            .withUsername(u.getEmail())                  
            .password(u.getPassword())                   
            .roles(u.getRole().name())                   
            .build();
    }
}
