package gabriel.tiziano.microservice_pedidos.entity;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum StatusPedido {

    REALIZADO,
    PAGO,
    FATURADO,
    PREPARANDO_ENVIO,
    ENVIADO,
    ERRO_PAGAMENTO;

    private static final Map<StatusPedido, Set<StatusPedido>> TRANSICOES = new EnumMap<>(StatusPedido.class);

    static {
        TRANSICOES.put(REALIZADO, EnumSet.of(PAGO, ERRO_PAGAMENTO));
        TRANSICOES.put(PAGO, EnumSet.of(FATURADO));
        TRANSICOES.put(FATURADO, EnumSet.of(PREPARANDO_ENVIO));
        TRANSICOES.put(PREPARANDO_ENVIO, EnumSet.of(ENVIADO));
        TRANSICOES.put(ENVIADO, EnumSet.noneOf(StatusPedido.class));
        TRANSICOES.put(ERRO_PAGAMENTO, EnumSet.of(PAGO));
    }

    public boolean podeTransicionarPara(StatusPedido destino) {
        return TRANSICOES.get(this).contains(destino);
    }

    public boolean isFinal() {
        return TRANSICOES.get(this).isEmpty();
    }
}