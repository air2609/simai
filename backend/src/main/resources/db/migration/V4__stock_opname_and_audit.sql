-- =============================================================
-- V4: Stock Opname & Audit Trail
-- stock_opnames, stock_opname_items, audit_logs
-- =============================================================

CREATE TABLE stock_opnames (
    id                  BIGSERIAL PRIMARY KEY,
    opname_code         VARCHAR(50) NOT NULL UNIQUE,
    location_id         BIGINT      NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    opname_date         DATE        NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                        CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED')),
    created_by_user_id  BIGINT      REFERENCES users(id) ON DELETE SET NULL,
    approved_by_user_id BIGINT      REFERENCES users(id) ON DELETE SET NULL,
    approved_at         TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_opnames_location ON stock_opnames(location_id);
CREATE INDEX idx_stock_opnames_status ON stock_opnames(status);

CREATE TABLE stock_opname_items (
    id              BIGSERIAL PRIMARY KEY,
    stock_opname_id BIGINT        NOT NULL REFERENCES stock_opnames(id) ON DELETE CASCADE,
    item_id         BIGINT        NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
    system_qty      NUMERIC(19,4) NOT NULL DEFAULT 0,
    physical_qty    NUMERIC(19,4) NOT NULL DEFAULT 0,
    variance_qty    NUMERIC(19,4) NOT NULL DEFAULT 0,
    notes           TEXT,
    CONSTRAINT uq_stock_opname_item UNIQUE (stock_opname_id, item_id)
);

CREATE INDEX idx_stock_opname_items_opname ON stock_opname_items(stock_opname_id);

CREATE TABLE audit_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      REFERENCES users(id) ON DELETE SET NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id   VARCHAR(50),
    action      VARCHAR(30) NOT NULL,
    old_values  TEXT,
    new_values  TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
