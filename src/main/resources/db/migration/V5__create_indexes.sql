CREATE INDEX IF NOT EXISTS idx_ordens_servico_status
    ON ordens_servico (status);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_prioridade
    ON ordens_servico (prioridade);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_tecnico_id
    ON ordens_servico (tecnico_id);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_departamento_id
    ON ordens_servico (departamento_id);

CREATE INDEX IF NOT EXISTS idx_ordens_servico_data_abertura
    ON ordens_servico (data_abertura);
