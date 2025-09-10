CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    profile_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

INSERT INTO admins (email, username) VALUES
('ilham@gmail.com', 'admin1');
