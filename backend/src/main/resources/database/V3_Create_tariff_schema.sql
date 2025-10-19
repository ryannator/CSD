-- =====================================================
-- Tariff Database Schema
-- Splits tariff_data_2025 into logical tables for easier reference
-- =====================================================

-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS tariff;

-- =====================================================
-- 1. PRODUCTS TABLE
-- Core product information
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.products (
    id BIGSERIAL PRIMARY KEY,
    hts8 VARCHAR(8) NOT NULL UNIQUE,
    brief_description TEXT NOT NULL,
    quantity_1_code VARCHAR(10),
    quantity_2_code VARCHAR(10),
    wto_binding_code VARCHAR(1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. COUNTRIES TABLE
-- Reference table for all countries/regions
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.countries (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL UNIQUE,
    country_name VARCHAR(100) NOT NULL,
    country_name_short VARCHAR(50),
    region VARCHAR(50),
    continent VARCHAR(50),
    currency VARCHAR(3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3. TRADE_AGREEMENTS TABLE
-- Trade agreements and their metadata
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.trade_agreements (
    id BIGSERIAL PRIMARY KEY,
    agreement_code VARCHAR(20) NOT NULL UNIQUE,
    agreement_name VARCHAR(100) NOT NULL,
    agreement_type VARCHAR(50) NOT NULL, -- FTA, PTA, GSP, etc.
    is_multilateral BOOLEAN DEFAULT FALSE,
    effective_date DATE,
    expiration_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 4. AGREEMENT_PARTICIPANTS TABLE
-- Countries participating in trade agreements
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.agreement_participants (
    id BIGSERIAL PRIMARY KEY,
    agreement_id BIGINT NOT NULL REFERENCES tariff.trade_agreements(id) ON DELETE CASCADE,
    country_id BIGINT NOT NULL REFERENCES tariff.countries(id) ON DELETE CASCADE,
    participant_type VARCHAR(20) DEFAULT 'PARTNER', -- PARTNER, EXCLUDED, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(agreement_id, country_id)
);

-- =====================================================
-- 5. MFN_TARIFF_RATES TABLE
-- Most Favored Nation tariff rates
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.mfn_tariff_rates (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tariff.products(id) ON DELETE CASCADE,
    -- changed to TEXT
    mfn_text_rate TEXT,
    mfn_rate_type_code VARCHAR(10),
    mfn_ave decimal(18,6),
    -- widened to DECIMAL(20,6)
    mfn_ad_val_rate DECIMAL(20,6),
    mfn_specific_rate DECIMAL(20,6),
    mfn_other_rate DECIMAL(20,6),
    -- changed to TEXT
    col1_special_text TEXT,
    col1_special_mod VARCHAR(50),
    -- changed to TEXT
    col2_text_rate TEXT,
    col2_rate_type_code VARCHAR(10),
    -- widened to DECIMAL(20,6)
    col2_ad_val_rate DECIMAL(20,6),
    col2_specific_rate DECIMAL(20,6),
    col2_other_rate DECIMAL(20,6),
    begin_effect_date DATE,
    end_effective_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id)
);

-- =====================================================
-- 6. AGREEMENT_TARIFF_RATES TABLE
-- Country-specific tariff rates under trade agreements
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.agreement_tariff_rates (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tariff.products(id) ON DELETE CASCADE,
    agreement_id BIGINT NOT NULL REFERENCES tariff.trade_agreements(id) ON DELETE CASCADE,
    country_id BIGINT NOT NULL REFERENCES tariff.countries(id) ON DELETE CASCADE,
    rate_type_code VARCHAR(10),
    ad_valorem_rate DECIMAL(20,6),
	text_rate TEXT,
    specific_rate DECIMAL(20,6),
    other_rate DECIMAL(20,6),
    effective_date DATE,
    expiration_date DATE,
	indicator TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, agreement_id, country_id)
);

-- =====================================================
-- 7. PRODUCT_INDICATORS TABLE
-- Special indicators for products (GSP, Pharmaceutical, etc.)
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.product_indicators (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tariff.products(id) ON DELETE CASCADE,
    indicator_type VARCHAR(50) NOT NULL, -- GSP, PHARMACEUTICAL, DYES, etc.
    indicator_value VARCHAR(10), -- Y, N, or specific value
    excluded_countries TEXT, -- For GSP exclusions
    effective_date DATE,
    expiration_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, indicator_type)
);

-- =====================================================
-- 8. PRODUCT_NOTES TABLE
-- Additional notes and comments for products
-- =====================================================
CREATE TABLE IF NOT EXISTS tariff.product_notes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tariff.products(id) ON DELETE CASCADE,
    note_type VARCHAR(50) NOT NULL, -- FOOTNOTE, ADDITIONAL_DUTY, etc.
    -- already TEXT (no change needed)
    note_content TEXT,
    effective_date DATE,
    expiration_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Product indexes
CREATE INDEX IF NOT EXISTS idx_products_hts8 ON tariff.products(hts8);
CREATE INDEX IF NOT EXISTS idx_products_description ON tariff.products USING gin(to_tsvector('english', brief_description));

-- Country indexes
CREATE INDEX IF NOT EXISTS idx_countries_code ON tariff.countries(country_code);
CREATE INDEX IF NOT EXISTS idx_countries_name ON tariff.countries(country_name);

-- Agreement indexes
CREATE INDEX IF NOT EXISTS idx_agreements_code ON tariff.trade_agreements(agreement_code);
CREATE INDEX IF NOT EXISTS idx_agreements_type ON tariff.trade_agreements(agreement_type);

-- Rate indexes
CREATE INDEX IF NOT EXISTS idx_mfn_rates_product ON tariff.mfn_tariff_rates(product_id);
CREATE INDEX IF NOT EXISTS idx_agreement_rates_product ON tariff.agreement_tariff_rates(product_id);
CREATE INDEX IF NOT EXISTS idx_agreement_rates_agreement ON tariff.agreement_tariff_rates(agreement_id);
CREATE INDEX IF NOT EXISTS idx_agreement_rates_country ON tariff.agreement_tariff_rates(country_id);

-- Indicator indexes
CREATE INDEX IF NOT EXISTS idx_indicators_product ON tariff.product_indicators(product_id);
CREATE INDEX IF NOT EXISTS idx_indicators_type ON tariff.product_indicators(indicator_type);

-- Note indexes
CREATE INDEX IF NOT EXISTS idx_notes_product ON tariff.product_notes(product_id);
CREATE INDEX IF NOT EXISTS idx_notes_type ON tariff.product_notes(note_type);

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================

COMMENT ON TABLE tariff.products IS 'Core product information with HTS codes and descriptions';
COMMENT ON TABLE tariff.countries IS 'Reference table for all countries and regions';
COMMENT ON TABLE tariff.trade_agreements IS 'Trade agreements and their metadata';
COMMENT ON TABLE tariff.agreement_participants IS 'Countries participating in trade agreements';
COMMENT ON TABLE tariff.mfn_tariff_rates IS 'Most Favored Nation tariff rates for products';
COMMENT ON TABLE tariff.agreement_tariff_rates IS 'Country-specific tariff rates under trade agreements';
COMMENT ON TABLE tariff.product_indicators IS 'Special indicators for products (GSP, Pharmaceutical, etc.)';
COMMENT ON TABLE tariff.product_notes IS 'Additional notes and comments for products';

COMMENT ON COLUMN tariff.products.hts8 IS '8-digit Harmonized Tariff Schedule code';
COMMENT ON COLUMN tariff.products.brief_description IS 'Product description';
COMMENT ON COLUMN tariff.products.wto_binding_code IS 'WTO binding status (B=Bound, U=Unbound)';

COMMENT ON COLUMN tariff.mfn_tariff_rates.mfn_ad_val_rate IS 'Ad valorem rate as decimal (0.05 = 5%)';
COMMENT ON COLUMN tariff.mfn_tariff_rates.mfn_specific_rate IS 'Specific rate per unit';
COMMENT ON COLUMN tariff.agreement_tariff_rates.ad_val_rate IS 'Ad valorem rate as decimal (0.05 = 5%)';
COMMENT ON COLUMN tariff.agreement_tariff_rates.specific_rate IS 'Specific rate per unit';

COMMENT ON COLUMN tariff.agreement_tariff_rates.text_rate IS 'Text description of the tariff rate';
COMMENT ON COLUMN tariff.agreement_tariff_rates.indicator IS 'Special indicator for the rate';
COMMENT ON COLUMN tariff.agreement_tariff_rates.is_active IS 'Whether this rate is currently active';

-- =====================================================
-- Add effective_date and withdrawal_date to agreement_participants
-- =====================================================

-- Add the new date columns to agreement_participants
ALTER TABLE tariff.agreement_participants 
ADD COLUMN IF NOT EXISTS effective_date DATE,
ADD COLUMN IF NOT EXISTS withdrawal_date DATE;

-- Add indexes for the new date columns
CREATE INDEX IF NOT EXISTS idx_agreement_participants_effective_date 
ON tariff.agreement_participants(effective_date);

CREATE INDEX IF NOT EXISTS idx_agreement_participants_withdrawal_date 
ON tariff.agreement_participants(withdrawal_date);

-- =====================================================
-- Populate random dates for agreement_participants
-- =====================================================

-- Update agreement_participants with random effective dates (2020-2024)
UPDATE tariff.agreement_participants 
SET effective_date = (
    '2020-01-01'::date + 
    (random() * ('2024-12-31'::date - '2020-01-01'::date))::integer
)
WHERE effective_date IS NULL;

-- Update agreement_participants with random withdrawal dates (2025-2030)
-- Withdrawal date should be after effective date
UPDATE tariff.agreement_participants 
SET withdrawal_date = (
    effective_date + 
    (random() * ('2030-12-31'::date - effective_date))::integer
)
WHERE withdrawal_date IS NULL;

-- =====================================================
-- Populate random dates for agreement_tariff_rates
-- =====================================================

-- Update agreement_tariff_rates with random effective dates (2020-2024)
UPDATE tariff.agreement_tariff_rates 
SET effective_date = (
    '2020-01-01'::date + 
    (random() * ('2024-12-31'::date - '2020-01-01'::date))::integer
)
WHERE effective_date IS NULL;

-- Update agreement_tariff_rates with random expiration dates (2025-2030)
-- Expiration date should be after effective date
UPDATE tariff.agreement_tariff_rates 
SET expiration_date = (
    effective_date + 
    (random() * ('2030-12-31'::date - effective_date))::integer
)
WHERE expiration_date IS NULL;

-- =====================================================
-- Ensure agreement_participants dates align with agreement_tariff_rates
-- =====================================================

-- For each agreement, set agreement_participants effective_date to match 
-- the earliest effective_date in agreement_tariff_rates for that agreement
UPDATE tariff.agreement_participants 
SET effective_date = (
    SELECT MIN(atr.effective_date)
    FROM tariff.agreement_tariff_rates atr
    WHERE atr.agreement_id = tariff.agreement_participants.agreement_id
)
WHERE EXISTS (
    SELECT 1 FROM tariff.agreement_tariff_rates atr
    WHERE atr.agreement_id = tariff.agreement_participants.agreement_id
);

-- For each agreement, set agreement_participants withdrawal_date to match 
-- the latest expiration_date in agreement_tariff_rates for that agreement
UPDATE tariff.agreement_participants 
SET withdrawal_date = (
    SELECT MAX(atr.expiration_date)
    FROM tariff.agreement_tariff_rates atr
    WHERE atr.agreement_id = tariff.agreement_participants.agreement_id
)
WHERE EXISTS (
    SELECT 1 FROM tariff.agreement_tariff_rates atr
    WHERE atr.agreement_id = tariff.agreement_participants.agreement_id
);

-- =====================================================
-- Add comments for documentation
-- =====================================================

COMMENT ON COLUMN tariff.agreement_participants.effective_date IS 'Date when the country joined the trade agreement';
COMMENT ON COLUMN tariff.agreement_participants.withdrawal_date IS 'Date when the country withdrew from the trade agreement (NULL if still active)';

COMMENT ON COLUMN tariff.agreement_tariff_rates.effective_date IS 'Date when this tariff rate became effective';
COMMENT ON COLUMN tariff.agreement_tariff_rates.expiration_date IS 'Date when this tariff rate expires (NULL if indefinite)';
