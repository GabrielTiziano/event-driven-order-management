package gabriel.tiziano.microservice_pedidos.service;

import feign.FeignException;
import gabriel.tiziano.microservice_pedidos.client.ClientesClient;
import gabriel.tiziano.microservice_pedidos.client.ProdutosClient;
import gabriel.tiziano.microservice_pedidos.client.ServicoBancarioClient;
import gabriel.tiziano.microservice_pedidos.client.representation.ClienteRepresentation;
import gabriel.tiziano.microservice_pedidos.client.representation.ProdutoRepresentation;
import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.dto.PedidoStatusRequest;
import gabriel.tiziano.microservice_pedidos.entity.*;
import gabriel.tiziano.microservice_pedidos.exception.ClienteNotFoundException;
import gabriel.tiziano.microservice_pedidos.exception.PedidoNotFoundException;
import gabriel.tiziano.microservice_pedidos.exception.ProdutoNotFoundException;
import gabriel.tiziano.microservice_pedidos.mapper.PedidoMapper;
import gabriel.tiziano.microservice_pedidos.repository.PedidoRepository;
import gabriel.tiziano.microservice_pedidos.validator.PedidoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoValidator pedidoValidator;
    private final ServicoBancarioClient servicoBancarioClient;
    private final ClientesClient clientesClient;
    private final ProdutosClient produtosClient;

    public PedidoService(PedidoRepository pedidoRepository, PedidoValidator pedidoValidator, ServicoBancarioClient servicoBancarioClient, ClientesClient clientesClient, ProdutosClient produtosClient) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoValidator = pedidoValidator;
        this.servicoBancarioClient = servicoBancarioClient;
        this.clientesClient = clientesClient;
        this.produtosClient = produtosClient;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findAllPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse findPedidoById(Long codigo) {
        return PedidoMapper.toResponse(getPedido(codigo));
    }

    @Transactional
    public PedidoResponse createPedido(PedidoRequest request) {
        Pedido pedido = PedidoMapper.toEntity(request);

        pedidoValidator.validarPagamento(pedido);

        snapshotCliente(pedido);
        pedido.getItens().forEach(this::snapshotProduto);
        pedido.setTotal(calcularTotal(pedido));

        requestPayment(pedido);
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse updateStatus(Long codigo, PedidoStatusRequest request) {
        Pedido pedido = getPedido(codigo);
        pedidoValidator.validarTransicaoStatus(pedido, request.status());
        pedido.setStatus(request.status());
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public void confirmPayment(Long codigo, String chavePagamento, boolean aprovado, String observacoes) {
        Pedido pedido = pedidoRepository.findByCodigoAndChavePagamento(codigo, chavePagamento)
                .orElseThrow(() -> new PedidoNotFoundException(codigo));

        StatusPedido novoStatus = aprovado ? StatusPedido.PAGO : StatusPedido.ERRO_PAGAMENTO;
        pedidoValidator.validarTransicaoStatus(pedido, novoStatus);

        pedido.setStatus(novoStatus);
        if (!aprovado) {
            pedido.setObservacoes(observacoes);
        }
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void retryPayment(Long codigo, MetodoPagamento metodoPagamento, Integer parcelas) {
        Pedido pedido = getPedido(codigo);

        pedidoValidator.validarTransicaoStatus(pedido, StatusPedido.REALIZADO);

        pedido.setMetodoPagamento(metodoPagamento);
        pedido.setParcelas(parcelas);
        pedidoValidator.validarPagamento(pedido);

        pedido.setStatus(StatusPedido.REALIZADO);

        requestPayment(pedido);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void deletePedido(Long codigo) {
        if (!pedidoRepository.existsById(codigo)) {
            throw new PedidoNotFoundException(codigo);
        }
        pedidoRepository.deleteById(codigo);
    }

    private void requestPayment(Pedido pedido) {
        String chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private Pedido getPedido(Long codigo) {
        return pedidoRepository.findById(codigo)
                .orElseThrow(() -> new PedidoNotFoundException(codigo));
    }

    private void snapshotCliente(Pedido pedido) {
        ClienteRepresentation cliente;
        try {
            cliente = clientesClient.findClientById(pedido.getCodigoCliente()).getBody();
        } catch (FeignException.NotFound e) {
            throw new ClienteNotFoundException(pedido.getCodigoCliente());
        }
        pedido.setDadosCliente(new DadosCliente(
                cliente.nome(), cliente.cpf(), cliente.email(), cliente.telefone()));
    }

    private void snapshotProduto(ItemPedido item) {
        ProdutoRepresentation produto;
        try {
            produto = produtosClient.findProductById(item.getCodigoProduto()).getBody();
        } catch (FeignException.NotFound e) {
            throw new ProdutoNotFoundException(item.getCodigoProduto());
        }
        item.setNomeProduto(produto.nome());
        item.setValorUnitario(produto.preco());
    }

    private BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .map(i -> i.getValorUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}