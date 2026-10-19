package br.com.fiap.mercadomvc.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange principal para transações bancárias entre Mercado Express e Banco Tranquilo
    public static final String EXCHANGE_BANCO_TRANQUILO = "mercado.banco.exchange";

    // Queue 1: Requisições de pagamento enviadas pelo Mercado Express para a Integração de Pagamentos / Banco Tranquilo
    public static final String QUEUE_PAGAMENTO_REQUISICAO = "banco.tranquilo.pagamento.requisicao.queue";
    public static final String ROUTING_KEY_PAGAMENTO_REQUISICAO = "banco.pagamento.requisicao";

    // Queue 2: Respostas assíncronas da Integração de Pagamentos (Banco Tranquilo) de volta para o Mercado Express
    public static final String QUEUE_PAGAMENTO_RESPOSTA = "banco.tranquilo.pagamento.resposta.queue";
    public static final String ROUTING_KEY_PAGAMENTO_RESPOSTA = "banco.pagamento.resposta";

    @Bean
    public DirectExchange bancoTranquiloExchange() {
        return new DirectExchange(EXCHANGE_BANCO_TRANQUILO, true, false);
    }

    @Bean
    public Queue pagamentoRequisicaoQueue() {
        return QueueBuilder.durable(QUEUE_PAGAMENTO_REQUISICAO).build();
    }

    @Bean
    public Queue pagamentoRespostaQueue() {
        return QueueBuilder.durable(QUEUE_PAGAMENTO_RESPOSTA).build();
    }

    @Bean
    public Binding bindingPagamentoRequisicao(Queue pagamentoRequisicaoQueue, DirectExchange bancoTranquiloExchange) {
        return BindingBuilder
                .bind(pagamentoRequisicaoQueue)
                .to(bancoTranquiloExchange)
                .with(ROUTING_KEY_PAGAMENTO_REQUISICAO);
    }

    @Bean
    public Binding bindingPagamentoResposta(Queue pagamentoRespostaQueue, DirectExchange bancoTranquiloExchange) {
        return BindingBuilder
                .bind(pagamentoRespostaQueue)
                .to(bancoTranquiloExchange)
                .with(ROUTING_KEY_PAGAMENTO_RESPOSTA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
