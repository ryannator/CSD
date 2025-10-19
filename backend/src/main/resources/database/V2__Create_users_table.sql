-- =====================================================
-- Users Table for Authentication
-- =====================================================

CREATE TABLE IF NOT EXISTS tariff.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_username ON tariff.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON tariff.users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON tariff.users(role);

-- Insert default users for testing (with bcrypt-hashed passwords)
-- admin123 → $2a$12$it2KFuax/amnI0xyZcZ95ujrcBGv.yh.bJj/d82gn9QuCOyr.EtGy
-- user123  → $2a$12$6mVssiRkU9z4tx.EBKhGa.uHEpr.r/GJ4OGGKWMQICFqKr2Vi4xfm

INSERT INTO tariff.users (username, email, password, role) VALUES 
    ('admin', 'admin@tariff.com', '$2a$12$it2KFuax/amnI0xyZcZ95ujrcBGv.yh.bJj/d82gn9QuCOyr.EtGy', 'ADMIN'),
    ('user1', 'user@tariff.com', '$2a$12$6mVssiRkU9z4tx.EBKhGa.uHEpr.r/GJ4OGGKWMQICFqKr2Vi4xfm', 'USER')
ON CONFLICT (username) DO NOTHING;