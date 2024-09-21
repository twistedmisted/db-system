--liquibase formatted sql

--changeset Andrii Mishchenko:3
CREATE TABLE tables
(
    id    BIGINT       NOT NULL IDENTITY,
    name  VARCHAR(255) NOT NULL,
    db_id BIGINT       NOT NULL
);

ALTER TABLE tables
    ADD CONSTRAINT pk_tables PRIMARY KEY (id);
ALTER TABLE tables
    ADD CONSTRAINT fk_tables_dbs FOREIGN KEY (db_id) REFERENCES dbs (id);
ALTER TABLE tables
    ADD CONSTRAINT uq_tables_name_db_id UNIQUE (name, db_id);