package br.com.techmarket_product_service.controller;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.dto.produtoSnapshot.ProdutoSnapshotDTO;
import br.com.techmarket_product_service.service.ProdutoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    private final RabbitTemplate rabbitTemplate;

    public ProdutoController(ProdutoService produtoService, RabbitTemplate rabbitTemplate) {
        this.produtoService = produtoService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodosUsuarios(@PageableDefault(size = 10) Pageable paginacao) {
        Page<ProdutoResponseDTO> produtos = produtoService.obterTodosProdutos(paginacao);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable @NotNull String id) {
        ProdutoResponseDTO produto = produtoService.obterProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarUsuario(@RequestBody @Valid ProdutoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        ProdutoResponseDTO produto = produtoService.cadastrarProduto(dto);
        URI endereco = uriBuilder.path("/produtos/{id}").buildAndExpand(produto.id()).toUri();

        ProdutoSnapshotDTO produtoSnapshotDTO = converterParaProdutoSnapshot(produto);
        System.out.println("Enviando produto: " + produtoSnapshotDTO);
        rabbitTemplate.convertAndSend("produto.exchange", "", produtoSnapshotDTO);

        return ResponseEntity.created(endereco).body(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable @NotNull String id, @RequestBody @Valid ProdutoUpdateDTO dto) {
        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> remover(@PathVariable @NotNull String id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    private ProdutoSnapshotDTO converterParaProdutoSnapshot(ProdutoResponseDTO produtoResponseDTO) {
        return new ProdutoSnapshotDTO(
                produtoResponseDTO.id(),
                produtoResponseDTO.codigo(),
                produtoResponseDTO.nome(),
                produtoResponseDTO.preco(),
                produtoResponseDTO.estoque(),
                produtoResponseDTO.status()
        );
    }
}
