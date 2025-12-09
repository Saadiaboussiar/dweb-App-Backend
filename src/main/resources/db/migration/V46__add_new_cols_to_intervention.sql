ALTER TABLE intervention
ADD COLUMN updated BOOLEAN DEFAULT false;

ALTER TABLE intervention
ADD COLUMN update_date_time VARCHAR(255);

