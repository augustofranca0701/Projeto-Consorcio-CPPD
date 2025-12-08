-- V1__create_schema.sql
-- Cria esquema básico compatível com as models Java enviadas

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USERS
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

-- GROUPS
CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    valor_total BIGINT NOT NULL,
    valor_parcelas BIGINT NOT NULL,
    data_criacao DATE NOT NULL,
    meses INTEGER NOT NULL,
    data_final DATE NOT NULL,
    quantidade_pessoas INTEGER NOT NULL,
    privado BOOLEAN DEFAULT false,
    created_by INTEGER, -- deve referenciar users(id) que é SERIAL (INTEGER)
    CONSTRAINT fk_groups_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- tabela intermediária para many-to-many users <-> groups
-- nome ajustado para 'user_group' para bater com o JoinTable no código
CREATE TABLE IF NOT EXISTS user_group (
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_ug_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ug_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- PRIZES
CREATE TABLE IF NOT EXISTS prizes (
    id SERIAL PRIMARY KEY,
    date_prize DATE NOT NULL,
    group_id INTEGER,
    user_id INTEGER, -- trocado para INTEGER para compatibilidade com users.id
    CONSTRAINT fk_prizes_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_prizes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    group_id INTEGER,
    valor BIGINT NOT NULL,
    data_vencimento DATE NOT NULL,
    is_paid BOOLEAN DEFAULT false,
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_payments_group FOREIGN KEY (group_id) REFERENCES groups(id)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS idx_users_email ON users(lower(email));
CREATE INDEX IF NOT EXISTS idx_groups_created_by ON groups(created_by);
CREATE INDEX IF NOT EXISTS idx_prizes_group ON prizes(group_id);
CREATE INDEX IF NOT EXISTS idx_payments_group ON payments(group_id);
CREATE INDEX IF NOT EXISTS idx_payments_user ON payments(user_id);
