package br.com.techmarket_product_service.dto.pedido;

import br.com.techmarket_product_service.model.enums.MetodoPagamentoPedido;

import java.math.BigDecimal;
import java.util.List;

public record PedidoCriadoEventDTO(
        Long pedidoId,
        BigDecimal valorTotal,
        MetodoPagamentoPedido metodoPagamento,
        List<ItemPedidoEventDTO> itens
) {
}
