-- V2__add_system_admin.sql
-- =========================
-- SYSTEM ADMIN ROLE
-- =========================

-- Adiciona coluna de papel sistêmico
-- Protegido contra reexecução
ALTER TABLE users
ADD COLUMN IF NOT EXISTS system_role VARCHAR(32) NOT NULL DEFAULT 'USER';

-- Constraint de integridade do papel sistêmico
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_system_role'
    ) THEN
        ALTER TABLE users
        ADD CONSTRAINT chk_system_role
        CHECK (system_role IN ('USER', 'SYSTEM_ADMIN'));
    END IF;
END $$;

-- =========================
-- Índice para operações administrativas
-- =========================
CREATE INDEX IF NOT EXISTS idx_users_system_role
ON users(system_role);
