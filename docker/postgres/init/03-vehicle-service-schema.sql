-- ============================================================
-- Vehicle Service Database Tables
-- ============================================================
-- Creates all necessary tables for the vehicle service
-- Schema: vehicle_service
-- ============================================================

SET search_path TO vehicle_service;

-- Vehicle Brands table
CREATE TABLE IF NOT EXISTS vehicle_brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(512),
    country VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle Types table
CREATE TABLE IF NOT EXISTS vehicle_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand_id UUID NOT NULL REFERENCES vehicle_brands(id) ON DELETE RESTRICT,
    type_id UUID NOT NULL REFERENCES vehicle_types(id) ON DELETE RESTRICT,
    model_name VARCHAR(255) NOT NULL,
    year INTEGER,
    price DECIMAL(10, 2),
    description TEXT,
    image_url VARCHAR(512),
    color VARCHAR(50),
    fuel_type VARCHAR(50),
    transmission VARCHAR(50),
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for vehicles table
CREATE INDEX IF NOT EXISTS idx_vehicle_brand ON vehicles(brand_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_type ON vehicles(type_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_status ON vehicles(status);

-- Log completion
DO $$
BEGIN
  RAISE NOTICE 'Vehicle service tables initialized successfully';
  RAISE NOTICE 'Tables created: vehicle_brands, vehicle_types, vehicles';
END $$;