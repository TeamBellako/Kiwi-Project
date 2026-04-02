-- Step 1: Add column as nullable
ALTER TABLE skills
ADD COLUMN type VARCHAR(50);

-- Step 2: Set all existing rows
UPDATE skills
SET type = 'ADAPTABILITY';

-- Step 3: Make it NOT NULL
ALTER TABLE skills
MODIFY COLUMN type VARCHAR(50) NOT NULL;

-- Step 4: Drop old column
ALTER TABLE skills
DROP COLUMN icon;