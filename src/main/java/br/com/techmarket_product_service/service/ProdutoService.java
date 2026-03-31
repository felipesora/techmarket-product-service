package br.com.techmarket_product_service.service;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.dto.produtoSnapshot.ProdutoSnapshotDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.exception.RegraNegocioException;
import br.com.techmarket_product_service.mapper.ProdutoMapper;
import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;
import br.com.techmarket_product_service.repository.ProdutoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
                .map(ProdutoMapper::converterParaResponseDTO);
    }

    public List<ProdutoResponseDTO> obterProdutosFavoritos(List<String> ids) {
        return produtoRepository
                .findAllById(ids)
                .stream()
                .map(ProdutoMapper::converterParaResponseDTO)
                .toList();
    }

    public List<ProdutoResponseDTO> obterProdutosPorCategoria(String categoria, String ordenarPor) {
        CategoriaProduto categoriaProdutoEnum = CategoriaProduto.valueOf(categoria.toUpperCase());
        Sort sort = Sort.unsorted();

        if (ordenarPor != null) {
            sort = switch (ordenarPor) {
                case "preco_asc" -> Sort.by("preco").ascending();
                case "preco_desc" -> Sort.by("preco").descending();
                default -> sort;
            };
        }

        return produtoRepository
                .findByCategoria(categoriaProdutoEnum, sort)
                .stream()
                .map(ProdutoMapper::converterParaResponseDTO)
                .toList();
    }

    public List<ProdutoResponseDTO> buscarProdutosPorNome(String busca, String ordenarPor) {

        Sort sort = Sort.unsorted();

        if (ordenarPor != null) {
            sort = switch (ordenarPor) {
                case "preco_asc" -> Sort.by("preco").ascending();
                case "preco_desc" -> Sort.by("preco").descending();
                default -> sort;
            };
        }

        return produtoRepository
                .findByNomeContainingIgnoreCase(busca, sort)
                .stream()
                .map(ProdutoMapper::converterParaResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO obterProdutoPorId(String id) {
        Produto produto = buscarEntidadeProdutoPorId(id);
        return ProdutoMapper.converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO cadastrarProduto(ProdutoCreateDTO dto) {
        Produto produto = ProdutoMapper.converterCreateDTOParaEntity(dto);
        produtoRepository.save(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = ProdutoMapper.converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto cadastrado: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.criado", produtoSnapshotDTO);

        return ProdutoMapper.converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(String id, ProdutoUpdateDTO dto) {
        Produto produto = buscarEntidadeProdutoPorId(id);
        ProdutoMapper.converterUpdateDTOParaEntity(dto, produto);
        produtoRepository.save(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = ProdutoMapper.converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto atualizado: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.atualizado", produtoSnapshotDTO);

        return ProdutoMapper.converterParaResponseDTO(produto);
    }

    @Transactional
    public void baixarEstoque(String idMongo, Integer quantidade) {
        Produto produto = buscarEntidadeProdutoPorId(idMongo);

        Integer estoque = produto.getEstoque();

        if (quantidade > estoque) {
            throw new RegraNegocioException("Estoque insuficiente para o produto " + produto.getId());
        }

        produto.setEstoque(estoque - quantidade);
        produto = produtoRepository.save(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = ProdutoMapper.converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto atualizado: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.atualizado", produtoSnapshotDTO);
    }

    @Transactional
    public void devolverEstoque(String idMongo, Integer quantidade) {
        Produto produto = buscarEntidadeProdutoPorId(idMongo);

        Integer estoque = produto.getEstoque();

        produto.setEstoque(estoque + quantidade);
        produto = produtoRepository.save(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = ProdutoMapper.converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto atualizado: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.atualizado", produtoSnapshotDTO);
    }

    @Transactional
    public void deletarProduto(String id) {
        var produto = buscarEntidadeProdutoPorId(id);
        produtoRepository.delete(produto);

        ProdutoSnapshotDTO produtoSnapshotDTO = ProdutoMapper.converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto removido: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "produto.removido", produtoSnapshotDTO);
    }

    private Produto buscarEntidadeProdutoPorId(String id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com id: " + id + " não encontrado"));
    }
}
