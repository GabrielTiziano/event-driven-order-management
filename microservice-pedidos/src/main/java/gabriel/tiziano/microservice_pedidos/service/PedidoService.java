package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.client.ServicoBancarioClient;
import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.dto.PedidoStatusRequest;
import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.PedidoNotFoundException;
import gabriel.tiziano.microservice_pedidos.mapper.PedidoMapper;
import gabriel.tiziano.microservice_pedidos.repository.PedidoRepository;
import gabriel.tiziano.microservice_pedidos.validator.PedidoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoValidator pedidoValidator;
    private final ServicoBancarioClient servicoBancarioClient;

    public PedidoService(PedidoRepository pedidoRepository, PedidoValidator pedidoValidator, ServicoBancarioClient servicoBancarioClient) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoValidator = pedidoValidator;
        this.servicoBancarioClient = servicoBancarioClient;
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
}