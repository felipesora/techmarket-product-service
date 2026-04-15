package br.com.techmarket_product_service.dto.produto;

import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponseDTO(
        String id,
        String codigo,
        String nome,
        String descricao,
        CategoriaProduto categoria,
        String marca,
        BigDecimal preco,
        Integer estoque,
        StatusProduto status,
        LocalDateTime dataCriacao,
        String imagemId
) {}
