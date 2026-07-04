-- V4: Add latitude and longitude to store table if missing
-- Safe: uses IF NOT EXISTS where supported. If your MySQL version doesn't support IF NOT EXISTS for ADD COLUMN, run the ALTER statements manually.

ALTER TABLE store
  ADD COLUMN IF NOT EXISTS latitude DOUBLE NULL,
  ADD COLUMN IF NOT EXISTS longitude DOUBLE NULL;
