BEGIN;
UPDATE intervention
SET points_earned = CASE
    WHEN bonus_amount = 200.00 THEN 500
    WHEN bonus_amount = 100.00  THEN 300
    WHEN bonus_amount = 50.00  THEN 150
    ELSE 0
END
WHERE points_earned IS NULL
   OR points_earned NOT IN (0, 150, 300, 500);

COMMIT;