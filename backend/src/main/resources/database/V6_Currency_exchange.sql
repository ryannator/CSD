-- -- =====================================================
-- -- Currency Exchange System (Integrated with tariff.countries)
-- -- =====================================================

-- -- =====================================================
-- -- CURRENCY EXCHANGE RATES TABLE
-- -- Stores historical and spot exchange rates between countries
-- -- =====================================================
CREATE TABLE IF NOT EXISTS tariff.currency_exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    base_country_code VARCHAR(3) NOT NULL REFERENCES tariff.countries(country_code),
    target_country_code VARCHAR(3) NOT NULL REFERENCES tariff.countries(country_code),
    exchange_rate NUMERIC(20,6) NOT NULL,
    rate_type VARCHAR(20) DEFAULT 'SPOT', -- SPOT, HISTORICAL, AVERAGE
    source VARCHAR(100),                  -- e.g., ECB, IMF, MAS
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_currency_pair_date UNIQUE (base_country_code, target_country_code, effective_date)
);

-- -- =====================================================
-- -- INDEXES FOR PERFORMANCE
-- -- =====================================================
CREATE INDEX IF NOT EXISTS idx_currency_pair ON tariff.currency_exchange_rates (base_country_code, target_country_code);
CREATE INDEX IF NOT EXISTS idx_effective_date ON tariff.currency_exchange_rates (effective_date);

-- -- =====================================================
-- -- SAMPLE DATA (Only for countries present in tariff.countries)
-- -- =====================================================
-- -- United States (USD) to others
INSERT INTO tariff.currency_exchange_rates (
    base_country_code, target_country_code, exchange_rate, rate_type, source, effective_date
) VALUES
    ('USD', 'CAD', 1.35, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'MXN', 17.2, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'KRW', 1350, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'JPY', 149.25, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'SGD', 1.36, 'SPOT', 'MAS', '2025-10-07'),
    ('USD', 'AUD', 1.52, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'CLP', 930, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'MAD', 10, 'SPOT', 'IMF', '2025-10-07'),
    ('USD', 'BHD', 0.376, 'SPOT', 'IMF', '2025-10-07'),
    ('USD', 'JOD', 0.709, 'SPOT', 'IMF', '2025-10-07'),
    ('USD', 'PAB', 1, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'COP', 4000, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'PEN', 3.8, 'SPOT', 'Federal Reserve', '2025-10-07'),
    ('USD', 'OMR', 0.385, 'SPOT', 'IMF', '2025-10-07'),
    ('USD', 'ILS', 3.9, 'SPOT', 'IMF', '2025-10-07'),
    ('USD', 'NPR', 133, 'SPOT', 'IMF', '2025-10-07')
ON CONFLICT (base_country_code, target_country_code, effective_date) DO NOTHING;
