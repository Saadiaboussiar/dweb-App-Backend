CREATE TABLE technician_monthly_summary (
    id SERIAL PRIMARY KEY,
    technician_id INTEGER NOT NULL,
    month_year DATE NOT NULL, -- First day of month: 2024-01-01
    total_points INTEGER DEFAULT 0,
    total_bonus DECIMAL(10, 2) DEFAULT 0.00, -- Total DH earned
    interventions_count INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_technician_monthly_summary_technician
            FOREIGN KEY (technician_id)
            REFERENCES technician(id)
            ON DELETE CASCADE,  -- or ON DELETE RESTRICT/SET NULL

     -- Unique constraint
     CONSTRAINT uq_technician_month_year
            UNIQUE (technician_id, month_year)
);