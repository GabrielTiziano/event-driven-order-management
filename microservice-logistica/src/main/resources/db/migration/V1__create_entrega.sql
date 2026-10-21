CREATE TABLE entrega
(
    id              BIGSERIAL PRIMARY KEY,
    codigo_pedido   BIGINT      NOT NULL,
    codigo_rastreio VARCHAR(255),
    status          VARCHAR(20) NOT NULL,
    data_criacao    TIMESTAMP   NOT NULL,
    data_despacho   TIMESTAMP,
    CONSTRAINT uk_entrega_codigo_pedido UNIQUE (codigo_pedido)
);