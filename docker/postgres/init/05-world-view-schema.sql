-- ============================================================
-- World View Database Tables
-- ============================================================
-- Creates all necessary tables for the world view service
-- Schema: world_view
-- ============================================================

SET search_path TO world_view;

-- Location table
CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- World View Events table
CREATE TABLE IF NOT EXISTS world_view_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    event_type VARCHAR(100),
    description TEXT,
    event_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for world_view_events
CREATE INDEX IF NOT EXISTS idx_world_view_location ON world_view_events(location_id);
CREATE INDEX IF NOT EXISTS idx_world_view_date ON world_view_events(event_date);

-- Log completion
DO $$
BEGIN
  RAISE NOTICE 'World view service tables initialized successfully';
  RAISE NOTICE 'Tables created: locations, world_view_events';
END $$;