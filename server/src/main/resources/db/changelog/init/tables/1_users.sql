--liquibase formatted sql

--changeset Andrii Mishchenko:1
CREATE TABLE users
(
    id         BIGINT       NOT NULL IDENTITY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    username   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);
ALTER TABLE users
    ADD CONSTRAINT uq_users_username UNIQUE (username);
ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);