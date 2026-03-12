package br.com.techmarket_product_service.dto.produto;

public record ProdutoResponseDTO(
        String id,
        String nome,
        String descricao,
        Double preco,
        Integer estoque
) {}
