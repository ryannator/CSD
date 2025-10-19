package csd.tariff.backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.tariff.backend.model.Country;
import csd.tariff.backend.service.CountryService;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/country")
@CrossOrigin(origins = "*")
public class CountryController {
    
    @Autowired
    private CountryService CountryService;
    
    
    /**
     * Get all countries
     */
    @GetMapping("")
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = CountryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * Create a new country entry
     */
    @PostMapping("")
    public ResponseEntity<?> createCountry(@Valid @RequestBody Country country) {
        try {
            Country created = CountryService.createCountry(country);
            return ResponseEntity.created(URI.create("/country/" + created.getId())).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /**
     * Update an existing country entry
     */
     @PutMapping("/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable Long id, @Valid @RequestBody Country country) {
        try {
            Country updated = CountryService.updateCountry(id, country);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /**
     * Delete a country entry
     */
     @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable Long id) {
        try {
            CountryService.deleteCountry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}