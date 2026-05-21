-- Run this in MySQL Workbench (or `mysql -u root -p < schema.sql`) once
-- to create the database and tables used by the Examinations app.

CREATE DATABASE IF NOT EXISTS examinations
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE examinations;

CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(120) NOT NULL UNIQUE,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
