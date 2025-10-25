package csd.tariff.backend.config;

import static org.mockito.Mockito.mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import csd.tariff.backend.service.CurrencyService;
import csd.tariff.backend.service.MfnService;
import csd.tariff.backend.service.ProductService;
import csd.tariff.backend.service.TradeAgreementService;

/**
 * Test configuration for providing mock services during testing
 * This helps avoid dependency issues in unit tests
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CurrencyService currencyService() {
        return mock(CurrencyService.class);
    }

    @Bean
    @Primary
    public MfnService mfnService() {
        return mock(MfnService.class);
    }

    @Bean
    @Primary
    public ProductService productService() {
        return mock(ProductService.class);
    }

    @Bean
    @Primary
    public TradeAgreementService tradeAgreementService() {
        return mock(TradeAgreementService.class);
    }
}
