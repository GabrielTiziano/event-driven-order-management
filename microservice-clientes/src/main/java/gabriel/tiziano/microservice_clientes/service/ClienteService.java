package gabriel.tiziano.microservice_clientes.service;

import gabriel.tiziano.microservice_clientes.dto.ClienteRequest;
import gabriel.tiziano.microservice_clientes.dto.ClienteResponse;
import gabriel.tiziano.microservice_clientes.entity.Cliente;
import gabriel.tiziano.microservice_clientes.exception.ClienteNotFoundException;
import gabriel.tiziano.microservice_clientes.mapper.ClienteMapper;
import gabriel.tiziano.microservice_clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> findAllClients() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse findClientById(Long codigo) {
        return clienteRepository.findById(codigo)
                .map(ClienteMapper::toResponse)
                .orElseThrow(() -> new ClienteNotFoundException(codigo));
    }

    @Transactional
    public ClienteResponse createClient(ClienteRequest request) {
        return ClienteMapper.toResponse(clienteRepository.save(ClienteMapper.toEntity(request)));
    }

    @Transactional
    public ClienteResponse updateClient(Long codigo, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(codigo)
                .orElseThrow(() -> new ClienteNotFoundException(codigo));
        ClienteMapper.updateEntity(cliente, request);
        return ClienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void deleteClient(Long codigo) {
        if (!clienteRepository.existsById(codigo)) {
            throw new ClienteNotFoundException(codigo);
        }
        clienteRepository.deleteById(codigo);
    }
}
