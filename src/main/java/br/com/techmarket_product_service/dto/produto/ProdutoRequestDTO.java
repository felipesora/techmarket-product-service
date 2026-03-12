package br.com.techmarket_product_service.dto.produto;

public record ProdutoRequestDTO (
        String nome,
        String descricao,
        Double preco,
        Integer estoque
) {}
