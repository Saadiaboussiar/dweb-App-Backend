ALTER TABLE intervention
ALTER COLUMN status SET DEFAULT 'PENDING';

UPDATE intervention
SET status='PENDING'
WHERE status IS NULL;

ALTER TABLE intervention
ALTER COLUMN status SET NOT NULL;
