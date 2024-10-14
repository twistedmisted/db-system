--liquibase formatted sql

--changeset Andrii Mishchenko:6
ALTER TABLE queries
    ADD CONSTRAINT pk_queries PRIMARY KEY (id);
ALTER TABLE queries
    ADD CONSTRAINT fk_queries_dbs FOREIGN KEY (id) REFERENCES dbs (id);
ALTER TABLE queries
    ADD CONSTRAINT uq_queries_db_id_query_name UNIQUE (db_id, query_name);