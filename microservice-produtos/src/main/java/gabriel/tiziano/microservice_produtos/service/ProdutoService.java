package gabriel.tiziano.microservice_produtos.service;

import gabriel.tiziano.microservice_produtos.dto.ProdutoRequest;
import gabriel.tiziano.microservice_produtos.dto.ProdutoResponse;
import gabriel.tiziano.microservice_produtos.entity.Produto;
import gabriel.tiziano.microservice_produtos.exception.ProdutoNotFoundException;
import gabriel.tiziano.microservice_produtos.mapper.ProdutoMapper;
import gabriel.tiziano.microservice_produtos.repository.ProdutoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> findAllProducts() {
        return produtoRepository.findAll()
                .stream()
                .map(ProdutoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse findProductById(Long codigo) {
        return produtoRepository.findById(codigo)
                .map(ProdutoMapper::toResponse)
                .orElseThrow(() -> new ProdutoNotFoundException(codigo));
    }

    @Transactional
    public ProdutoResponse createProduct(ProdutoRequest request) {
        return ProdutoMapper.toResponse(produtoRepository.save(ProdutoMapper.toEntity(request)));
    }

    @Transactional
    public ProdutoResponse updateProduct(Long codigo, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(codigo)
                .orElseThrow(() -> new ProdutoNotFoundException(codigo));
        ProdutoMapper.updateEntity(produto, request);
        return ProdutoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deleteProduct(Long codigo) {
        if (!produtoRepository.existsById(codigo)) {
            throw new ProdutoNotFoundException(codigo);
        }
        produtoRepository.deleteById(codigo);
    }
}
