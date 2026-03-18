package br.com.techmarket_product_service.amqp;

import br.com.techmarket_product_service.dto.pedido.ItemPedidoEventDTO;
import br.com.techmarket_product_service.dto.pedido.PedidoCanceladoEventDTO;
import br.com.techmarket_product_service.dto.pedido.PedidoCriadoEventDTO;
import br.com.techmarket_product_service.service.ProdutoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;

@Component
public class PedidoListener {

    private final ProdutoService produtoService;

    public PedidoListener(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @RabbitListener(queues = "pedido.criado")
    public void recebePedidoCriado(PedidoCriadoEventDTO evento) {
        System.out.println("Mensagem recebida da fila de pedidos criados");
        System.out.println("Conteúdo: " + evento);

        processarItens(evento.itens(), true);
    }

    @RabbitListener(queues = "pedido.cancelado")
    public void recebePedidoCancelado(PedidoCanceladoEventDTO evento) {
        System.out.println("Mensagem recebida da fila de pedidos cancelados");
        System.out.println("Conteúdo: " + evento);

        processarItens(evento.itens(), false);
    }

    private void processarItens(List<ItemPedidoEventDTO> itens, boolean baixarEstoque) {
        for (ItemPedidoEventDTO item: itens) {

            if (baixarEstoque) {
                produtoService.baixarEstoque(item.produtoIdMongo(), item.quantidade());
            } else {
                produtoService.devolverEstoque(item.produtoIdMongo(), item.quantidade());
            }

            System.out.println("Estoque do produto " + item.produtoIdMongo() + " atualizado!");
        }
    }
}
