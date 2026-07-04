-- Add latitude and longitude to store table
ALTER TABLE store
ADD COLUMN latitude DOUBLE NULL,
ADD COLUMN longitude DOUBLE NULL;
