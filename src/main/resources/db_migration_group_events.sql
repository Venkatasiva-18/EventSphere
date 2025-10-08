-- Migration script for Group/Team Event functionality
-- This script adds support for group/team events to the EventSphere application

-- Add participation type and group size columns to events table
ALTER TABLE events 
ADD COLUMN participation_type VARCHAR(20) DEFAULT 'INDIVIDUAL' AFTER max_participants,
ADD COLUMN group_size INT DEFAULT NULL AFTER participation_type;

-- Add team information columns to rsvps table
ALTER TABLE rsvps
ADD COLUMN team_name VARCHAR(255) DEFAULT NULL AFTER notes,
ADD COLUMN team_size INT DEFAULT NULL AFTER team_name;

-- Update existing events to have INDIVIDUAL participation type (if not already set)
UPDATE events SET participation_type = 'INDIVIDUAL' WHERE participation_type IS NULL;

-- Add indexes for better query performance
CREATE INDEX idx_events_participation_type ON events(participation_type);
CREATE INDEX idx_rsvps_team_name ON rsvps(team_name);

-- Verification queries
-- SELECT * FROM events LIMIT 5;
-- SELECT * FROM rsvps LIMIT 5;