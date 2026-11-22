-- =============================================================
-- V2: Modul Aset (dilacak per unit)
-- assets, asset_movements, asset_maintenances
-- =============================================================

CREATE TABLE assets (
    id                  BIGSERIAL PRIMARY KEY,
    asset_code          VARCHAR(50)   NOT NULL UNIQUE,
    name                VARCHAR(200)  NOT NULL,
    category_id         BIGINT        NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    vendor_id           BIGINT        REFERENCES vendors(id) ON DELETE SET NULL,
    brand               VARCHAR(100),
    model               VARCHAR(100),
    serial_number       VARCHAR(100),
    purchase_date       DATE,
    purchase_price      NUMERIC(19,2),
    useful_life_months  INTEGER,
    salvage_value       NUMERIC(19,2),
    status              VARCHAR(30)   NOT NULL DEFAULT 'AVAILABLE'
                        CHECK (status IN ('AVAILABLE', 'ASSIGNED', 'MAINTENANCE', 'DISPOSED')),
    current_location_id BIGINT        REFERENCES locations(id) ON DELETE SET NULL,
    custodian_user_id   BIGINT        REFERENCES users(id) ON DELETE SET NULL,
    notes               TEXT,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_assets_category ON assets(category_id);
CREATE INDEX idx_assets_status ON assets(status);
CREATE INDEX idx_assets_location ON assets(current_location_id);
CREATE INDEX idx_assets_custodian ON assets(custodian_user_id);
CREATE INDEX idx_assets_serial ON assets(serial_number);

CREATE TABLE asset_movements (
    id               BIGSERIAL PRIMARY KEY,
    asset_id         BIGINT      NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    movement_type    VARCHAR(30) NOT NULL
                     CHECK (movement_type IN (
                         'RECEIVE', 'ASSIGN', 'TRANSFER', 'LOAN', 'RETURN',
                         'MAINTENANCE_IN', 'MAINTENANCE_OUT', 'DISPOSE')),
    from_location_id BIGINT      REFERENCES locations(id) ON DELETE SET NULL,
    to_location_id   BIGINT      REFERENCES locations(id) ON DELETE SET NULL,
    from_user_id     BIGINT      REFERENCES users(id) ON DELETE SET NULL,
    to_user_id       BIGINT      REFERENCES users(id) ON DELETE SET NULL,
    reference_no     VARCHAR(100),
    notes            TEXT,
    moved_by_user_id BIGINT      REFERENCES users(id) ON DELETE SET NULL,
    moved_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_asset_movements_asset ON asset_movements(asset_id);
CREATE INDEX idx_asset_movements_moved_at ON asset_movements(moved_at);

CREATE TABLE asset_maintenances (
    id               BIGSERIAL PRIMARY KEY,
    asset_id         BIGINT      NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    vendor_id        BIGINT      REFERENCES vendors(id) ON DELETE SET NULL,
    maintenance_date DATE        NOT NULL,
    cost             NUMERIC(19,2),
    description      TEXT,
    status           VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED'
                     CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_asset_maintenances_asset ON asset_maintenances(asset_id);
