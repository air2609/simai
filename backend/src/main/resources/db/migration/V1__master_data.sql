-- =============================================================
-- V1: Master Data & Referensi
-- departments, locations, vendors, categories, users
-- =============================================================

CREATE TABLE departments (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(150) NOT NULL
);

CREATE TABLE locations (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(150) NOT NULL,
    parent_id     BIGINT       REFERENCES locations(id) ON DELETE SET NULL,
    address       TEXT
);

CREATE TABLE vendors (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(150) NOT NULL,
    phone         VARCHAR(50),
    email         VARCHAR(150),
    address       TEXT
);

CREATE TABLE categories (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(50)  NOT NULL UNIQUE,
    name                VARCHAR(150) NOT NULL,
    type                VARCHAR(20)  NOT NULL CHECK (type IN ('ASSET', 'INVENTORY')),
    useful_life_months  INTEGER
);

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR', 'APPROVER', 'VIEWER')),
    department_id BIGINT       REFERENCES departments(id) ON DELETE SET NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_locations_parent ON locations(parent_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_department ON users(department_id);
