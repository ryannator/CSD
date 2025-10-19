package csd.tariff.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.service.TariffCalculationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tariff")
@CrossOrigin(origins = "*")
public class TariffCalculationController {
    
    @Autowired
    private TariffCalculationService TariffCalculationService;


    
    // ===== CRUD OPERATIONS FOR TARIFF CALCULATIONS =====
    
    /**
     * Create a new tariff calculation
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> createTariffCalculation(@Valid @RequestBody TariffCalculationRequest request) {
        try {
            TariffCalculationResponse response = TariffCalculationService.createTariffCalculation(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", "Invalid request: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get tariff calculation by ID
     */
    @GetMapping("/calculate/{id}")
    public ResponseEntity<?> getTariffCalculationById(@PathVariable Long id) {
        Optional<TariffCalculation> result = TariffCalculationService.getTariffCalculationById(id);
        
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            Map<String, Object> error = Map.of("error", "Tariff calculation not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all tariff calculations
     */
    @GetMapping("/calculate-all")
    public ResponseEntity<List<TariffCalculation>> getAllTariffCalculations() {
        List<TariffCalculation> calculations = TariffCalculationService.getAllTariffCalculations();
        return ResponseEntity.ok(calculations);
    }
    
    /**
     * Update tariff calculation
     */
    @PutMapping("/calculate/{id}")
    public ResponseEntity<?> updateTariffCalculation(
            @PathVariable Long id, 
            @Valid @RequestBody TariffCalculationRequest request) {
        try {
            TariffCalculationResponse response = TariffCalculationService.updateTariffCalculation(id, request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", "Invalid request: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Delete tariff calculation by ID
     */
    @DeleteMapping("/calculate/{id}")
    public ResponseEntity<Map<String, Object>> deleteTariffCalculation(@PathVariable Long id) {
        boolean deleted = TariffCalculationService.deleteTariffCalculation(id);
        
        if (deleted) {
            Map<String, Object> result = Map.of("message", "Tariff calculation deleted successfully", "id", id);
            return ResponseEntity.ok(result);
        } else {
            Map<String, Object> error = Map.of("error", "Tariff calculation not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

}