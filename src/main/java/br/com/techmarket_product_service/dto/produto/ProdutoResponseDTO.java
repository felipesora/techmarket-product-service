package br.com.techmarket_product_service.dto.produto;

import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;

import java.time.LocalDateTime;

public record ProdutoResponseDTO(
        String id,
        String codigo,
        String nome,
        String descricao,
        CategoriaProduto categoria,
        String marca,
        Double preco,
        Integer estoque,
        StatusProduto status,
        LocalDateTime dataCriacao
) {}
