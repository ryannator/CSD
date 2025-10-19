-- =====================================================
-- V5: Populate Normalized Tables from tariffs_raw
-- Migrates data from tariffs_raw table to normalized schema
-- =====================================================

-- Clear existing data (optional - comment out if you want to preserve existing data)
-- TRUNCATE tariff.product_notes, tariff.product_indicators, tariff.agreement_tariff_rates, 
--         tariff.mfn_tariff_rates, tariff.agreement_participants, tariff.trade_agreements, 
--         tariff.countries, tariff.products RESTART IDENTITY CASCADE;

-- =====================================================
-- 1. POPULATE PRODUCTS TABLE
-- =====================================================
INSERT INTO tariff.products (hts8, brief_description, quantity_1_code, quantity_2_code, wto_binding_code)
SELECT DISTINCT 
    hts8,
    brief_description,
    quantity_1_code,
    quantity_2_code,
    wto_binding_code
FROM tariff.tariffs_raw 
WHERE hts8 IS NOT NULL 
  AND brief_description IS NOT NULL
ON CONFLICT (hts8) DO NOTHING;

-- =====================================================
-- 2. POPULATE COUNTRIES TABLE
-- =====================================================
-- Insert common countries referenced in trade agreements
INSERT INTO tariff.countries (country_code, country_name, country_name_short, region, continent, currency)
VALUES 
    ('US', 'United States', 'USA', 'North America', 'North America', 'USD'),
    ('CA', 'Canada', 'CAN', 'North America', 'North America', 'CAD'),
    ('MX', 'Mexico', 'MEX', 'North America', 'North America', 'MXN'),
    ('KR', 'South Korea', 'KOR', 'East Asia', 'Asia', 'KRW'),
    ('JP', 'Japan', 'JPN', 'East Asia', 'Asia', 'JPY'),
    ('SG', 'Singapore', 'SGP', 'Southeast Asia', 'Asia', 'SGD'),
    ('AU', 'Australia', 'AUS', 'Oceania', 'Oceania', 'AUD'),
    ('CL', 'Chile', 'CHL', 'South America', 'South America', 'CLP'),
    ('MA', 'Morocco', 'MAR', 'North Africa', 'Africa', 'MAD'),
    ('BH', 'Bahrain', 'BHR', 'Middle East', 'Asia', 'BHD'),
    ('JO', 'Jordan', 'JOR', 'Middle East', 'Asia', 'JOD'),
    ('PA', 'Panama', 'PAN', 'Central America', 'North America', 'PAB'),
    ('CO', 'Colombia', 'COL', 'South America', 'South America', 'COP'),
    ('PE', 'Peru', 'PER', 'South America', 'South America', 'PEN'),
    ('OM', 'Oman', 'OMN', 'Middle East', 'Asia', 'OMR'),
    ('IL', 'Israel', 'ISR', 'Middle East', 'Asia', 'ILS'),
    ('NP', 'Nepal', 'NPL', 'South Asia', 'Asia', 'NPR')
ON CONFLICT (country_code) DO NOTHING;

-- =====================================================
-- 3. POPULATE TRADE AGREEMENTS TABLE
-- =====================================================
INSERT INTO tariff.trade_agreements (agreement_code, agreement_name, agreement_type, is_multilateral, effective_date)
VALUES 
    ('GSP', 'Generalized System of Preferences', 'GSP', true, '1976-01-01'),
    ('USMCA', 'United States-Mexico-Canada Agreement', 'FTA', false, '2020-07-01'),
    ('KORUS', 'United States-Korea Free Trade Agreement', 'FTA', false, '2012-03-15'),
    ('SINGAPORE', 'United States-Singapore Free Trade Agreement', 'FTA', false, '2004-01-01'),
    ('AUSTRALIA', 'United States-Australia Free Trade Agreement', 'FTA', false, '2005-01-01'),
    ('CHILE', 'United States-Chile Free Trade Agreement', 'FTA', false, '2004-01-01'),
    ('MOROCCO', 'United States-Morocco Free Trade Agreement', 'FTA', false, '2006-01-01'),
    ('BAHRAIN', 'United States-Bahrain Free Trade Agreement', 'FTA', false, '2006-08-01'),
    ('JORDAN', 'United States-Jordan Free Trade Agreement', 'FTA', false, '2001-12-17'),
    ('PANAMA', 'United States-Panama Trade Promotion Agreement', 'FTA', false, '2012-10-31'),
    ('COLOMBIA', 'United States-Colombia Trade Promotion Agreement', 'FTA', false, '2012-05-15'),
    ('PERU', 'United States-Peru Trade Promotion Agreement', 'FTA', false, '2009-02-01'),
    ('OMAN', 'United States-Oman Free Trade Agreement', 'FTA', false, '2009-01-01'),
    ('ISRAEL', 'United States-Israel Free Trade Agreement', 'FTA', false, '1985-09-01'),
    ('JAPAN', 'United States-Japan Trade Agreement', 'FTA', false, '2020-01-01'),
    ('NEPAL', 'Nepal Trade Preference Program', 'GSP', false, '2016-12-30')
