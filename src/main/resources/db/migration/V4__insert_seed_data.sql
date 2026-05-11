INSERT INTO departamentos (nome, sigla)
VALUES
    ('Tecnologia da Informacao', 'TI'),
    ('Recursos Humanos', 'RH'),
    ('Financeiro', 'FIN')
ON CONFLICT (sigla) DO NOTHING;

INSERT INTO tecnicos (nome, email, especialidade)
VALUES
    ('Ana Souza', 'ana.souza@example.com', 'Infraestrutura'),
    ('Bruno Lima', 'bruno.lima@example.com', 'Suporte Tecnico'),
    ('Carla Mendes', 'carla.mendes@example.com', 'Sistemas')
ON CONFLICT (email) DO NOTHING;
