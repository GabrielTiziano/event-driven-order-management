CREATE TABLE pedidos
(
    codigo          SERIAL         NOT NULL PRIMARY KEY,
    codigo_cliente  BIGINT         NOT NULL,
    data_pedido     TIMESTAMP      NOT NULL DEFAULT NOW(),
    chave_pagamento TEXT,
    observacoes     TEXT,
    status          VARCHAR(20) CHECK (
        status IN ('REALIZADO', 'PAGO', 'FATURADO', 'ENVIADO',
                   'ERRO_PAGAMENTO', 'PREPARANDO_ENVIO')
        ),
    total           DECIMAL(16, 2) NOT NULL,
    codigo_rastreio VARCHAR(255),
    url_nf          TEXT
);

CREATE TABLE itens_pedido
(
    codigo         SERIAL         NOT NULL PRIMARY KEY,
    codigo_pedido  BIGINT         NOT NULL REFERENCES pedidos (codigo),
    codigo_produto BIGINT         NOT NULL,
    quantidade     INT            NOT NULL,
    valor_unitario DECIMAL(16, 2) NOT NULL
);