ON CONFLICT (agreement_code) DO NOTHING;

-- =====================================================
-- 4. POPULATE AGREEMENT PARTICIPANTS TABLE
-- =====================================================
INSERT INTO tariff.agreement_participants (agreement_id, country_id, participant_type)
SELECT ta.id, c.id, 'PARTNER'
FROM tariff.trade_agreements ta, tariff.countries c
WHERE (ta.agreement_code = 'USMCA' AND c.country_code IN ('CA', 'MX'))
   OR (ta.agreement_code = 'KORUS' AND c.country_code = 'KR')
   OR (ta.agreement_code = 'SINGAPORE' AND c.country_code = 'SG')
   OR (ta.agreement_code = 'AUSTRALIA' AND c.country_code = 'AU')
   OR (ta.agreement_code = 'CHILE' AND c.country_code = 'CL')
   OR (ta.agreement_code = 'MOROCCO' AND c.country_code = 'MA')
   OR (ta.agreement_code = 'BAHRAIN' AND c.country_code = 'BH')
   OR (ta.agreement_code = 'JORDAN' AND c.country_code = 'JO')
   OR (ta.agreement_code = 'PANAMA' AND c.country_code = 'PA')
   OR (ta.agreement_code = 'COLOMBIA' AND c.country_code = 'CO')
   OR (ta.agreement_code = 'PERU' AND c.country_code = 'PE')
   OR (ta.agreement_code = 'OMAN' AND c.country_code = 'OM')
   OR (ta.agreement_code = 'ISRAEL' AND c.country_code = 'IL')
   OR (ta.agreement_code = 'JAPAN' AND c.country_code = 'JP')
   OR (ta.agreement_code = 'NEPAL' AND c.country_code = 'NP')
ON CONFLICT (agreement_id, country_id) DO NOTHING;

-- =====================================================
-- 5. POPULATE MFN TARIFF RATES TABLE
-- =====================================================
INSERT INTO tariff.mfn_tariff_rates (
    product_id, mfn_text_rate, mfn_rate_type_code, mfn_ave, 
    mfn_ad_val_rate, mfn_specific_rate, mfn_other_rate,
    col1_special_text, col1_special_mod,
    col2_text_rate, col2_rate_type_code,
    col2_ad_val_rate, col2_specific_rate, col2_other_rate,
    begin_effect_date, end_effective_date
)
SELECT 
    p.id,
    tr.mfn_text_rate,
    tr.mfn_rate_type_code,
    tr.mfn_ave,
    tr.mfn_ad_val_rate,
    tr.mfn_specific_rate,
    tr.mfn_other_rate,
    tr.col1_special_text,
    tr.col1_special_mod,
    tr.col2_text_rate,
    tr.col2_rate_type_code,
    tr.col2_ad_val_rate,
    tr.col2_specific_rate,
    tr.col2_other_rate,
    CASE 
        WHEN tr.begin_effect_date ~ '^\d{1,2}/\d{1,2}/\d{4}$' 
        THEN to_date(tr.begin_effect_date, 'MM/DD/YYYY')
        ELSE NULL 
    END,
    CASE 
        WHEN tr.end_effective_date ~ '^\d{1,2}/\d{1,2}/\d{4}$' 
        THEN to_date(tr.end_effective_date, 'MM/DD/YYYY')
        ELSE NULL 
    END
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.hts8 IS NOT NULL
ON CONFLICT (product_id) DO NOTHING;

-- =====================================================
-- 6. POPULATE AGREEMENT TARIFF RATES TABLE
-- =====================================================

