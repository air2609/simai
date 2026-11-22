-- =============================================================
-- V3: Modul Inventori (dilacak per kuantitas / SKU)
-- items, inventory_balances, stock_movements
-- =============================================================

CREATE TABLE items (
    id                  BIGSERIAL PRIMARY KEY,
    sku                 VARCHAR(50)   NOT NULL UNIQUE,
    name                VARCHAR(200)  NOT NULL,
    category_id         BIGINT        NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    unit                VARCHAR(20)   NOT NULL DEFAULT 'PCS',
    min_stock           NUMERIC(19,4) NOT NULL DEFAULT 0,
    default_location_id BIGINT        REFERENCES locations(id) ON DELETE SET NULL,
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_items_category ON items(category_id);
CREATE INDEX idx_items_sku ON items(sku);

CREATE TABLE inventory_balances (
    id          BIGSERIAL PRIMARY KEY,
    item_id     BIGINT        NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    location_id BIGINT        NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    quantity    NUMERIC(19,4) NOT NULL DEFAULT 0,
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uq_inventory_balance_item_location UNIQUE (item_id, location_id)
);

CREATE INDEX idx_inventory_balances_item ON inventory_balances(item_id);
CREATE INDEX idx_inventory_balances_location ON inventory_balances(location_id);

CREATE TABLE stock_movements (
    id                  BIGSERIAL PRIMARY KEY,
    item_id             BIGINT        NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    location_id         BIGINT        NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    movement_type       VARCHAR(30)   NOT NULL
                        CHECK (movement_type IN (
                            'INBOUND', 'OUTBOUND', 'TRANSFER_IN', 'TRANSFER_OUT', 'OPNAME_ADJUSTMENT')),
    quantity_delta      NUMERIC(19,4) NOT NULL,
    unit_cost           NUMERIC(19,2),
    reference_no        VARCHAR(100),
    notes               TEXT,
    created_by_user_id  BIGINT        REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_movements_item ON stock_movements(item_id);
CREATE INDEX idx_stock_movements_location ON stock_movements(location_id);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(created_at);
