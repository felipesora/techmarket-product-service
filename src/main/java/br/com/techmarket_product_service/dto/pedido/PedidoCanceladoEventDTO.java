package br.com.techmarket_product_service.dto.pedido;

import java.util.List;

public record PedidoCanceladoEventDTO (
        Long pedidoId,
        List<ItemPedidoEventDTO> itens
) {
}
