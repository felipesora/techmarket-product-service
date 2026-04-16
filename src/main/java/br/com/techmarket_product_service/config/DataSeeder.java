package br.com.techmarket_product_service.config;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.repository.ProdutoRepository;
import br.com.techmarket_product_service.service.ProdutoImagemService;
import br.com.techmarket_product_service.service.ProdutoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProdutoRepository repository;
    private final ProdutoService produtoService;
    private final ProdutoImagemService produtoImagemService;

    public DataSeeder(ProdutoRepository repository, ProdutoService produtoService, ProdutoImagemService produtoImagemService) {
        this.repository = repository;
        this.produtoService = produtoService;
        this.produtoImagemService = produtoImagemService;
    }

    @Override
    public void run(String... args) throws Exception {
        seedProdutos();
    }

    private void seedProdutos() {
        cadastrarSeNaoExistir("C123F", "Mouse Gamer", "Mouse RGB", CategoriaProduto.MOUSES, "HyperX", BigDecimal.valueOf(199.9), 30, "static/images/mouse.png");
    }

    private void cadastrarSeNaoExistir(String codigo, String nome, String descricao, CategoriaProduto categoria, String marca, BigDecimal preco, Integer estoque, String caminhoImagem) {
        boolean existe = repository.existsByCodigo(codigo);

        if (!existe) {
            ProdutoCreateDTO dto = new ProdutoCreateDTO(codigo, nome, descricao, categoria, marca, preco, estoque);
            var response = produtoService.cadastrarProduto(dto);

            if (caminhoImagem != null) {
                cadastrarImagemProduto(response.id(), caminhoImagem);
            }

            System.out.println("Produto cadastrado: " + nome);
        } else {
            System.out.println("Produto já cadastrado: " + nome);
        }
    }

    private void cadastrarImagemProduto(String idProduto, String caminhoImagem) {
        try {
            ClassPathResource resource = new ClassPathResource(caminhoImagem);

            try (InputStream inputStream = resource.getInputStream()) {

                produtoImagemService.salvarImagem(
                        idProduto,
                        inputStream,
                        resource.getFilename(),
                        Files.probeContentType(resource.getFile().toPath())
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar imagem: " + caminhoImagem, e);
        }
    }
}