-- USMCA (Canada)
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.nafta_canada_ind,
    NULL, -- No specific rate column for NAFTA Canada
    NULL,
    'USMCA Rate',
    tr.nafta_canada_ind
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'USMCA'
JOIN tariff.countries c ON c.country_code = 'CA'
WHERE tr.nafta_canada_ind IS NOT NULL 
  AND tr.nafta_canada_ind != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- USMCA (Mexico)
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.mexico_rate_type_code,
    tr.mexico_ad_val_rate,
    tr.mexico_specific_rate,
    'USMCA Rate',
    tr.nafta_mexico_ind
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'USMCA'
JOIN tariff.countries c ON c.country_code = 'MX'
WHERE tr.nafta_mexico_ind IS NOT NULL 
  AND tr.nafta_mexico_ind != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Korea
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.korea_rate_type_code,
    tr.korea_ad_val_rate,
    tr.korea_specific_rate,
    'KORUS Rate',
    tr.korea_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'KORUS'
JOIN tariff.countries c ON c.country_code = 'KR'
WHERE tr.korea_indicator IS NOT NULL 
  AND tr.korea_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Singapore
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.singapore_rate_type_code,
    tr.singapore_ad_val_rate,
    tr.singapore_specific_rate,
    'Singapore FTA Rate',
    tr.singapore_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'SINGAPORE'
JOIN tariff.countries c ON c.country_code = 'SG'
WHERE tr.singapore_indicator IS NOT NULL 
  AND tr.singapore_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Australia
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.australia_rate_type_code,
    tr.australia_ad_val_rate,
    tr.australia_specific_rate,
    'Australia FTA Rate',
    tr.australia_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'AUSTRALIA'
JOIN tariff.countries c ON c.country_code = 'AU'
WHERE tr.australia_indicator IS NOT NULL 
  AND tr.australia_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Chile
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.chile_rate_type_code,
    tr.chile_ad_val_rate,
    tr.chile_specific_rate,
    'Chile FTA Rate',
    tr.chile_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'CHILE'
JOIN tariff.countries c ON c.country_code = 'CL'
WHERE tr.chile_indicator IS NOT NULL 
  AND tr.chile_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Morocco
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.morocco_rate_type_code,
    tr.morocco_ad_val_rate,
    tr.morocco_specific_rate,
    'Morocco FTA Rate',
    tr.morocco_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'MOROCCO'
JOIN tariff.countries c ON c.country_code = 'MA'
WHERE tr.morocco_indicator IS NOT NULL 
  AND tr.morocco_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Bahrain
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.bahrain_rate_type_code,
    tr.bahrain_ad_val_rate,
    tr.bahrain_specific_rate,
    'Bahrain FTA Rate',
    tr.bahrain_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'BAHRAIN'
JOIN tariff.countries c ON c.country_code = 'BH'
WHERE tr.bahrain_indicator IS NOT NULL 
  AND tr.bahrain_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Jordan
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.jordan_rate_type_code,
    tr.jordan_ad_val_rate,
    tr.jordan_specific_rate,
    'Jordan FTA Rate',
    tr.jordan_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'JORDAN'
JOIN tariff.countries c ON c.country_code = 'JO'
WHERE tr.jordan_indicator IS NOT NULL 
  AND tr.jordan_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Panama
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.panama_rate_type_code,
    tr.panama_ad_val_rate,
    tr.panama_specific_rate,
    'Panama TPA Rate',
    tr.panama_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'PANAMA'
JOIN tariff.countries c ON c.country_code = 'PA'
WHERE tr.panama_indicator IS NOT NULL 
  AND tr.panama_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Colombia
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.colombia_rate_type_code,
    tr.colombia_ad_val_rate,
    tr.colombia_specific_rate,
    'Colombia TPA Rate',
    tr.colombia_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'COLOMBIA'
JOIN tariff.countries c ON c.country_code = 'CO'
WHERE tr.colombia_indicator IS NOT NULL 
  AND tr.colombia_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Peru
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.peru_rate_type_code,
    tr.peru_ad_val_rate,
    tr.peru_specific_rate,
    'Peru TPA Rate',
    tr.peru_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'PERU'
JOIN tariff.countries c ON c.country_code = 'PE'
WHERE tr.peru_indicator IS NOT NULL 
  AND tr.peru_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Oman
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.oman_rate_type_code,
    tr.oman_ad_val_rate,
    tr.oman_specific_rate,
    'Oman FTA Rate',
    tr.oman_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'OMAN'
JOIN tariff.countries c ON c.country_code = 'OM'
WHERE tr.oman_indicator IS NOT NULL 
  AND tr.oman_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Israel
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    NULL, -- No specific rate type for Israel
    NULL,
    NULL,
    'Israel FTA Rate',
    tr.israel_fta_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'ISRAEL'
