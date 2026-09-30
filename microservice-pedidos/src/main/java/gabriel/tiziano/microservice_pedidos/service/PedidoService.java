package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.client.ServicoBancarioClient;
import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.dto.PedidoStatusRequest;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
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
        return PedidoMapper.toResponse(buscarPedido(codigo));
    }

    @Transactional
    public PedidoResponse createPedido(PedidoRequest request) {
        Pedido pedido = PedidoMapper.toEntity(request);
        pedidoValidator.validarPagamento(pedido);
        String chave = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chave);
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse updateStatus(Long codigo, PedidoStatusRequest request) {
        Pedido pedido = buscarPedido(codigo);
        pedidoValidator.validarTransicaoStatus(pedido, request.status());
        pedido.setStatus(request.status());
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public void deletePedido(Long codigo) {
        if (!pedidoRepository.existsById(codigo)) {
            throw new PedidoNotFoundException(codigo);
        }
        pedidoRepository.deleteById(codigo);
    }

    private Pedido buscarPedido(Long codigo) {
        return pedidoRepository.findById(codigo)
                .orElseThrow(() -> new PedidoNotFoundException(codigo));
    }
}