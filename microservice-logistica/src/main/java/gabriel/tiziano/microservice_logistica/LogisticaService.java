package gabriel.tiziano.microservice_logistica;

import gabriel.tiziano.microservice_logistica.entity.Entrega;
import gabriel.tiziano.microservice_logistica.entity.StatusEntrega;
import gabriel.tiziano.microservice_logistica.publisher.LogisticaPublisher;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_logistica.repository.EntregaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}