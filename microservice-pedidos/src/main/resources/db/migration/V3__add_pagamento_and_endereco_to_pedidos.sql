ALTER TABLE pedidos ADD COLUMN metodo_pagamento VARCHAR(20) NOT NULL DEFAULT 'PIX';
ALTER TABLE pedidos ALTER COLUMN metodo_pagamento DROP DEFAULT;
ALTER TABLE pedidos ADD CONSTRAINT chk_pedidos_metodo_pagamento CHECK (
    metodo_pagamento IN ('PIX', 'CREDITO', 'DEBITO', 'BOLETO')
    );

ALTER TABLE pedidos ADD COLUMN parcelas INTEGER;

ALTER TABLE pedidos ADD COLUMN endereco_logradouro VARCHAR(100);
ALTER TABLE pedidos ADD COLUMN endereco_numero     VARCHAR(10);
ALTER TABLE pedidos ADD COLUMN endereco_bairro     VARCHAR(100);
ALTER TABLE pedidos ADD COLUMN endereco_cidade     VARCHAR(100);
ALTER TABLE pedidos ADD COLUMN endereco_cep        VARCHAR(8);