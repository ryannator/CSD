package csd.tariff.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import csd.tariff.backend.model.AgreementParticipant;

@Repository
public interface AgreementParticipantRepository extends JpaRepository<AgreementParticipant, Long> {
    
    // Find participants by agreement ID
    List<AgreementParticipant> findByAgreementId(Long agreementId);
    
    // Find participants by country ID
    List<AgreementParticipant> findByCountryId(Long countryId);
    
    // Find participants by agreement code
    @Query("SELECT ap FROM AgreementParticipant ap JOIN ap.agreement ta WHERE ta.agreementCode = :agreementCode")
    List<AgreementParticipant> findByAgreementCode(@Param("agreementCode") String agreementCode);
    
    // Find participants by country code
    @Query("SELECT ap FROM AgreementParticipant ap JOIN ap.country c WHERE c.countryCode = :countryCode")
    List<AgreementParticipant> findByCountryCode(@Param("countryCode") String countryCode);
    
    // Find specific participant by agreement and country
    @Query("SELECT ap FROM AgreementParticipant ap WHERE ap.agreement.id = :agreementId AND ap.country.id = :countryId")
    Optional<AgreementParticipant> findByAgreementAndCountry(@Param("agreementId") Long agreementId, @Param("countryId") Long countryId);
    
    // Check if participant exists
    boolean existsByAgreementIdAndCountryId(Long agreementId, Long countryId);
}
