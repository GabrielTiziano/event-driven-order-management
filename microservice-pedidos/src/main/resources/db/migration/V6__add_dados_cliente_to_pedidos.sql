ALTER TABLE pedidos
    ADD COLUMN cliente_nome VARCHAR(255),
    ADD COLUMN cliente_cpf      VARCHAR(14),
    ADD COLUMN cliente_email    VARCHAR(255),
    ADD COLUMN cliente_telefone VARCHAR(20);