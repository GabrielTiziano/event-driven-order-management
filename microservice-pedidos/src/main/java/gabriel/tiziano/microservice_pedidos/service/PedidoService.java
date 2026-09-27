package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.exception.PedidoNotFoundException;
import gabriel.tiziano.microservice_pedidos.mapper.PedidoMapper;
import gabriel.tiziano.microservice_pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
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
        Pedido pedido = pedidoRepository.findById(codigo)
                .orElseThrow(() -> new PedidoNotFoundException(codigo));
        return PedidoMapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse createPedido(PedidoRequest request) {
        Pedido pedido = PedidoMapper.toEntity(request);
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse updatePedido(Long codigo, PedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(codigo)
                .orElseThrow(() -> new PedidoNotFoundException(codigo));
        PedidoMapper.updateEntity(pedido, request);
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public void deletePedido(Long codigo) {
        if (!pedidoRepository.existsById(codigo)) {
            throw new PedidoNotFoundException(codigo);
        }
        pedidoRepository.deleteById(codigo);
    }
}