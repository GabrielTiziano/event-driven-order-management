package gabriel.tiziano.microservice_faturamento.consumer;

import gabriel.tiziano.microservice_faturamento.consumer.representation.ItemRepresentation;
import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.service.FaturamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoPagoConsumerTest {

    @Mock
    private FaturamentoService faturamentoService;

    @InjectMocks
    private PedidoPagoConsumer pedidoPagoConsumer;

    @Test
    void deveDelegarEventoRecebidoParaOFaturamentoService() {
        PedidoPagoRepresentation evento = new PedidoPagoRepresentation(
                100L, 55L, "Gabriel", "123", "Rua", "42", "Centro",
                "g@email.com", "4199", "2026-10-18T10:00:00",
                new BigDecimal("150.00"),
                List.of(new ItemRepresentation(10L, "Teclado", 2, new BigDecimal("75.00")))
        );

        pedidoPagoConsumer.consumir(evento);

        verify(faturamentoService).faturar(evento);
    }
}