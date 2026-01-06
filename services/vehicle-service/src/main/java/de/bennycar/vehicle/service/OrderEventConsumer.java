package de.bennycar.vehicle.service;

import de.bennycar.api.order.event.OrderCreatedEvent;
import de.bennycar.vehicle.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final VehicleService vehicleService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consume(OrderCreatedEvent event) {
        LOGGER.info("Received order event -> {}", event);
        try {
            vehicleService.markAsOrdered(event.getVehicleId());
        } catch (Exception e) {
            LOGGER.error("Failed to process order event for vehicle: {}", event.getVehicleId(), e);
        }
    }
}