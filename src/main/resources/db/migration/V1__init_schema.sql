CREATE TABLE car(
    matricule VARCHAR(10) PRIMARY KEY
);

CREATE TABLE technician(
    id BIGINT PRIMARY KEY,
    car_id VARCHAR(10) NOT NULL,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    email VARCHAR(255),
    phoneNumber VARCHAR(255),
    cin VARCHAR(255),
    photoUrl VARCHAR(255),
    cnss VARCHAR(255),
    FOREIGN KEY (car_id) REFERENCES car(matricule)

);

CREATE TABLE client(
    cin VARCHAR(20) PRIMARY KEY,
    reseauSocial VARCHAR(255),
    contrat VARCHAR(255),
    ville VARCHAR(255),
    adresse VARCHAR(255)
);


CREATE TABLE bonIntervention(
    id SERIAL PRIMARY KEY,
    client_cin VARCHAR(20) NOT NULL,
    technician_id BIGINT NOT NULL,
    km FLOAT,
    date DATE,
    startTime TIME,
    finishTime TIME,
    duration INTERVAL DAY TO SECOND,
    numberIntervenant INTEGER,
    FOREIGN KEY (client_cin) REFERENCES client(cin),
    FOREIGN KEY (technician_id) REFERENCES technician(id)
);

CREATE TABLE intervention(
    id SERIAL PRIMARY KEY,
    technician_id BIGINT NOT NULL,
    bonIntervention_id BIGINT NOT NULL,
    submissionDate TIMESTAMP,
    validationDate TIMESTAMP,
    points INTEGER,
    FOREIGN KEY (bonIntervention_id) REFERENCES bonIntervention(id),
    FOREIGN KEY (technician_id) REFERENCES technician(id)
);

