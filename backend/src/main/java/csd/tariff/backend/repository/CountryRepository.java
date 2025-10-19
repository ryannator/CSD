package csd.tariff.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import csd.tariff.backend.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    
    // Find by country code
    Optional<Country> findByCountryCode(String countryCode);
    
    // Find by country name
    Optional<Country> findByCountryName(String countryName);
    
    // Search by country name
    @Query("SELECT c FROM Country c WHERE LOWER(c.countryName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Country> findByCountryNameContainingIgnoreCase(@Param("name") String name);
    
    // Find by region
    List<Country> findByRegion(String region);
    
    // Find by continent
    List<Country> findByContinent(String continent);
    
    // Find countries with trade agreements
    @Query("SELECT DISTINCT c FROM Country c JOIN c.agreementParticipants ap")
    List<Country> findCountriesWithTradeAgreements();
    
    // Find countries by agreement code
    @Query("SELECT DISTINCT c FROM Country c JOIN c.agreementParticipants ap JOIN ap.agreement ta WHERE ta.agreementCode = :agreementCode")
    List<Country> findCountriesByAgreementCode(@Param("agreementCode") String agreementCode);
    
    // Count total countries
    long count();
}