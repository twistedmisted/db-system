--liquibase formatted sql

--changeset Andrii Mishchenko:3
CREATE TABLE db_tokens
(
    db_id      BIGINT        NOT NULL,
    token      VARCHAR(8192) NOT NULL,
    life_time  VARCHAR(20)   NOT NULL,
    created_at TIMESTAMP     NOT NULL
);

ALTER TABLE db_tokens
    ADD CONSTRAINT pk_db_tokens PRIMARY KEY (db_id);
ALTER TABLE db_tokens
    ADD CONSTRAINT fk_db_tokens_dbs FOREIGN KEY (db_id) REFERENCES dbs (id);
ALTER TABLE db_tokens
    ADD CONSTRAINT uq_db_tokens_db_id_token UNIQUE (db_id, token);
