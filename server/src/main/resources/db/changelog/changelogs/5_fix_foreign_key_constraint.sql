--liquibase formatted sql

--changeset Andrii Mishchenko:7
ALTER TABLE queries DROP CONSTRAINT fk_queries_dbs;
ALTER TABLE queries
    ADD CONSTRAINT fk_queries_dbs FOREIGN KEY (db_id) REFERENCES dbs (id);