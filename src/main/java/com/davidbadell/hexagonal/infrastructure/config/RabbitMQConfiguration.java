package com.davidbadell.hexagonal.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Infrastructure Layer: Messaging Configuration
 */
@Configuration
public class RabbitMQConfiguration {
    
    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_CANCELLED_QUEUE = "order.cancelled.queue";

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue orderCancelledQueue() {
        return new Queue(ORDER_CANCELLED_QUEUE, true);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(orderEventsExchange)
                .with("ordercreated");
    }

    @Bean
    public Binding orderCancelledBinding(Queue orderCancelledQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(orderCancelledQueue)
                .to(orderEventsExchange)
                .with("ordercancelled");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
