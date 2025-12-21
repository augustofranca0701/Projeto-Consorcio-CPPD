-- V3__create_schema.sql
-- Alinhado com o CONTRATO DE DOMÍNIO — API CONSÓRCIAR

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================================
-- USERS
-- =========================================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    name VARCHAR(255),
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

CREATE INDEX idx_users_email ON users (lower(email));
CREATE INDEX idx_users_uuid ON users (uuid);

-- =========================================================
-- GROUPS
-- =========================================================
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
    CHECK (status IN ('CRIADO', 'ATIVO', 'FINALIZADO', 'CANCELADO')),

    created_by INTEGER NOT NULL REFERENCES users(id),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_groups_uuid ON groups (uuid);
CREATE INDEX idx_groups_status ON groups (status);
CREATE INDEX idx_groups_created_by ON groups (created_by);

-- =========================================================
-- USER_GROUP (participação)
-- =========================================================
CREATE TABLE user_group (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,

    role VARCHAR(16) NOT NULL,
    CHECK (role IN ('ADMIN', 'MEMBER')),

    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (user_id, group_id)
);

CREATE INDEX idx_user_group_role ON user_group (role);

-- =========================================================
-- JOIN REQUESTS (grupos privados)
-- =========================================================
CREATE TABLE join_requests (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,

    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),

    requested_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    decided_at TIMESTAMPTZ,

    UNIQUE (user_id, group_id)
);

CREATE INDEX idx_join_requests_group ON join_requests (group_id);
CREATE INDEX idx_join_requests_status ON join_requests (status);

-- =========================================================
-- PAYMENTS
-- =========================================================
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    user_id INTEGER NOT NULL REFERENCES users(id),
    group_id INTEGER NOT NULL REFERENCES groups(id),

    parcela_numero INTEGER NOT NULL,
    valor BIGINT NOT NULL,

    data_vencimento DATE NOT NULL,

    is_paid BOOLEAN NOT NULL DEFAULT false,
    paid_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,

    UNIQUE (user_id, group_id, parcela_numero)
);

CREATE INDEX idx_payments_uuid ON payments (uuid);
CREATE INDEX idx_payments_group ON payments (group_id);
CREATE INDEX idx_payments_user ON payments (user_id);

-- =========================================================
-- PAYMENT HISTORY (imutável)
-- =========================================================
CREATE TABLE payment_history (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    payment_id INTEGER NOT NULL REFERENCES payments(id) ON DELETE CASCADE,

    action VARCHAR(32) NOT NULL,
    old_value BOOLEAN,
    new_value BOOLEAN,

    performed_by INTEGER NOT NULL REFERENCES users(id),
    performed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_payment_history_payment ON payment_history (payment_id);

-- =========================================================
-- PRIZES
-- =========================================================
CREATE TABLE prizes (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id),

    date_prize DATE NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_prizes_group ON prizes (group_id);

-- =========================================================
-- AUDIT LOG (eventos de domínio)
-- =========================================================
CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,

    group_id INTEGER REFERENCES groups(id),
    action VARCHAR(64) NOT NULL,

    performed_by INTEGER NOT NULL REFERENCES users(id),
    metadata JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_logs_group ON audit_logs (group_id);
CREATE INDEX idx_audit_logs_action ON audit_logs (action);
