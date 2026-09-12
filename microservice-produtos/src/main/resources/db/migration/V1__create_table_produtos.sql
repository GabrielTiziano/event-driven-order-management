CREATE TABLE produtos
(
    codigo             SERIAL         NOT NULL PRIMARY KEY,
    nome               VARCHAR(150)   NOT NULL,
    descricao          VARCHAR(500),
    preco              NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INTEGER        NOT NULL DEFAULT 0
);