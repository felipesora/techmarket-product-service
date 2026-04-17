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
        cadastrarSeNaoExistir("NB001", "Notebook Dell Inspiron 15", "Notebook i5 16GB RAM SSD 512GB", CategoriaProduto.NOTEBOOKS,
                "Dell", BigDecimal.valueOf(4599.90), 15, "static/images/notebook.png");

        cadastrarSeNaoExistir("CEL001", "iPhone 13", "Smartphone Apple 128GB", CategoriaProduto.CELULARES,
                "Apple", BigDecimal.valueOf(4299.90), 20, "static/images/celular.png");

        cadastrarSeNaoExistir("PC001", "PC Gamer Ryzen 5", "Computador gamer com RTX 3060", CategoriaProduto.COMPUTADORES,
                "Pichau", BigDecimal.valueOf(5999.90), 10, "static/images/computador.png");

        cadastrarSeNaoExistir("MON001", "Monitor LG 24''", "Monitor Full HD IPS 75Hz", CategoriaProduto.MONITORES,
                "LG", BigDecimal.valueOf(899.90), 25, "static/images/monitor.png");

        cadastrarSeNaoExistir("PER001", "Mousepad Gamer XL", "Mousepad grande com base antiderrapante", CategoriaProduto.PERIFERICOS,
                "Redragon", BigDecimal.valueOf(79.90), 50, "static/images/periferico.png");

        cadastrarSeNaoExistir("TEC001", "Teclado Mecânico Redragon Kumara", "Switch Outemu Blue ABNT2", CategoriaProduto.TECLADOS,
                "Redragon", BigDecimal.valueOf(249.90), 30, "static/images/teclado.png");

        cadastrarSeNaoExistir("MOU001", "Mouse Logitech G502", "Mouse gamer RGB com sensor HERO", CategoriaProduto.MOUSES,
                "Logitech", BigDecimal.valueOf(299.90), 40, "static/images/mouse.png");

        cadastrarSeNaoExistir("HEA001", "Headset HyperX Cloud II", "Headset gamer com som surround 7.1", CategoriaProduto.HEADSETS,
                "HyperX", BigDecimal.valueOf(399.90), 20, "static/images/headset.png");

        cadastrarSeNaoExistir("WEB001", "Webcam Logitech C920", "Webcam Full HD 1080p", CategoriaProduto.WEBCAMS,
                "Logitech", BigDecimal.valueOf(349.90), 18, "static/images/webcam.png");

        cadastrarSeNaoExistir("COM001", "Placa de Vídeo RTX 4060", "GPU NVIDIA 8GB GDDR6", CategoriaProduto.COMPONENTES,
                "NVIDIA", BigDecimal.valueOf(2499.90), 12, "static/images/componente.png");

        cadastrarSeNaoExistir("AUD001", "Caixa de Som JBL Go 3", "Caixa de som Bluetooth portátil", CategoriaProduto.AUDIO,
                "JBL", BigDecimal.valueOf(199.90), 35, "static/images/audio.png");

        cadastrarSeNaoExistir("OUT001", "Suporte para Notebook", "Base ergonômica ajustável", CategoriaProduto.OUTROS,
                "Multilaser", BigDecimal.valueOf(89.90), 40, "static/images/outros.png");
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
