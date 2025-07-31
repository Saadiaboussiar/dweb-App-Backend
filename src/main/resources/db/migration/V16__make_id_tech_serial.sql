CREATE SEQUENCE tech_id_seq;
ALTER TABLE technician
ALTER COLUMN id SET DEFAULT nextval('tech_id_seq');
