CREATE TABLE nota_fiscal
(
    id               BIGSERIAL PRIMARY KEY,
    codigo_pedido    BIGINT         NOT NULL,
    codigo_cliente   BIGINT         NOT NULL,
    cliente_nome     VARCHAR(255)   NOT NULL,
    cliente_cpf      VARCHAR(255)   NOT NULL,
    cliente_email    VARCHAR(255),
    cliente_telefone VARCHAR(255),
    logradouro       VARCHAR(255)   NOT NULL,
    numero           VARCHAR(255)   NOT NULL,
    bairro           VARCHAR(255)   NOT NULL,
    valor_total      NUMERIC(19, 2) NOT NULL,
    status           VARCHAR(20)    NOT NULL,
    chave_arquivo    VARCHAR(255),
    data_emissao     TIMESTAMP      NOT NULL,
    CONSTRAINT uk_nota_fiscal_codigo_pedido UNIQUE (codigo_pedido)
);

CREATE TABLE item_nota_fiscal
(
    id             BIGSERIAL PRIMARY KEY,
    nota_fiscal_id BIGINT         NOT NULL,
    codigo_produto BIGINT         NOT NULL,
    nome_produto   VARCHAR(255)   NOT NULL,
    quantidade     INTEGER        NOT NULL,
    valor_unitario NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_item_nota_fiscal FOREIGN KEY (nota_fiscal_id)
        REFERENCES nota_fiscal (id)
);

CREATE INDEX idx_item_nota_fiscal_nota ON item_nota_fiscal (nota_fiscal_id);