-- V1__create_schema.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) UNIQUE,
    phone VARCHAR(50) UNIQUE,
    address VARCHAR(255),
    city VARCHAR(128),
    state VARCHAR(64),
    complement VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

-- =========================
-- GROUPS
-- =========================
CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    name VARCHAR(255) NOT NULL,
    valor_total BIGINT NOT NULL,
    valor_parcelas BIGINT NOT NULL,
    meses INTEGER NOT NULL,
    quantidade_pessoas INTEGER NOT NULL,
    data_criacao DATE NOT NULL,
    data_final DATE NOT NULL,
    privado BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(16) NOT NULL DEFAULT 'CRIADO',
    created_by INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    CHECK (status IN ('CRIADO','ATIVO','FINALIZADO','CANCELADO')),
    CHECK (data_final > data_criacao)
);

-- =========================
-- USER_GROUP
-- =========================
CREATE TABLE user_group (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    role VARCHAR(16) NOT NULL CHECK (role IN ('ADMIN','MEMBER')),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, group_id)
);

-- =========================
-- JOIN REQUESTS
-- =========================
CREATE TABLE join_requests (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    decided_at TIMESTAMPTZ,
    UNIQUE (user_id, group_id),
    CHECK (status IN ('PENDING','APPROVED','REJECTED'))
);

-- =========================
-- PAYMENTS
-- =========================
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    parcela_numero INTEGER NOT NULL,
    valor BIGINT NOT NULL,
    data_vencimento DATE NOT NULL,
    is_paid BOOLEAN NOT NULL DEFAULT false,
    paid_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    UNIQUE (user_id, group_id, parcela_numero)
);

-- =========================
-- PAYMENT HISTORY
-- =========================
CREATE TABLE payment_history (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    payment_id INTEGER NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    action VARCHAR(32) NOT NULL,
    old_value BOOLEAN,
    new_value BOOLEAN,
    performed_by INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    performed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- PRIZES
-- =========================
CREATE TABLE prizes (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    date_prize DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- AUDIT LOGS
-- =========================
CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    group_id INTEGER REFERENCES groups(id) ON DELETE CASCADE,
    action VARCHAR(64) NOT NULL,
    performed_by INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- GROUP INVITES
-- =========================
CREATE TABLE group_invites (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,

    token VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,

    created_by INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    used BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_group_invites_token ON group_invites (token);
