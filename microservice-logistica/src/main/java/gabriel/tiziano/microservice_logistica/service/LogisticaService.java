package gabriel.tiziano.microservice_logistica.service;

import gabriel.tiziano.microservice_logistica.entity.Entrega;
import gabriel.tiziano.microservice_logistica.entity.StatusEntrega;
import gabriel.tiziano.microservice_logistica.exception.EntregaNaoEncontradaException;
import gabriel.tiziano.microservice_logistica.exception.EstadoEntregaInvalidoException;
import gabriel.tiziano.microservice_logistica.publisher.LogisticaPublisher;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoEnviadoRepresentation;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_logistica.repository.EntregaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class LogisticaService {

    private final EntregaRepository entregaRepository;
    private final LogisticaPublisher logisticaPublisher;

    public LogisticaService(EntregaRepository entregaRepository, LogisticaPublisher logisticaPublisher) {
        this.entregaRepository = entregaRepository;
        this.logisticaPublisher = logisticaPublisher;
    }

    public void receberParaPreparacao(Long codigoPedido) {
        if (entregaRepository.existsByCodigoPedido(codigoPedido)) {
            log.info("Entrega já existe para o pedido {} — ignorando (idempotência)", codigoPedido);
            return;
        }

        Entrega entrega = new Entrega();
        entrega.setCodigoPedido(codigoPedido);
        entrega.setStatus(StatusEntrega.EM_PREPARACAO);
        entrega.setDataCriacao(LocalDateTime.now());
        entregaRepository.save(entrega);

        logisticaPublisher.publicarPreparando(new PedidoPreparandoEnvioRepresentation(codigoPedido));

        log.info("Entrega criada (EM_PREPARACAO) e evento publicado para o pedido {}", codigoPedido);
    }

    public String despachar(Long codigoPedido) {
        Entrega entrega = entregaRepository.findByCodigoPedido(codigoPedido)
                .orElseThrow(() -> new EntregaNaoEncontradaException(codigoPedido));

        if (entrega.getStatus() != StatusEntrega.EM_PREPARACAO) {
            throw new EstadoEntregaInvalidoException(codigoPedido, entrega.getStatus());
        }

        String codigoRastreio = gerarCodigoRastreio();
        entrega.setCodigoRastreio(codigoRastreio);
        entrega.setStatus(StatusEntrega.DESPACHADO);
        entrega.setDataDespacho(LocalDateTime.now());
        entregaRepository.save(entrega);

        logisticaPublisher.publicarEnviado(new PedidoEnviadoRepresentation(codigoPedido, codigoRastreio));

        log.info("Entrega despachada para o pedido {} (rastreio={})", codigoPedido, codigoRastreio);
        return codigoRastreio;
    }

    private String gerarCodigoRastreio() {
        long numero = ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);
        return "LG" + numero + "BR";
    }
}