--liquibase formatted sql

--changeset Andrii Mishchenko:2
CREATE TABLE dbs
(
    id         BIGINT       NOT NULL IDENTITY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    status     VARCHAR(50)  NOT NULL,
    user_id    BIGINT       NOT NULL
);

ALTER TABLE dbs
    ADD CONSTRAINT pk_dbs PRIMARY KEY (id);
ALTER TABLE dbs
    ADD CONSTRAINT fk_dbs_users FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE dbs
    ADD CONSTRAINT uq_dbs_name_user_id UNIQUE (name, user_id);