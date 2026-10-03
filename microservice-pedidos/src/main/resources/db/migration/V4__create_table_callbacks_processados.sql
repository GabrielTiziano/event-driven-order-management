CREATE TABLE callbacks_processados
(
    chave_idempotencia VARCHAR(255) PRIMARY KEY,
    processado_em      TIMESTAMP NOT NULL DEFAULT NOW()
);