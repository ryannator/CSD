package csd.tariff.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.tariff.backend.model.Country;
import csd.tariff.backend.repository.CountryRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Country createCountry(Country countryRequest) {
        if (countryRequest == null) {
            throw new IllegalArgumentException("Country payload is required.");
        }

        String name = nullIfBlank(countryRequest.getCountryName());
        if (name == null) {
            throw new IllegalArgumentException("Country name is required.");
        }

        String code = nullIfBlank(countryRequest.getCountryCode());
        if (code == null) {
            throw new IllegalArgumentException("Country code is required.");
        }

        String normalizedCode = code.toUpperCase(Locale.ROOT);
        countryRepository
            .findByCountryCode(normalizedCode)
            .ifPresent(existing -> {
                throw new IllegalStateException("A country with this code already exists.");
            });

        Country country = new Country();
        country.setCountryName(name);
        country.setCountryCode(normalizedCode);
        country.setCountryNameShort(nullIfBlank(countryRequest.getCountryNameShort()));
        country.setRegion(nullIfBlank(countryRequest.getRegion()));
        country.setContinent(nullIfBlank(countryRequest.getContinent()));

        String currency = nullIfBlank(countryRequest.getCurrency());
        country.setCurrency(currency != null ? currency.toUpperCase(Locale.ROOT) : null);
        country.setUpdatedAt(LocalDateTime.now());

        return countryRepository.save(country);
    }

    @Override
    public Country updateCountry(Long id, Country countryRequest) {
        if (id == null) {
            throw new IllegalArgumentException("Country ID is required.");
        }

        Country existing = countryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Country not found with ID: " + id));

        if (countryRequest == null) {
            throw new IllegalArgumentException("Country payload is required.");
        }

        String name = nullIfBlank(countryRequest.getCountryName());
        if (name == null) {
            throw new IllegalArgumentException("Country name is required.");
        }

        String code = nullIfBlank(countryRequest.getCountryCode());
        if (code == null) {
            throw new IllegalArgumentException("Country code is required.");
        }

        String normalizedCode = code.toUpperCase(Locale.ROOT);
        countryRepository
            .findByCountryCode(normalizedCode)
            .filter(found -> !found.getId().equals(id))
            .ifPresent(found -> {
                throw new IllegalStateException("A country with this code already exists.");
            });

        existing.setCountryName(name);
        existing.setCountryCode(normalizedCode);
        existing.setCountryNameShort(nullIfBlank(countryRequest.getCountryNameShort()));
        existing.setRegion(nullIfBlank(countryRequest.getRegion()));
        existing.setContinent(nullIfBlank(countryRequest.getContinent()));

        String currency = nullIfBlank(countryRequest.getCurrency());
        existing.setCurrency(currency != null ? currency.toUpperCase(Locale.ROOT) : null);
        existing.setUpdatedAt(LocalDateTime.now());

        return countryRepository.save(existing);
    }

    @Override
    public void deleteCountry(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Country ID is required.");
        }

        Country existing = countryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Country not found with ID: " + id));

        countryRepository.delete(existing);
    }

    private String nullIfBlank(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
