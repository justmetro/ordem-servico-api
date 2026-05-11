CREATE TABLE IF NOT EXISTS ordens_servico (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(160) NOT NULL,
    descricao TEXT NOT NULL,
    solicitante VARCHAR(120) NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    departamento_id BIGINT NOT NULL REFERENCES departamentos(id),
    tecnico_id BIGINT REFERENCES tecnicos(id),
    data_abertura TIMESTAMP NOT NULL,
    data_inicio TIMESTAMP,
    data_finalizacao TIMESTAMP,
    descricao_tecnica TEXT
);
