-- Initialize database schemas for Bennycar microservices
-- This script runs automatically when PostgreSQL container starts

-- Create schemas for each microservice
CREATE SCHEMA IF NOT EXISTS user_service;

-- Grant all privileges to the admin user
GRANT ALL PRIVILEGES ON SCHEMA user_service TO admin;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON TABLES TO admin;

-- Set default privileges for sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON SEQUENCES TO admin;

-- Log completion
DO $$
BEGIN
  RAISE NOTICE 'Database schema initialized successfully';
  RAISE NOTICE 'Schema created: user_service';
END $$;