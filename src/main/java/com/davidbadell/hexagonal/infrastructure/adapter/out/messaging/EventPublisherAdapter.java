package com.davidbadell.hexagonal.infrastructure.adapter.out.messaging;

import com.davidbadell.hexagonal.application.port.out.EventPublisher;
import com.davidbadell.hexagonal.domain.event.DomainEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Event Publisher Adapter using RabbitMQ
 * Hexagonal Architecture: Output Adapter (Driven Adapter)
 * 
 * This adapter implements the EventPublisher port using RabbitMQ.
 */
@Component
public class EventPublisherAdapter implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherAdapter.class);
    private static final String DEFAULT_EXCHANGE = "order.events";
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void publish(DomainEvent event) {
        publish(event, DEFAULT_EXCHANGE);
    }

    @Override
    public void publish(DomainEvent event, String topic) {
        try {
            String routingKey = event.getEventType().toLowerCase();
            String message = objectMapper.writeValueAsString(event);
            
            rabbitTemplate.convertAndSend(topic, routingKey, message);
            
            logger.info("Published event {} to exchange {} with routing key {}", 
                    event.getEventType(), topic, routingKey);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event {}: {}", event.getEventType(), e.getMessage());
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
