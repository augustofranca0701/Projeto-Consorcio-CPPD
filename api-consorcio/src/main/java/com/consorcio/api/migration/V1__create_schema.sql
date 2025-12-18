-- V2__create_schema.sql
-- ID interno (SERIAL) + UUID público (API)

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================================
-- USERS
-- =========================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,

    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    cpf VARCHAR(20) UNIQUE,
    phone VARCHAR(50) UNIQUE,

    address VARCHAR(255),
    city VARCHAR(128),
    state VARCHAR(64),
    complement VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(lower(email));
CREATE INDEX IF NOT EXISTS idx_users_uuid ON users(uuid);

-- =========================================================
-- GROUPS (consórcio)
-- =========================================================
CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,

    name VARCHAR(255) NOT NULL,

    valor_total BIGINT NOT NULL,
    valor_parcelas BIGINT NOT NULL,

    meses INTEGER NOT NULL,
    quantidade_pessoas INTEGER NOT NULL,

    data_criacao DATE NOT NULL,
    data_final DATE NOT NULL,

    privado BOOLEAN DEFAULT false,

    status VARCHAR(32) NOT NULL DEFAULT 'CRIADO',
    -- CRIADO | ATIVO | FINALIZADO | CANCELADO

    created_by INTEGER NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_groups_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_groups_uuid ON groups(uuid);
CREATE INDEX IF NOT EXISTS idx_groups_created_by ON groups(created_by);
CREATE INDEX IF NOT EXISTS idx_groups_status ON groups(status);

-- =========================================================
-- USER_GROUP
-- =========================================================
CREATE TABLE IF NOT EXISTS user_group (
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,

    joined_at TIMESTAMP WITH TIME ZONE DEFAULT now(),

    PRIMARY KEY (user_id, group_id),

    CONSTRAINT fk_ug_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ug_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
        ON DELETE CASCADE
);

-- =========================================================
-- PRIZES (contemplações)
-- =========================================================
CREATE TABLE IF NOT EXISTS prizes (
    id SERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,

    group_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,

    date_prize DATE NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),

    CONSTRAINT fk_prizes_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_prizes_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_prizes_uuid ON prizes(uuid);
CREATE INDEX IF NOT EXISTS idx_prizes_group ON prizes(group_id);

-- =========================================================
-- PAYMENTS
-- =========================================================
CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,

    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,

    parcela_numero INTEGER NOT NULL,

    valor BIGINT NOT NULL,

    data_vencimento DATE NOT NULL,

    is_paid BOOLEAN DEFAULT false,
    paid_at TIMESTAMP WITH TIME ZONE,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_payments_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_payments_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id),

    CONSTRAINT uk_payment_unique_parcela
        UNIQUE (user_id, group_id, parcela_numero)
);

CREATE INDEX IF NOT EXISTS idx_payments_uuid ON payments(uuid);
CREATE INDEX IF NOT EXISTS idx_payments_group ON payments(group_id);
CREATE INDEX IF NOT EXISTS idx_payments_user ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_paid ON payments(is_paid);
