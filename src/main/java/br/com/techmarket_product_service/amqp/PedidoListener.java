package br.com.techmarket_product_service.amqp;

import br.com.techmarket_product_service.dto.pedido.ItemPedidoEventDTO;
import br.com.techmarket_product_service.dto.pedido.PedidoCriadoEventDTO;
import br.com.techmarket_product_service.service.ProdutoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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

        for (ItemPedidoEventDTO item: evento.itens()) {
            produtoService.baixarEstoque(item.produtoIdMongo(), item.quantidade());
            System.out.println("Estoque do produto " + item.produtoIdMongo() + " atualizado!");
        }
    }
}