JOIN tariff.countries c ON c.country_code = 'IL'
WHERE tr.israel_fta_indicator IS NOT NULL 
  AND tr.israel_fta_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Japan
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    tr.japan_rate_type_code,
    tr.japan_ad_val_rate,
    tr.japan_specific_rate,
    'Japan FTA Rate',
    tr.japan_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'JAPAN'
JOIN tariff.countries c ON c.country_code = 'JP'
WHERE tr.japan_indicator IS NOT NULL 
  AND tr.japan_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- Nepal
INSERT INTO tariff.agreement_tariff_rates (product_id, agreement_id, country_id, rate_type_code, ad_valorem_rate, specific_rate, text_rate, indicator)
SELECT 
    p.id,
    ta.id,
    c.id,
    NULL, -- No specific rate type for Nepal
    NULL,
    NULL,
    'Nepal Trade Preference Rate',
    tr.nepal_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
JOIN tariff.trade_agreements ta ON ta.agreement_code = 'NEPAL'
JOIN tariff.countries c ON c.country_code = 'NP'
WHERE tr.nepal_indicator IS NOT NULL 
  AND tr.nepal_indicator != ''
ON CONFLICT (product_id, agreement_id, country_id) DO NOTHING;

-- =====================================================
-- 7. POPULATE PRODUCT INDICATORS TABLE
-- =====================================================

-- GSP Indicators
INSERT INTO tariff.product_indicators (product_id, indicator_type, indicator_value, excluded_countries)
SELECT 
    p.id,
    'GSP',
    tr.gsp_indicator,
    tr.gsp_ctry_excluded
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.gsp_indicator IS NOT NULL 
  AND tr.gsp_indicator != ''
ON CONFLICT (product_id, indicator_type) DO NOTHING;

-- Pharmaceutical Indicators
INSERT INTO tariff.product_indicators (product_id, indicator_type, indicator_value)
SELECT 
    p.id,
    'PHARMACEUTICAL',
    tr.pharmaceutical_ind
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.pharmaceutical_ind IS NOT NULL 
  AND tr.pharmaceutical_ind != ''
ON CONFLICT (product_id, indicator_type) DO NOTHING;

-- Dyes Indicators
INSERT INTO tariff.product_indicators (product_id, indicator_type, indicator_value)
SELECT 
    p.id,
    'DYES',
    tr.dyes_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.dyes_indicator IS NOT NULL 
  AND tr.dyes_indicator != ''
ON CONFLICT (product_id, indicator_type) DO NOTHING;

-- Nepal Indicators
INSERT INTO tariff.product_indicators (product_id, indicator_type, indicator_value)
SELECT 
    p.id,
    'NEPAL',
    tr.nepal_indicator
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.nepal_indicator IS NOT NULL 
  AND tr.nepal_indicator != ''
ON CONFLICT (product_id, indicator_type) DO NOTHING;

-- =====================================================
-- 8. POPULATE PRODUCT NOTES TABLE
-- =====================================================
INSERT INTO tariff.product_notes (product_id, note_type, note_content)
SELECT 
    p.id,
    'FOOTNOTE',
    tr.footnote_comment
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.footnote_comment IS NOT NULL 
  AND tr.footnote_comment != '';

INSERT INTO tariff.product_notes (product_id, note_type, note_content)
SELECT 
    p.id,
    'ADDITIONAL_DUTY',
    tr.additional_duty
FROM tariff.tariffs_raw tr
JOIN tariff.products p ON p.hts8 = tr.hts8
WHERE tr.additional_duty IS NOT NULL 
  AND tr.additional_duty != '';

-- =====================================================
-- SUMMARY STATISTICS
-- =====================================================
SELECT 
    'Products' as table_name, COUNT(*) as record_count FROM tariff.products
UNION ALL
SELECT 
    'Countries' as table_name, COUNT(*) as record_count FROM tariff.countries
UNION ALL
SELECT 
    'Trade Agreements' as table_name, COUNT(*) as record_count FROM tariff.trade_agreements
UNION ALL
SELECT 
    'MFN Rates' as table_name, COUNT(*) as record_count FROM tariff.mfn_tariff_rates
UNION ALL
SELECT 
    'Agreement Rates' as table_name, COUNT(*) as record_count FROM tariff.agreement_tariff_rates
UNION ALL
SELECT 
    'Product Indicators' as table_name, COUNT(*) as record_count FROM tariff.product_indicators
UNION ALL
SELECT 
    'Product Notes' as table_name, COUNT(*) as record_count FROM tariff.product_notes
ORDER BY table_name;
