SELECT setval('tech_id_seq', (SELECT MAX(id) FROM technician) + 1);
