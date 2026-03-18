package br.com.techmarket_product_service.dto.pedido;

public record ItemPedidoEventDTO(
        String produtoIdMongo,
        Integer quantidade
) {
}
