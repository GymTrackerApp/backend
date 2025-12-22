ALTER TABLE exercises ADD COLUMN category VARCHAR(50);

UPDATE exercises SET category = 'UNCATEGORIZED' WHERE category IS NULL;

ALTER TABLE exercises ALTER COLUMN category SET NOT NULL;