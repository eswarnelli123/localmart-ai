-- V5: Add category_id to product (safe, three-step)
-- Step 1: add nullable column if missing
ALTER TABLE product
  ADD COLUMN IF NOT EXISTS category_id BIGINT NULL;

-- Step 2: backfill default (change 1 to an appropriate existing category_id if needed)
UPDATE product SET category_id = 1 WHERE category_id IS NULL;

-- Step 3: add foreign key constraint and make not null (run after verifying backfill)
ALTER TABLE product
  ADD CONSTRAINT IF NOT EXISTS fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id),
  MODIFY COLUMN category_id BIGINT NOT NULL;
