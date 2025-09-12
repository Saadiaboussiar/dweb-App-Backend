CREATE TABLE notifications(

    id BIGSERIAL PRIMARY KEY,
    technician_id BIGINT NOT NULL,

    type VARCHAR(50) NOT NULL CHECK (type IN (
         'INTERVENTION_VALIDATED',
         'INTERVENTION_REJECTED'
    )),
    title VARCHAR(255) NOT NULL DEFAULT 'Statut de votre intervention',
    message TEXT NOT NULL,
    intervention_id BIGINT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (technician_id) REFERENCES technician(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    FOREIGN KEY (intervention_id) REFERENCES intervention(id) ON DELETE CASCADE ON UPDATE RESTRICT
);


