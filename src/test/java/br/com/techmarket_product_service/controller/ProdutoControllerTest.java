package br.com.techmarket_product_service.controller;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;
import br.com.techmarket_product_service.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProdutoService produtoService;

    private ProdutoResponseDTO produtoResponseDTO;
    private ProdutoCreateDTO produtoCreateDTO;
    private ProdutoUpdateDTO produtoUpdateDTO;

    private final String PRODUTO_ID = UUID.randomUUID().toString();
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
        inicializarDTOs();
    }

    private void inicializarDTOs() {
        produtoResponseDTO = new ProdutoResponseDTO(
                PRODUTO_ID,
                PRODUTO_CODIGO,
                PRODUTO_NOME,
                PRODUTO_DESCRICAO,
                PRODUTO_CATEGORIA,
                PRODUTO_MARCA,
                PRODUTO_PRECO,
                PRODUTO_ESTOQUE,
                PRODUTO_STATUS,
                PRODUTO_DATA_CRIACAO
        );

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
    @DisplayName("GET /produtos")
    class ListarTodosProdutosTest {

        @Test
        @DisplayName("Deve retornar lista paginada de produtos com status 200")
        void deveRetornarListaPaginadaDeProdutos() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProdutoResponseDTO> page = new PageImpl<>(
                    List.of(produtoResponseDTO),
                    pageable,
                    1
            );

            when(produtoService.obterTodosProdutos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/produtos")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id", is(PRODUTO_ID)))
                    .andExpect(jsonPath("$.content[0].codigo", is(PRODUTO_CODIGO)))
                    .andExpect(jsonPath("$.content[0].nome", is(PRODUTO_NOME)))
                    .andExpect(jsonPath("$.content[0].categoria", is(PRODUTO_CATEGORIA.toString())))
                    .andExpect(jsonPath("$.content[0].preco", is(PRODUTO_PRECO.doubleValue())))
                    .andExpect(jsonPath("$.content[0].estoque", is(PRODUTO_ESTOQUE)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)));

            verify(produtoService, times(1)).obterTodosProdutos(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver produtos")
        void deveRetornarListaVazia() throws Exception {
            Page<ProdutoResponseDTO> paginaVazia = new PageImpl<>(List.of());

            when(produtoService.obterTodosProdutos(any(Pageable.class))).thenReturn(paginaVazia);

            mockMvc.perform(get("/produtos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));

            verify(produtoService, times(1)).obterTodosProdutos(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve usar paginação padrão quando parâmetros não forem fornecidos")
        void deveUsarPaginacaoPadrao() throws Exception {
            Page<ProdutoResponseDTO> page = new PageImpl<>(List.of(produtoResponseDTO));

            when(produtoService.obterTodosProdutos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/produtos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(produtoService, times(1)).obterTodosProdutos(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /produtos/{id}")
    class BuscarPorIdTest {

        @Test
        @DisplayName("Deve retornar produto quando ID existir")
        void deveRetornarProdutoQuandoIdExistir() throws Exception {
            when(produtoService.obterProdutoPorId(PRODUTO_ID)).thenReturn(produtoResponseDTO);

            mockMvc.perform(get("/produtos/{id}", PRODUTO_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(PRODUTO_ID)))
                    .andExpect(jsonPath("$.codigo", is(PRODUTO_CODIGO)))
                    .andExpect(jsonPath("$.nome", is(PRODUTO_NOME)))
                    .andExpect(jsonPath("$.categoria", is(PRODUTO_CATEGORIA.toString())))
                    .andExpect(jsonPath("$.preco", is(PRODUTO_PRECO.doubleValue())))
                    .andExpect(jsonPath("$.estoque", is(PRODUTO_ESTOQUE)))
                    .andExpect(jsonPath("$.status", is(PRODUTO_STATUS.toString())));

            verify(produtoService, times(1)).obterProdutoPorId(PRODUTO_ID);
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existir")
        void deveRetornar404QuandoIdNaoExistir() throws Exception {
            String idInexistente = "id-inexistente";
            when(produtoService.obterProdutoPorId(idInexistente))
                    .thenThrow(new EntityNotFoundException("Produto com id: " + idInexistente + " não encontrado"));

            mockMvc.perform(get("/produtos/{id}", idInexistente)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Produto com id: " + idInexistente + " não encontrado")))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));

            verify(produtoService, times(1)).obterProdutoPorId(idInexistente);
        }
    }

    @Nested
    @DisplayName("POST /produtos")
    class CadastrarProdutoTest {

        @Test
        @DisplayName("Deve cadastrar produto com sucesso e retornar 201")
        void deveCadastrarProdutoComSucesso() throws Exception {
            when(produtoService.cadastrarProduto(any(ProdutoCreateDTO.class))).thenReturn(produtoResponseDTO);

            mockMvc.perform(post("/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(produtoCreateDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", containsString("/produtos/" + PRODUTO_ID)))
                    .andExpect(jsonPath("$.id", is(PRODUTO_ID)))
                    .andExpect(jsonPath("$.codigo", is(PRODUTO_CODIGO)))
                    .andExpect(jsonPath("$.nome", is(PRODUTO_NOME)))
                    .andExpect(jsonPath("$.status", is(PRODUTO_STATUS.toString())));

            verify(produtoService, times(1)).cadastrarProduto(any(ProdutoCreateDTO.class));
        }

        @Test
        @DisplayName("Deve aceitar campos opcionais nulos")
        void deveAceitarCamposOpcionaisNulos() throws Exception {
            ProdutoCreateDTO dtoSemOpcionais = new ProdutoCreateDTO(
                    PRODUTO_CODIGO,
                    PRODUTO_NOME,
                    null,
                    PRODUTO_CATEGORIA,
                    null,
                    PRODUTO_PRECO,
                    PRODUTO_ESTOQUE
            );

            ProdutoResponseDTO responseSemOpcionais = new ProdutoResponseDTO(
                    PRODUTO_ID,
                    PRODUTO_CODIGO,
                    PRODUTO_NOME,
                    null,
                    PRODUTO_CATEGORIA,
                    null,
                    PRODUTO_PRECO,
                    PRODUTO_ESTOQUE,
                    PRODUTO_STATUS,
                    PRODUTO_DATA_CRIACAO
            );

            when(produtoService.cadastrarProduto(any(ProdutoCreateDTO.class))).thenReturn(responseSemOpcionais);

            mockMvc.perform(post("/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoSemOpcionais)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.descricao").doesNotExist())
                    .andExpect(jsonPath("$.marca").doesNotExist());

            verify(produtoService, times(1)).cadastrarProduto(any(ProdutoCreateDTO.class));
        }
    }

    @Nested
    @DisplayName("PUT /produtos/{id}")
    class AtualizarProdutoTest {

        @Test
        @DisplayName("Deve atualizar produto com sucesso e retornar 200")
        void deveAtualizarProdutoComSucesso() throws Exception {
            when(produtoService.atualizarProduto(eq(PRODUTO_ID), any(ProdutoUpdateDTO.class)))
                    .thenReturn(produtoResponseDTO);

            mockMvc.perform(put("/produtos/{id}", PRODUTO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(produtoUpdateDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(PRODUTO_ID)))
                    .andExpect(jsonPath("$.codigo", is(PRODUTO_CODIGO)))
                    .andExpect(jsonPath("$.nome", is(PRODUTO_NOME)))
                    .andExpect(jsonPath("$.status", is(PRODUTO_STATUS.toString())));

            verify(produtoService, times(1)).atualizarProduto(eq(PRODUTO_ID), any(ProdutoUpdateDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar quando ID não existir")
        void deveRetornar404AoAtualizarQuandoIdNaoExistir() throws Exception {
            String idInexistente = "id-inexistente";
            when(produtoService.atualizarProduto(eq(idInexistente), any(ProdutoUpdateDTO.class)))
                    .thenThrow(new EntityNotFoundException("Produto com id: " + idInexistente + " não encontrado"));

            mockMvc.perform(put("/produtos/{id}", idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(produtoUpdateDTO)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Produto com id: " + idInexistente + " não encontrado")));

            verify(produtoService, times(1)).atualizarProduto(eq(idInexistente), any(ProdutoUpdateDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /produtos/{id}")
    class RemoverProdutoTest {

        @Test
        @DisplayName("Deve remover produto com sucesso e retornar 204")
        void deveRemoverProdutoComSucesso() throws Exception {
            doNothing().when(produtoService).deletarProduto(PRODUTO_ID);

            mockMvc.perform(delete("/produtos/{id}", PRODUTO_ID))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(produtoService, times(1)).deletarProduto(PRODUTO_ID);
        }

        @Test
        @DisplayName("Deve retornar 404 ao remover quando ID não existir")
        void deveRetornar404AoRemoverQuandoIdNaoExistir() throws Exception {
            String idInexistente = "id-inexistente";
            doThrow(new EntityNotFoundException("Produto com id: " + idInexistente + " não encontrado"))
                    .when(produtoService).deletarProduto(idInexistente);

            mockMvc.perform(delete("/produtos/{id}", idInexistente))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Produto com id: " + idInexistente + " não encontrado")));

            verify(produtoService, times(1)).deletarProduto(idInexistente);
        }
    }
}