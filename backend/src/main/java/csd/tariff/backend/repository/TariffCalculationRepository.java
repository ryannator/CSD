package csd.tariff.backend.repository;

import csd.tariff.backend.model.TariffCalculation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffCalculationRepository extends JpaRepository<TariffCalculation, Long> {

  /** Find tariff calculations by HTS code */
  List<TariffCalculation> findByHtsCode(String htsCode);

  /** Find tariff calculations by country code */
  List<TariffCalculation> findByCountryCode(String countryCode);

  /** Find tariff calculations by HTS code and country code */
  List<TariffCalculation> findByHtsCodeAndCountryCode(String htsCode, String countryCode);

  /** Find tariff calculations by calculation type */
  List<TariffCalculation> findByCalculationType(String calculationType);

  /** Count tariff calculations by HTS code */
  long countByHtsCode(String htsCode);

  /** Count tariff calculations by country code */
  long countByCountryCode(String countryCode);
}
