package br.com.techmarket_product_service.service;

import br.com.techmarket_product_service.dto.produto.ProdutoRequestDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Page<ProdutoResponseDTO> obterTodosProdutos(Pageable paginacao) {
        return produtoRepository
                .findAll(paginacao)
                .map(this::converterParaResponseDTO);
    }

    public ProdutoResponseDTO obterProdutoPorId(String id) {
        Produto produto = buscarEntidadeProdutoPorId(id);
        return converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO cadastrarProduto(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());

        produto = produtoRepository.save(produto);

        return converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(String id, ProdutoRequestDTO dto) {
        Produto produto = buscarEntidadeProdutoPorId(id);

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());

        produto = produtoRepository.save(produto);

        return converterParaResponseDTO(produto);
    }

    @Transactional
    public void deletarProduto(String id) {
        var produto = buscarEntidadeProdutoPorId(id);
        produtoRepository.delete(produto);
    }

    private Produto buscarEntidadeProdutoPorId(String id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com id: " + id + " não encontrado"));
    }

    private ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque()
        );
    }
}
