package de.bennycar.vehicle.service;

import de.bennycar.vehicle.config.RabbitMQConfig;
import de.bennycar.vehicle.dto.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consume(OrderCreatedEvent event) {
        LOGGER.info(String.format("Received order event -> %s", event.toString()));
        // TODO: Implement logic to update vehicle availability based on the order
    }
}

