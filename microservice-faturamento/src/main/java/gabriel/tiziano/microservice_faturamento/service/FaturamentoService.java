package gabriel.tiziano.microservice_faturamento.service;

import gabriel.tiziano.microservice_faturamento.consumer.NotaFiscalMapper;
import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.StatusNotaFiscal;
import gabriel.tiziano.microservice_faturamento.exception.ArmazenamentoNotaFiscalException;
import gabriel.tiziano.microservice_faturamento.publisher.FaturamentoPublisher;
import gabriel.tiziano.microservice_faturamento.publisher.representation.PedidoFaturadoRepresentation;
import gabriel.tiziano.microservice_faturamento.repository.NotaFiscalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FaturamentoService {

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final NotaFiscalRepository notaFiscalRepository;
    private final GeradorNotaFiscalService geradorNotaFiscalService;
    private final BucketService bucketService;
    private final FaturamentoPublisher faturamentoPublisher;

    public FaturamentoService(NotaFiscalRepository notaFiscalRepository, GeradorNotaFiscalService geradorNotaFiscalService, BucketService bucketService, FaturamentoPublisher faturamentoPublisher) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.geradorNotaFiscalService = geradorNotaFiscalService;
        this.bucketService = bucketService;
        this.faturamentoPublisher = faturamentoPublisher;
    }

    public void faturar(PedidoPagoRepresentation pedidoPagoRepresentation) {
        if (notaFiscalRepository.existsByCodigoPedido(pedidoPagoRepresentation.codigoPedido())) {
            log.info("Nota fiscal já existe para o pedido: codigoPedido={}", pedidoPagoRepresentation.codigoPedido());
            return;
        }

        NotaFiscal notaFiscal = NotaFiscalMapper.toNotaFiscal(pedidoPagoRepresentation);

        byte[] pdf = geradorNotaFiscalService.gerarNotaFiscal(notaFiscal);

        String chave = montarChave(pedidoPagoRepresentation.codigoPedido());
        try {
            bucketService.upload(chave, pdf, CONTENT_TYPE_PDF);
        } catch (Exception e) {
            throw new ArmazenamentoNotaFiscalException(chave, e);
        }

        notaFiscal.setChaveArquivo(chave);
        notaFiscal.setStatus(StatusNotaFiscal.EMITIDA);
        notaFiscalRepository.save(notaFiscal);

        faturamentoPublisher.publicar(new PedidoFaturadoRepresentation(pedidoPagoRepresentation.codigoPedido(), chave));

        log.info("Nota fiscal emitida para o pedido {} (chave={})", pedidoPagoRepresentation.codigoPedido(), chave);
    }

    private String montarChave(Long codigoPedido) {
        return "nota-fiscal-" + codigoPedido + ".pdf";
    }
}
