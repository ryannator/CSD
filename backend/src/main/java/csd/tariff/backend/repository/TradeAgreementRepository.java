package csd.tariff.backend.repository;

import csd.tariff.backend.model.TradeAgreement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeAgreementRepository extends JpaRepository<TradeAgreement, Long> {

  // Find by agreement code
  Optional<TradeAgreement> findByAgreementCode(String agreementCode);

  // Find by agreement name
  Optional<TradeAgreement> findByAgreementName(String agreementName);

  // Find by agreement type
  List<TradeAgreement> findByAgreementType(String agreementType);

  // Find multilateral agreements
  List<TradeAgreement> findByIsMultilateralTrue();

  // Find bilateral agreements
  List<TradeAgreement> findByIsMultilateralFalse();

  // Find active agreements
  @Query(
      "SELECT ta FROM TradeAgreement ta WHERE ta.effectiveDate <= CURRENT_DATE AND (ta.expirationDate IS NULL OR ta.expirationDate >= CURRENT_DATE)")
  List<TradeAgreement> findActiveAgreements();

  // Find agreements by country
  @Query(
      "SELECT DISTINCT ta FROM TradeAgreement ta JOIN ta.participants ap JOIN ap.country c WHERE c.countryCode = :countryCode")
  List<TradeAgreement> findByParticipatingCountry(@Param("countryCode") String countryCode);

  // Find agreements between two countries
  @Query(
      "SELECT DISTINCT ta FROM TradeAgreement ta WHERE ta.id IN ("
          + "SELECT ap1.agreement.id FROM AgreementParticipant ap1 WHERE ap1.country.countryCode = :country1Code"
          + ") AND ta.id IN ("
          + "SELECT ap2.agreement.id FROM AgreementParticipant ap2 WHERE ap2.country.countryCode = :country2Code"
          + ")")
  List<TradeAgreement> findBetweenCountries(
      @Param("country1Code") String country1Code, @Param("country2Code") String country2Code);

  // Find US trade agreements
  @Query(
      "SELECT DISTINCT ta FROM TradeAgreement ta JOIN ta.participants ap JOIN ap.country c WHERE c.countryCode = 'US'")
  List<TradeAgreement> findUSTradeAgreements();

  // Find agreements by country code (simplified to avoid duplicates)
  @Query(
      "SELECT ta FROM TradeAgreement ta WHERE ta.id IN ("
          + "SELECT DISTINCT ap.agreement.id FROM AgreementParticipant ap WHERE ap.country.countryCode = :countryCode"
          + ")")
  List<TradeAgreement> findByCountryCode(@Param("countryCode") String countryCode);

  // Find agreements effective on a specific date
  @Query(
      "SELECT ta FROM TradeAgreement ta WHERE ta.effectiveDate <= :date AND (ta.expirationDate IS NULL OR ta.expirationDate >= :date)")
  List<TradeAgreement> findEffectiveOnDate(@Param("date") LocalDate date);

  // Find agreements expiring on a specific date
  @Query("SELECT ta FROM TradeAgreement ta WHERE ta.expirationDate = :date")
  List<TradeAgreement> findExpiringOnDate(@Param("date") LocalDate date);

  // Find agreements active between two dates
  @Query(
      "SELECT ta FROM TradeAgreement ta WHERE ta.effectiveDate >= :startDate AND (ta.expirationDate IS NULL OR ta.expirationDate <= :endDate)")
  List<TradeAgreement> findActiveBetweenDates(
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

  // Count total agreements
  long count();
}
