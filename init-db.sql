-- Initialize database schemas for Bennycar microservices
-- This script runs automatically when PostgreSQL container starts

-- Create schemas for each microservice
CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS vehicle_service;
CREATE SCHEMA IF NOT EXISTS order_service;

-- Grant all privileges to the admin user
GRANT ALL PRIVILEGES ON SCHEMA user_service TO admin;
GRANT ALL PRIVILEGES ON SCHEMA vehicle_service TO admin;
GRANT ALL PRIVILEGES ON SCHEMA order_service TO admin;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON TABLES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON TABLES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_service GRANT ALL ON TABLES TO admin;

-- Set default privileges for sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON SEQUENCES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON SEQUENCES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_service GRANT ALL ON SEQUENCES TO admin;

-- Log completion
DO $$
BEGIN
  RAISE NOTICE 'Database schemas initialized successfully';
  RAISE NOTICE 'Schemas created: user_service, vehicle_service, order_service';
END $$;

