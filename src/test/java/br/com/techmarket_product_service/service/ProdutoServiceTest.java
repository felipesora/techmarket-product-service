package br.com.techmarket_product_service.service;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.exception.RegraNegocioException;
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

    private Produto produto;

    @BeforeEach
    void setup() {
        produto = new Produto();
        produto.setId("abc123");
        produto.setNome("Produto Teste");
        produto.setPrecoUnitario(BigDecimal.valueOf(100));
        produto.setPrecoPromocional(BigDecimal.valueOf(90));
        produto.setEstoque(10);
        produto.setStatus(StatusProduto.ATIVO);
    }

    @Test
    void deveCadastrarProdutoComSucesso() {
        ProdutoCreateDTO dto = new ProdutoCreateDTO(
                "Teste",
                "Teste",
                "Teste",
                CategoriaProduto.OUTROS,
                "Teste",
                BigDecimal.TEN,
                BigDecimal.TWO,
                10
        );

        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = produtoService.cadastrarProduto(dto);

        assertNotNull(response);

        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("produto.exchange"),
                eq("produto.criado"),
                any(Object.class)
        );
    }

    @Test
    void deveAtualizarProdutoComSucesso() {
        ProdutoUpdateDTO dto = new ProdutoUpdateDTO(
                "Teste",
                "Produto Atualizado",
                "Teste",
                CategoriaProduto.OUTROS,
                "Teste",
                BigDecimal.TEN,
                BigDecimal.TWO,
                20,
                StatusProduto.ATIVO
        );

        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = produtoService.atualizarProduto("abc123", dto);

        assertEquals("Produto Atualizado", produto.getNome());
        assertEquals(20, produto.getEstoque());

        verify(rabbitTemplate).convertAndSend(
                eq("produto.exchange"),
                eq("produto.atualizado"),
                any(Object.class)
        );
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.empty());

        ProdutoUpdateDTO dto = new ProdutoUpdateDTO(
                "Teste",
                "Teste",
                "Teste",
                CategoriaProduto.OUTROS,
                "Teste",
                BigDecimal.TEN,
                BigDecimal.TWO,
                10,
                StatusProduto.ATIVO
        );

        assertThrows(EntityNotFoundException.class, () ->
                produtoService.atualizarProduto("abc123", dto)
        );
    }

    @Test
    void deveBaixarEstoqueComSucesso() {
        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        produtoService.baixarEstoque("abc123", 5);

        assertEquals(5, produto.getEstoque());

        verify(rabbitTemplate).convertAndSend(
                eq("produto.exchange"),
                eq("produto.atualizado"),
                any(Object.class)
        );
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.of(produto));

        assertThrows(RegraNegocioException.class, () ->
                produtoService.baixarEstoque("abc123", 20)
        );
    }

    @Test
    void deveDevolverEstoqueComSucesso() {
        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        produtoService.devolverEstoque("abc123", 5);

        assertEquals(15, produto.getEstoque());

        verify(rabbitTemplate).convertAndSend(
                eq("produto.exchange"),
                eq("produto.atualizado"),
                any(Object.class)
        );
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.of(produto));

        produtoService.deletarProduto("abc123");

        verify(produtoRepository).delete(produto);

        verify(rabbitTemplate).convertAndSend(
                eq("produto.exchange"),
                eq("produto.removido"),
                any(Object.class)
        );
    }

    @Test
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        when(produtoRepository.findById("abc123"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                produtoService.deletarProduto("abc123")
        );
    }
}