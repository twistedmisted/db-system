--liquibase formatted sql

--changeset Andrii Mishchenko:5
CREATE TABLE queries
(
    id         BIGINT       NOT NULL IDENTITY,
    db_id      BIGINT,
    query_name VARCHAR(250) NOT NULL,
    query      TEXT         NOT NULL
);

-- ALTER TABLE queries
--     ADD CONSTRAINT pk_queries PRIMARY KEY (id);
-- ALTER TABLE queries
--     ADD CONSTRAINT fk_queries_dbs FOREIGN KEY (id) REFERENCES dbs (id);
-- ALTER TABLE queries
--     ADD CONSTRAINT uq_queries_db_id_query_name UNIQUE (db_id, query_name);