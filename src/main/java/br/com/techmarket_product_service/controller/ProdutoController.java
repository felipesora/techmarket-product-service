package br.com.techmarket_product_service.controller;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.service.ProdutoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodosProdutos(@PageableDefault(size = 10) Pageable paginacao) {
        Page<ProdutoResponseDTO> produtos = produtoService.obterTodosProdutos(paginacao);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping(params = "categoria")
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutosPorCategoria(
            @RequestParam String categoria,
            @RequestParam(required = false) String ordenarPor) {
        var produtos = produtoService.obterProdutosPorCategoria(categoria, ordenarPor);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping(params = "busca")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutos(
            @RequestParam String busca,
            @RequestParam(required = false) String ordenarPor) {

        var produtos = produtoService.buscarProdutosPorNome(busca, ordenarPor);
        return ResponseEntity.ok(produtos);
    }

    @PostMapping("/favoritos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosFavoritos(@RequestBody List<String> ids) {
        var produtos = produtoService.obterProdutosFavoritos(ids);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable @NotNull String id) {
        ProdutoResponseDTO produto = produtoService.obterProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@RequestBody @Valid ProdutoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        ProdutoResponseDTO produto = produtoService.cadastrarProduto(dto);
        URI endereco = uriBuilder.path("/produtos/{id}").buildAndExpand(produto.id()).toUri();

        return ResponseEntity.created(endereco).body(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable @NotNull String id, @RequestBody @Valid ProdutoUpdateDTO dto) {
        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable @NotNull String id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}
