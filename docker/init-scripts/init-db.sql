-- Initialize database schemas for Bennycar microservices
-- This script runs automatically when PostgreSQL container starts

-- Create schemas for each microservice
CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS vehicle_service;
CREATE SCHEMA IF NOT EXISTS order_service;
CREATE SCHEMA IF NOT EXISTS world_view;
CREATE SCHEMA IF NOT EXISTS sonarqube;

-- Grant all privileges to the admin user
GRANT ALL PRIVILEGES ON SCHEMA user_service TO admin;
GRANT ALL PRIVILEGES ON SCHEMA vehicle_service TO admin;
GRANT ALL PRIVILEGES ON SCHEMA order_service TO admin;
GRANT ALL PRIVILEGES ON SCHEMA world_view TO admin;
GRANT ALL PRIVILEGES ON SCHEMA sonarqube TO admin;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON TABLES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON TABLES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_service GRANT ALL ON TABLES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA world_view GRANT ALL ON TABLES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA sonarqube GRANT ALL ON TABLES TO admin;

-- Set default privileges for sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON SEQUENCES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON SEQUENCES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_service GRANT ALL ON SEQUENCES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA world_view GRANT ALL ON SEQUENCES TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA sonarqube GRANT ALL ON SEQUENCES TO admin;

-- Log completion
DO $$
BEGIN
  RAISE NOTICE 'Database schema initialized successfully';
  RAISE NOTICE 'Schemas created: user_service, vehicle_service, order_service, world_view, sonarqube';
END $$;