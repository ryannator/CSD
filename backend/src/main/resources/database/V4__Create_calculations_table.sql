-- Create tariff_calculations table (Postgres)
CREATE TABLE IF NOT EXISTS tariff.calculations (
    id BIGSERIAL PRIMARY KEY,                      
    hts_code VARCHAR(8) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    product_value NUMERIC(15,2) NOT NULL,
    quantity INTEGER NOT NULL,
    total_tariff_amount NUMERIC(18,2) NOT NULL,
    calculation_result NUMERIC(38,2) NOT NULL,   
    calculation_type VARCHAR(50) NOT NULL,     
    -- New columns for historical tariff date ranges
    currency VARCHAR(3) DEFAULT 'USD',
    tariff_effective_date DATE,
    tariff_expiration_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_product_value CHECK (product_value >= 0)
);

-- Indexes (Postgres syntax)
CREATE INDEX IF NOT EXISTS idx_hts_code ON tariff.calculations (hts_code);
CREATE INDEX IF NOT EXISTS idx_country_code ON tariff.calculations (country_code);
CREATE INDEX IF NOT EXISTS idx_created_at ON tariff.calculations (created_at);
CREATE INDEX IF NOT EXISTS idx_tariff_effective_date ON tariff.calculations (tariff_effective_date);
CREATE INDEX IF NOT EXISTS idx_tariff_expiration_date ON tariff.calculations (tariff_expiration_date);
CREATE INDEX IF NOT EXISTS idx_calculations_currency ON tariff.calculations (currency);

-- Add comment to explain the currency column
COMMENT ON COLUMN tariff.calculations.currency IS 'Currency code used for the calculation (e.g., USD, CAD, MXN)';

-- Add new columns
ALTER TABLE tariff.calculations 
ADD COLUMN IF NOT EXISTS origin_country VARCHAR(3),
ADD COLUMN IF NOT EXISTS destination_country VARCHAR(3);

-- Copy existing country_code data to origin_country
UPDATE tariff.calculations 
SET origin_country = 'US'
WHERE origin_country IS NULL;

-- Set default destination_country to 'US' for existing records
UPDATE tariff.calculations 
SET destination_country = 'US' 
WHERE destination_country IS NULL;

-- Make origin_country NOT NULL (after copying data)
ALTER TABLE tariff.calculations 
ALTER COLUMN origin_country SET NOT NULL;

-- Make destination_country NOT NULL (after setting defaults)
ALTER TABLE tariff.calculations 
ALTER COLUMN destination_country SET NOT NULL;

-- Update indexes
CREATE INDEX IF NOT EXISTS idx_origin_country ON tariff.calculations (origin_country);
CREATE INDEX IF NOT EXISTS idx_destination_country ON tariff.calculations (destination_country);

-- Add comments
COMMENT ON COLUMN tariff.calculations.origin_country IS 'Country code where the product originates from';
COMMENT ON COLUMN tariff.calculations.destination_country IS 'Country code where the product is being imported to';