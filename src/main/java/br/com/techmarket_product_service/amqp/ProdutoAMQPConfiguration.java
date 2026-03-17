package br.com.techmarket_product_service.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProdutoAMQPConfiguration {

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    // Config Producer
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("produto.exchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("produto.dlx");
    }

    // Config Consumer
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setDefaultRequeueRejected(false);

        return factory;
    }

    @Bean
    public Queue filaPedidosCriados() {
        return QueueBuilder
                .durable("pedido.criado")
                .withArgument("x-dead-letter-exchange", "pedido.dlx")
                .withArgument("x-dead-letter-routing-key", "pedido.criado.dlq")
                .build();
    }

    @Bean
    public Queue filaPedidosCriadosDLQ() {
        return QueueBuilder.durable("pedido.criado.dlq").build();
    }

    @Bean
    public TopicExchange pedidoTopicExchange() {
        return ExchangeBuilder.topicExchange("pedido.exchange").build();
    }

    @Bean
    public DirectExchange pedidoDeadLetterExchange() {
        return ExchangeBuilder.directExchange("pedido.dlx").build();
    }

    @Bean
    public Binding bindPedidosCriados(Queue filaPedidosCriados, TopicExchange pedidoTopicExchange) {
        return BindingBuilder
                .bind(filaPedidosCriados)
                .to(pedidoTopicExchange)
                .with("pedido.criado");
    }

    @Bean
    public Binding bindDLQPedidosCriados(Queue filaPedidosCriadosDLQ, DirectExchange pedidoDeadLetterExchange) {
        return BindingBuilder.bind(filaPedidosCriadosDLQ)
                .to(pedidoDeadLetterExchange)
                .with("pedido.criado.dlq");
    }

}
