CREATE SCHEMA IF NOT EXISTS my_schema;

CREATE TABLE IF NOT EXISTS my_schema.roles (
    id int PRIMARY KEY,
    role character varying(255) NOT NULL
);

CREATE SEQUENCE my_schema.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS my_schema.users (
    id bigint PRIMARY KEY,
    role_id int NOT NULL REFERENCES my_schema.roles (id),
    name character varying(255) NOT NULL,
    registration timestamp(6) without time zone,
    password character varying(255) NOT NULL
);

CREATE SEQUENCE my_schema.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS my_schema.trainings (
    id bigint PRIMARY KEY,
    training_type character varying(255) NOT NULL,
    date timestamp(6) without time zone,
    duration int NOT NULL,
    spent_calories int NOT NULL,
    description character varying(255),
    user_id bigint NOT NULL REFERENCES my_schema.users (id)
);

CREATE SEQUENCE my_schema.trainings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
