package gabriel.tiziano.microservice_pedidos;

import gabriel.tiziano.microservice_pedidos.client.ClientesClient;
import gabriel.tiziano.microservice_pedidos.client.ProdutosClient;
import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoEnviadoRepresentation;
import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Teste de integracao do "rabo" do ciclo (M4) com um broker Kafka real embarcado.
 *
 * Publica os eventos que a logistica emite e verifica o pedido avancando de estado
 * no banco, exercitando o caminho completo: desserializacao (ByteArrayDeserializer)
 * -> RecordMessageConverter -> @KafkaListener -> PedidoService -> repository.
 *
 * Diferente dos testes unitarios (que mockam o service), este sobe o contexto real,
 * entao pega regressoes de configuracao do Kafka que o mock nunca veria.
 *
 * NAO e @Transactional de proposito: o listener roda em outra thread e so enxerga o
 * pedido se ele estiver committado; por isso o seed e persistido direto e a limpeza
 * vai no @AfterEach. O @BeforeEach espera os listeners receberem particao antes de
 * publicar, eliminando corrida entre producer e consumer.
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, topics = {
        "order-management-microservices_pedidos-faturados",
        "order-management-microservices_pedidos-preparando-envio",
        "order-management-microservices_pedidos-enviados"
})
class CicloPedidoKafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Value("${order-management-microservices.kafka.topics.pedido-preparando-envio}")
    private String topicoPreparando;

    @Value("${order-management-microservices.kafka.topics.pedido-enviado}")
    private String topicoEnviado;

    @MockitoBean
    private ClientesClient clientesClient;

    @MockitoBean
    private ProdutosClient produtosClient;

    @BeforeEach
    void aguardarListenersProntos() {
        // Falha rapido e claro se a infra de @KafkaListener nao subiu no contexto.
        assertThat(registry.getListenerContainers())
                .as("Nenhum container de @KafkaListener foi registrado no contexto de teste")
                .isNotEmpty();
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, 1);
        }
    }

    @AfterEach
    void limpar() {
        pedidoRepository.deleteAll();
    }

    @Test
    void aoReceberPedidoPreparandoEnvio_deveAvancarParaPreparandoEnvio() {
        Long codigo = seed(StatusPedido.FATURADO).getCodigo();

        kafkaTemplate.send(topicoPreparando, String.valueOf(codigo),
                new PedidoPreparandoEnvioRepresentation(codigo));

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(pedidoRepository.findById(codigo))
                        .get()
                        .extracting(Pedido::getStatus)
                        .isEqualTo(StatusPedido.PREPARANDO_ENVIO));
    }

    @Test
    void aoReceberPedidoEnviado_deveAvancarParaEnviadoComRastreio() {
        Long codigo = seed(StatusPedido.PREPARANDO_ENVIO).getCodigo();

        kafkaTemplate.send(topicoEnviado, String.valueOf(codigo),
                new PedidoEnviadoRepresentation(codigo, "LG123456789BR"));

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Pedido pedido = pedidoRepository.findById(codigo).orElseThrow();
            assertThat(pedido.getStatus()).isEqualTo(StatusPedido.ENVIADO);
            assertThat(pedido.getCodigoRastreio()).isEqualTo("LG123456789BR");
        });
    }

    private Pedido seed(StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(1L);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setMetodoPagamento(MetodoPagamento.PIX);
        pedido.setTotal(new BigDecimal("20.00"));

        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        return pedidoRepository.save(pedido);
    }
}
