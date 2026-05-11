ALTER TABLE ordens_servico
    ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP,
    ADD COLUMN IF NOT EXISTS atualizado_por VARCHAR(160),
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

ALTER TABLE tecnicos
    ADD COLUMN IF NOT EXISTS usuario_id BIGINT UNIQUE REFERENCES usuarios(id);

CREATE TABLE IF NOT EXISTS ordens_servico_historico (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    status_anterior VARCHAR(30),
    status_novo VARCHAR(30) NOT NULL,
    alterado_por VARCHAR(160),
    observacao TEXT,
    alterado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_ativo
    ON ordens_servico (ativo);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_historico_ordem_servico_id
    ON ordens_servico_historico (ordem_servico_id);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_historico_alterado_em
    ON ordens_servico_historico (alterado_em);

CREATE INDEX IF NOT EXISTS idx_usuarios_email
    ON usuarios (email);

CREATE INDEX IF NOT EXISTS idx_usuarios_role
    ON usuarios (role);
