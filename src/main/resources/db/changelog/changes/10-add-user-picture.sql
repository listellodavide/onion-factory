--liquibase formatted sql

--changeset liquibase:10
ALTER TABLE users
ADD COLUMN picture_url VARCHAR(255);
