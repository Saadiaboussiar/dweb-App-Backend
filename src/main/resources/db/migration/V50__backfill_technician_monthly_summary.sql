BEGIN;

-- Delete existing summaries to start fresh (if needed)
-- TRUNCATE TABLE technician_monthly_summary;  -- Uncomment if you want to clear first

-- Insert monthly summaries from interventions
INSERT INTO technician_monthly_summary (
    technician_id,
    month_year,
    total_points,
    total_bonus,
    interventions_count,
    last_updated
)
SELECT
    i.technician_id,
    DATE_TRUNC('month',
            TO_TIMESTAMP(i.submission_date, 'DD/MM/YYYY "à" HH24:MI'))::DATE as month_year,
    SUM(COALESCE(i.points_earned, 0)) as total_points,
    SUM(COALESCE(i.bonus_amount, 0.00)) as total_bonus,
    COUNT(*) as interventions_count,
    NOW() as last_updated
FROM intervention i
WHERE i.technician_id IS NOT NULL
  AND i.submission_date IS NOT NULL
  AND (i.points_earned IS NOT NULL OR i.bonus_amount IS NOT NULL)
  AND i.submission_date ~ '^\d{2}/\d{2}/\d{4} à \d{2}:\d{2}$'
GROUP BY i.technician_id,
         DATE_TRUNC('month',
                           TO_TIMESTAMP(i.submission_date, 'DD/MM/YYYY "à" HH24:MI')
                           )::DATE
ON CONFLICT (technician_id, month_year)
DO UPDATE SET
    total_points = EXCLUDED.total_points,
    total_bonus = EXCLUDED.total_bonus,
    interventions_count = EXCLUDED.interventions_count,
    last_updated = NOW();

COMMIT;