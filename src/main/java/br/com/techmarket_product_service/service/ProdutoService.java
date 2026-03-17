package br.com.techmarket_product_service.service;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.dto.produtoSnapshot.ProdutoSnapshotDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.model.enums.StatusProduto;
import br.com.techmarket_product_service.repository.ProdutoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    private final RabbitTemplate rabbitTemplate;

    public ProdutoService(ProdutoRepository produtoRepository, RabbitTemplate rabbitTemplate) {
        this.produtoRepository = produtoRepository;
        this.rabbitTemplate = rabbitTemplate;
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
    public ProdutoResponseDTO cadastrarProduto(ProdutoCreateDTO dto) {
        Produto produto = new Produto();
        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setCategoria(dto.categoria());
        produto.setMarca(dto.marca());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setStatus(StatusProduto.ATIVO);
        produto.setDataCriacao(LocalDateTime.now());

        produto = produtoRepository.save(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto cadastrado: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.criado", produtoSnapshotDTO);

        return converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(String id, ProdutoUpdateDTO dto) {
        Produto produto = buscarEntidadeProdutoPorId(id);

        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setCategoria(dto.categoria());
        produto.setMarca(dto.marca());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setStatus(dto.status());
        produto.setDataCriacao(produto.getDataCriacao());

        produto = produtoRepository.save(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto atualizado: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.atualizado", produtoSnapshotDTO);

        return converterParaResponseDTO(produto);
    }

    @Transactional
    public void deletarProduto(String id) {
        var produto = buscarEntidadeProdutoPorId(id);
        produtoRepository.delete(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto removido: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.removido", produtoSnapshotDTO);
    }

    private Produto buscarEntidadeProdutoPorId(String id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com id: " + id + " não encontrado"));
    }

    private ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCategoria(),
                produto.getMarca(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getStatus(),
                produto.getDataCriacao()
        );
    }

    private ProdutoSnapshotDTO converterParaProdutoSnapshot(Produto produto) {
        return new ProdutoSnapshotDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getStatus()
        );
    }
}
