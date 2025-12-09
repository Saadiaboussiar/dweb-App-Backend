
ALTER TABLE intervention
RENAME COLUMN points TO bonus_amount;

ALTER TABLE intervention
ALTER COLUMN bonus_amount TYPE DECIMAL(10, 2)
USING bonus_amount::DECIMAL(10, 2);

-- Add bonus_amount column
ALTER TABLE intervention
ADD COLUMN IF NOT EXISTS points_earned INTEGER;

