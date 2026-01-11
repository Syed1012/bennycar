-- ============================================================
-- Vehicle Service Database Tables
-- ============================================================
-- Creates all necessary tables for the vehicle service
-- Schema: vehicle_service
-- ============================================================

SET search_path TO vehicle_service;

-- Brands table (manufacturers)
CREATE TABLE IF NOT EXISTS brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    logo_url VARCHAR(512),
    country_of_origin VARCHAR(100),
    founded_year INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle Types table (Sedan, SUV, etc.)
CREATE TABLE IF NOT EXISTS vehicle_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    icon_url VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand_id UUID NOT NULL REFERENCES brands(id) ON DELETE RESTRICT,
    vehicle_type_id UUID NOT NULL REFERENCES vehicle_types(id) ON DELETE RESTRICT,
    model VARCHAR(100) NOT NULL,
    model_year INTEGER NOT NULL,
    description VARCHAR(500),
    base_price DECIMAL(12, 2) NOT NULL,
    engine VARCHAR(50),
    transmission VARCHAR(50),
    fuel_type VARCHAR(30),
    horsepower VARCHAR(20),
    seating_capacity INTEGER,
    cargo_capacity_liters INTEGER,
    fuel_efficiency VARCHAR(30),
    main_image_url VARCHAR(512),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    stock_quantity INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle additional images table
CREATE TABLE IF NOT EXISTS vehicle_images (
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    image_url VARCHAR(512) NOT NULL,
    PRIMARY KEY (vehicle_id, image_url)
);

-- Customization Categories table
CREATE TABLE IF NOT EXISTS customization_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    display_order INTEGER DEFAULT 0,
    allows_multiple BOOLEAN DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customization Options table
CREATE TABLE IF NOT EXISTS customization_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL REFERENCES customization_categories(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price_adjustment DECIMAL(12, 2) DEFAULT 0.00,
    image_url VARCHAR(512),
    color_code VARCHAR(7),
    display_order INTEGER DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle to Customization Options mapping table
CREATE TABLE IF NOT EXISTS vehicle_customization_options (
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    customization_option_id UUID NOT NULL REFERENCES customization_options(id) ON DELETE CASCADE,
    PRIMARY KEY (vehicle_id, customization_option_id)
);

-- Vehicle Configurations table (user's saved builds)
CREATE TABLE IF NOT EXISTS vehicle_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,
    name VARCHAR(100),
    notes VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    total_price DECIMAL(12, 2),
    ordered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Configuration selected options mapping table
CREATE TABLE IF NOT EXISTS configuration_selected_options (
    configuration_id UUID NOT NULL REFERENCES vehicle_configurations(id) ON DELETE CASCADE,
    customization_option_id UUID NOT NULL REFERENCES customization_options(id) ON DELETE CASCADE,
    PRIMARY KEY (configuration_id, customization_option_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_brand_name ON brands(name);
CREATE INDEX IF NOT EXISTS idx_brand_active ON brands(active);
CREATE INDEX IF NOT EXISTS idx_vehicle_type_name ON vehicle_types(name);
CREATE INDEX IF NOT EXISTS idx_vehicle_brand ON vehicles(brand_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_type ON vehicles(vehicle_type_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_status ON vehicles(status);
CREATE INDEX IF NOT EXISTS idx_vehicle_model_year ON vehicles(model_year);
CREATE INDEX IF NOT EXISTS idx_vehicle_base_price ON vehicles(base_price);
CREATE INDEX IF NOT EXISTS idx_customization_option_category ON customization_options(category_id);
CREATE INDEX IF NOT EXISTS idx_customization_option_active ON customization_options(active);
CREATE INDEX IF NOT EXISTS idx_config_user ON vehicle_configurations(user_id);
CREATE INDEX IF NOT EXISTS idx_config_vehicle ON vehicle_configurations(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_config_status ON vehicle_configurations(status);
CREATE INDEX IF NOT EXISTS idx_config_created ON vehicle_configurations(created_at);

-- Log completion
DO $$
BEGIN
  RAISE NOTICE 'Vehicle service tables initialized successfully';
  RAISE NOTICE 'Tables created: brands, vehicle_types, vehicles, vehicle_images, customization_categories, customization_options, vehicle_customization_options, vehicle_configurations, configuration_selected_options';
END $$;
