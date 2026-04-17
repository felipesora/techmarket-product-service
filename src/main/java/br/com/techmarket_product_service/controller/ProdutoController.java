package br.com.techmarket_product_service.controller;

import br.com.techmarket_product_service.dto.imagem.ImagemResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.service.ProdutoImagemService;
import br.com.techmarket_product_service.service.ProdutoService;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoImagemService produtoImagemService;

    public ProdutoController(ProdutoService produtoService, ProdutoImagemService produtoImagemService) {
        this.produtoService = produtoService;
        this.produtoImagemService = produtoImagemService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodosProdutos(@PageableDefault(size = 10) Pageable paginacao) {
        Page<ProdutoResponseDTO> produtos = produtoService.obterTodosProdutos(paginacao);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/mais-vendidos")
    public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutosMaisVendidos(@PageableDefault(size = 10) Pageable paginacao) {
        Page<ProdutoResponseDTO> produtos = produtoService.obterProdutosMaisVendidos(paginacao);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/promocoes")
    public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutosEmPromocao(@PageableDefault(size = 10) Pageable paginacao) {
        var produtos = produtoService.obterProdutosEmPromocao(paginacao);
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

    @GetMapping("/total-produtos-ativos")
    public ResponseEntity<Long> totalProdutosAtivos() {
        long total = produtoService.contarUsuariosAtivosComuns();
        return ResponseEntity.ok(total);
    }

    @PostMapping("/buscar-por-ids")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorIds(@RequestBody List<String> ids) {
        var produtos = produtoService.obterProdutosPorIds(ids);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable @NotNull String id) {
        ProdutoResponseDTO produto = produtoService.obterProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/imagem/{imagemId}")
    public ResponseEntity<byte[]> buscarImagemProduto(@PathVariable String imagemId) {
        ImagemResponseDTO imagem = produtoImagemService.buscarImagem(imagemId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, imagem.contentType()).body(imagem.dados());
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@RequestBody @Valid ProdutoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        ProdutoResponseDTO produto = produtoService.cadastrarProduto(dto);
        URI endereco = uriBuilder.path("/produtos/{id}").buildAndExpand(produto.id()).toUri();

        return ResponseEntity.created(endereco).body(produto);
    }

    @PostMapping("/{id}/imagem")
    public ResponseEntity<String> uploadImagem(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        String imagemId = produtoImagemService.salvarImagemMultipart(id, file);
        return ResponseEntity.ok(imagemId);
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
