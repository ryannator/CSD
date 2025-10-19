package csd.tariff.backend.service;
import java.util.List;

import csd.tariff.backend.model.Country;

public interface CountryService {
    
    /**
     * Get all countries
    */
    List<Country> getAllCountries();

    /**
     * Create a new country entry.
     */
    Country createCountry(Country country);

    /**
     * Update an existing country.
     */
    Country updateCountry(Long id, Country country);

    /**
     * Delete an existing country by id.
     */
    void deleteCountry(Long id);
}
