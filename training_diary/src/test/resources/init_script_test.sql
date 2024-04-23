CREATE SCHEMA IF NOT EXISTS my_schema;

CREATE SEQUENCE my_schema.training_type_id_seq
    START WITH 4
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS my_schema.training_type(
    id INT PRIMARY KEY DEFAULT nextval('my_schema.training_type_id_seq'),
    type VARCHAR(255)
);

CREATE SEQUENCE my_schema.users_id_seq
    START WITH 2
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS my_schema.users (
    id BIGINT PRIMARY KEY DEFAULT nextval('my_schema.users_id_seq'),
    role VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    registration timestamp(6) without time zone,
    password VARCHAR(255) NOT NULL
);

CREATE SEQUENCE my_schema.trainings_id_seq
    START WITH 4
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS my_schema.trainings (
    id BIGINT PRIMARY KEY DEFAULT nextval('my_schema.trainings_id_seq'),
    training_type_id INT,
    date timestamp(6) without time zone,
    duration INT,
    spent_calories INT,
    description VARCHAR(255),
    user_id BIGINT
);

INSERT INTO my_schema.users (id, role, name, registration, password)
VALUES (1, 'ROLE_USER', 'Jack', '2024-04-10 15:00:00.00000', 'pass1');

INSERT INTO my_schema.training_type (id, type)
VALUES (1, 'плавание'), (2, 'дартс'), (3, 'лыжи');

INSERT INTO my_schema.trainings (id, training_type_id, date, duration, spent_calories, description, user_id)
VALUES (1, 2, '2024-04-11', 60, 300, '', 1), (2, 1, '2023-10-11', 50, 250, '', 1), (3, 3, '2023-10-11', 120, 350, '', 1);