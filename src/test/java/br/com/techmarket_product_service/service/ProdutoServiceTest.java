package br.com.techmarket_product_service.service;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;
import br.com.techmarket_product_service.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProdutoService produtoService;

    @Captor
    private ArgumentCaptor<Produto> produtoCaptor;

    private Produto produto;
    private ProdutoCreateDTO produtoCreateDTO;
    private ProdutoUpdateDTO produtoUpdateDTO;

    private final String PRODUTO_ID = "123e4567-e89b-12d3-a456-426614174000";
    private final String PRODUTO_CODIGO = "SMART-001";
    private final String PRODUTO_NOME = "Smartphone Galaxy S23";
    private final String PRODUTO_DESCRICAO = "Smartphone Samsung Galaxy S23 256GB";
    private final CategoriaProduto PRODUTO_CATEGORIA = CategoriaProduto.CELULARES;
    private final String PRODUTO_MARCA = "Samsung";
    private final BigDecimal PRODUTO_PRECO = new BigDecimal("4500.00");
    private final Integer PRODUTO_ESTOQUE = 50;
    private final StatusProduto PRODUTO_STATUS = StatusProduto.ATIVO;
    private final LocalDateTime PRODUTO_DATA_CRIACAO = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        inicializarProduto();
        inicializarDTOs();
    }

    private void inicializarProduto() {
        produto = new Produto();
        produto.setId(PRODUTO_ID);
        produto.setCodigo(PRODUTO_CODIGO);
        produto.setNome(PRODUTO_NOME);
        produto.setDescricao(PRODUTO_DESCRICAO);
        produto.setCategoria(PRODUTO_CATEGORIA);
        produto.setMarca(PRODUTO_MARCA);
        produto.setPreco(PRODUTO_PRECO);
        produto.setEstoque(PRODUTO_ESTOQUE);
        produto.setStatus(PRODUTO_STATUS);
        produto.setDataCriacao(PRODUTO_DATA_CRIACAO);
    }

    private void inicializarDTOs() {
        produtoCreateDTO = new ProdutoCreateDTO(
                PRODUTO_CODIGO,
                PRODUTO_NOME,
                PRODUTO_DESCRICAO,
                PRODUTO_CATEGORIA,
                PRODUTO_MARCA,
                PRODUTO_PRECO,
                PRODUTO_ESTOQUE
        );

        produtoUpdateDTO = new ProdutoUpdateDTO(
                PRODUTO_CODIGO + "-UPD",
                PRODUTO_NOME + " Atualizado",
                PRODUTO_DESCRICAO + " Atualizado",
                CategoriaProduto.OUTROS,
                PRODUTO_MARCA + " Atualizado",
                new BigDecimal("5000.00"),
                30,
                StatusProduto.INATIVO
        );
    }

    @Nested
    @DisplayName("Testes para obterTodosProdutos")
    class ObterTodosProdutosTest {

        @Test
        @DisplayName("Deve retornar página de produtos quando existirem produtos")
        void deveRetornarPaginaDeProdutos_QuandoExistiremProdutos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Produto> produtos = List.of(produto);
            Page<Produto> paginaProdutos = new PageImpl<>(produtos, pageable, 1);

            when(produtoRepository.findAll(pageable)).thenReturn(paginaProdutos);

            // Act
            Page<ProdutoResponseDTO> resultado = produtoService.obterTodosProdutos(pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            assertEquals(1, resultado.getContent().size());

            ProdutoResponseDTO dto = resultado.getContent().get(0);
            assertEquals(PRODUTO_ID, dto.id());
            assertEquals(PRODUTO_CODIGO, dto.codigo());
            assertEquals(PRODUTO_NOME, dto.nome());
            assertEquals(PRODUTO_DESCRICAO, dto.descricao());
            assertEquals(PRODUTO_CATEGORIA, dto.categoria());
            assertEquals(PRODUTO_MARCA, dto.marca());
            assertEquals(PRODUTO_PRECO, dto.preco());
            assertEquals(PRODUTO_ESTOQUE, dto.estoque());
            assertEquals(PRODUTO_STATUS, dto.status());
            assertEquals(PRODUTO_DATA_CRIACAO, dto.dataCriacao());

            verify(produtoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não existirem produtos")
        void deveRetornarPaginaVazia_QuandoNaoExistiremProdutos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Produto> paginaVazia = Page.empty(pageable);

            when(produtoRepository.findAll(pageable)).thenReturn(paginaVazia);

            // Act
            Page<ProdutoResponseDTO> resultado = produtoService.obterTodosProdutos(pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(0, resultado.getTotalElements());
            assertTrue(resultado.isEmpty());

            verify(produtoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve aplicar paginação corretamente")
        void deveAplicarPaginacaoCorretamente() {
            // Arrange
            Pageable pageable = PageRequest.of(2, 5);
            when(produtoRepository.findAll(pageable)).thenReturn(Page.empty());

            // Act
            produtoService.obterTodosProdutos(pageable);

            // Assert
            verify(produtoRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Testes para obterProdutoPorId")
    class ObterProdutoPorIdTest {

        @Test
        @DisplayName("Deve retornar produto quando ID existir")
        void deveRetornarProduto_QuandoIdExistir() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));

            // Act
            ProdutoResponseDTO resultado = produtoService.obterProdutoPorId(PRODUTO_ID);

            // Assert
            assertNotNull(resultado);
            assertEquals(PRODUTO_ID, resultado.id());
            assertEquals(PRODUTO_CODIGO, resultado.codigo());
            assertEquals(PRODUTO_NOME, resultado.nome());
            assertEquals(PRODUTO_DESCRICAO, resultado.descricao());
            assertEquals(PRODUTO_CATEGORIA, resultado.categoria());
            assertEquals(PRODUTO_MARCA, resultado.marca());
            assertEquals(PRODUTO_PRECO, resultado.preco());
            assertEquals(PRODUTO_ESTOQUE, resultado.estoque());
            assertEquals(PRODUTO_STATUS, resultado.status());
            assertEquals(PRODUTO_DATA_CRIACAO, resultado.dataCriacao());

            verify(produtoRepository, times(1)).findById(PRODUTO_ID);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando ID não existir")
        void deveLancarEntityNotFoundException_QuandoIdNaoExistir() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> produtoService.obterProdutoPorId(PRODUTO_ID)
            );

            assertEquals("Produto com id: " + PRODUTO_ID + " não encontrado", exception.getMessage());
            verify(produtoRepository, times(1)).findById(PRODUTO_ID);
        }
    }

    @Nested
    @DisplayName("Testes para cadastrarProduto")
    class CadastrarProdutoTest {

        @Test
        @DisplayName("Deve cadastrar produto com sucesso")
        void deveCadastrarProduto_ComSucesso() {
            // Arrange
            when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

            // Act
            ProdutoResponseDTO resultado = produtoService.cadastrarProduto(produtoCreateDTO);

            // Assert
            assertNotNull(resultado);
            assertEquals(PRODUTO_ID, resultado.id());
            assertEquals(PRODUTO_CODIGO, resultado.codigo());
            assertEquals(PRODUTO_NOME, resultado.nome());
            assertEquals(PRODUTO_DESCRICAO, resultado.descricao());
            assertEquals(PRODUTO_CATEGORIA, resultado.categoria());
            assertEquals(PRODUTO_MARCA, resultado.marca());
            assertEquals(PRODUTO_PRECO, resultado.preco());
            assertEquals(PRODUTO_ESTOQUE, resultado.estoque());
            assertEquals(StatusProduto.ATIVO, resultado.status());

            verify(produtoRepository, times(1)).save(produtoCaptor.capture());

            Produto produtoSalvo = produtoCaptor.getValue();
            assertEquals(PRODUTO_CODIGO, produtoSalvo.getCodigo());
            assertEquals(PRODUTO_NOME, produtoSalvo.getNome());
            assertEquals(PRODUTO_DESCRICAO, produtoSalvo.getDescricao());
            assertEquals(PRODUTO_CATEGORIA, produtoSalvo.getCategoria());
            assertEquals(PRODUTO_MARCA, produtoSalvo.getMarca());
            assertEquals(PRODUTO_PRECO, produtoSalvo.getPreco());
            assertEquals(PRODUTO_ESTOQUE, produtoSalvo.getEstoque());
            assertEquals(StatusProduto.ATIVO, produtoSalvo.getStatus());
            assertNotNull(produtoSalvo.getDataCriacao());
        }

        @Test
        @DisplayName("Deve cadastrar produto sem descrição e marca quando não fornecidas")
        void deveCadastrarProduto_SemDescricaoEMarca_QuandoNaoFornecidas() {
            // Arrange
            ProdutoCreateDTO dtoSemCamposOpcionais = new ProdutoCreateDTO(
                    PRODUTO_CODIGO,
                    PRODUTO_NOME,
                    null,  // descrição opcional
                    PRODUTO_CATEGORIA,
                    null,  // marca opcional
                    PRODUTO_PRECO,
                    PRODUTO_ESTOQUE
            );

            when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

            // Act
            ProdutoResponseDTO resultado = produtoService.cadastrarProduto(dtoSemCamposOpcionais);

            // Assert
            assertNotNull(resultado);

            verify(produtoRepository, times(1)).save(produtoCaptor.capture());

            Produto produtoSalvo = produtoCaptor.getValue();
            assertEquals(PRODUTO_CODIGO, produtoSalvo.getCodigo());
            assertEquals(PRODUTO_NOME, produtoSalvo.getNome());
            assertNull(produtoSalvo.getDescricao());
            assertEquals(PRODUTO_CATEGORIA, produtoSalvo.getCategoria());
            assertNull(produtoSalvo.getMarca());
            assertEquals(PRODUTO_PRECO, produtoSalvo.getPreco());
            assertEquals(PRODUTO_ESTOQUE, produtoSalvo.getEstoque());
        }
    }

    @Nested
    @DisplayName("Testes para atualizarProduto")
    class AtualizarProdutoTest {

        @Test
        @DisplayName("Deve atualizar produto com sucesso quando ID existir")
        void deveAtualizarProduto_ComSucesso_QuandoIdExistir() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

            // Act
            ProdutoResponseDTO resultado = produtoService.atualizarProduto(PRODUTO_ID, produtoUpdateDTO);

            // Assert
            assertNotNull(resultado);

            verify(produtoRepository, times(1)).findById(PRODUTO_ID);
            verify(produtoRepository, times(1)).save(produtoCaptor.capture());

            Produto produtoAtualizado = produtoCaptor.getValue();
            assertEquals(produtoUpdateDTO.codigo(), produtoAtualizado.getCodigo());
            assertEquals(produtoUpdateDTO.nome(), produtoAtualizado.getNome());
            assertEquals(produtoUpdateDTO.descricao(), produtoAtualizado.getDescricao());
            assertEquals(produtoUpdateDTO.categoria(), produtoAtualizado.getCategoria());
            assertEquals(produtoUpdateDTO.marca(), produtoAtualizado.getMarca());
            assertEquals(produtoUpdateDTO.preco(), produtoAtualizado.getPreco());
            assertEquals(produtoUpdateDTO.estoque(), produtoAtualizado.getEstoque());
            assertEquals(produtoUpdateDTO.status(), produtoAtualizado.getStatus());
            assertEquals(PRODUTO_DATA_CRIACAO, produtoAtualizado.getDataCriacao());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao atualizar quando ID não existir")
        void deveLancarEntityNotFoundException_AoAtualizar_QuandoIdNaoExistir() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> produtoService.atualizarProduto(PRODUTO_ID, produtoUpdateDTO)
            );

            assertEquals("Produto com id: " + PRODUTO_ID + " não encontrado", exception.getMessage());
            verify(produtoRepository, times(1)).findById(PRODUTO_ID);
            verify(produtoRepository, never()).save(any(Produto.class));
        }

        @Test
        @DisplayName("Deve manter a data de criação original após atualização")
        void deveManterDataCriacaoOriginal_AposAtualizacao() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

            // Act
            produtoService.atualizarProduto(PRODUTO_ID, produtoUpdateDTO);

            // Assert
            verify(produtoRepository, times(1)).save(produtoCaptor.capture());

            Produto produtoAtualizado = produtoCaptor.getValue();
            assertEquals(PRODUTO_DATA_CRIACAO, produtoAtualizado.getDataCriacao());
        }
    }

    @Nested
    @DisplayName("Testes para deletarProduto")
    class DeletarProdutoTest {

        @Test
        @DisplayName("Deve deletar produto com sucesso quando ID existir")
        void deveDeletarProduto_ComSucesso_QuandoIdExistir() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
            doNothing().when(produtoRepository).delete(any(Produto.class));

            // Act
            produtoService.deletarProduto(PRODUTO_ID);

            // Assert
            verify(produtoRepository, times(1)).findById(PRODUTO_ID);
            verify(produtoRepository, times(1)).delete(produto);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao deletar quando ID não existir")
        void deveLancarEntityNotFoundException_AoDeletar_QuandoIdNaoExistir() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> produtoService.deletarProduto(PRODUTO_ID)
            );

            assertEquals("Produto com id: " + PRODUTO_ID + " não encontrado", exception.getMessage());
            verify(produtoRepository, times(1)).findById(PRODUTO_ID);
            verify(produtoRepository, never()).delete(any(Produto.class));
        }
    }

    @Nested
    @DisplayName("Testes de validação de conversão")
    class TestesConversao {

        @Test
        @DisplayName("Deve converter Produto para ProdutoResponseDTO corretamente")
        void deveConverterProdutoParaResponseDTO_Corretamente() {
            // Arrange
            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));

            // Act
            ProdutoResponseDTO resultado = produtoService.obterProdutoPorId(PRODUTO_ID);

            // Assert
            assertAll("Conversão Produto para DTO",
                    () -> assertEquals(produto.getId(), resultado.id()),
                    () -> assertEquals(produto.getCodigo(), resultado.codigo()),
                    () -> assertEquals(produto.getNome(), resultado.nome()),
                    () -> assertEquals(produto.getDescricao(), resultado.descricao()),
                    () -> assertEquals(produto.getCategoria(), resultado.categoria()),
                    () -> assertEquals(produto.getMarca(), resultado.marca()),
                    () -> assertEquals(produto.getPreco(), resultado.preco()),
                    () -> assertEquals(produto.getEstoque(), resultado.estoque()),
                    () -> assertEquals(produto.getStatus(), resultado.status()),
                    () -> assertEquals(produto.getDataCriacao(), resultado.dataCriacao())
            );
        }

        @Test
        @DisplayName("Deve converter Produto com campos nulos para DTO corretamente")
        void deveConverterProdutoComCamposNulos_ParaDTOCorretamente() {
            // Arrange
            Produto produtoComCamposNulos = new Produto();
            produtoComCamposNulos.setId(PRODUTO_ID);
            produtoComCamposNulos.setCodigo(PRODUTO_CODIGO);
            produtoComCamposNulos.setNome(PRODUTO_NOME);
            produtoComCamposNulos.setDescricao(null);
            produtoComCamposNulos.setCategoria(PRODUTO_CATEGORIA);
            produtoComCamposNulos.setMarca(null);
            produtoComCamposNulos.setPreco(PRODUTO_PRECO);
            produtoComCamposNulos.setEstoque(PRODUTO_ESTOQUE);
            produtoComCamposNulos.setStatus(PRODUTO_STATUS);
            produtoComCamposNulos.setDataCriacao(PRODUTO_DATA_CRIACAO);

            when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produtoComCamposNulos));

            // Act
            ProdutoResponseDTO resultado = produtoService.obterProdutoPorId(PRODUTO_ID);

            // Assert
            assertAll("Conversão Produto com campos nulos",
                    () -> assertEquals(PRODUTO_ID, resultado.id()),
                    () -> assertEquals(PRODUTO_CODIGO, resultado.codigo()),
                    () -> assertEquals(PRODUTO_NOME, resultado.nome()),
                    () -> assertNull(resultado.descricao()),
                    () -> assertEquals(PRODUTO_CATEGORIA, resultado.categoria()),
                    () -> assertNull(resultado.marca()),
                    () -> assertEquals(PRODUTO_PRECO, resultado.preco()),
                    () -> assertEquals(PRODUTO_ESTOQUE, resultado.estoque()),
                    () -> assertEquals(PRODUTO_STATUS, resultado.status()),
                    () -> assertEquals(PRODUTO_DATA_CRIACAO, resultado.dataCriacao())
            );
        }
    }
}