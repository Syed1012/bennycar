package de.bennycar.messaging;

import de.bennycar.config.RabbitMQConfig;
import de.bennycar.dto.CarEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * MESSAGE PRODUCER SERVICE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Purpose: SEND messages to RabbitMQ exchanges
 *
 * AMQP Message Flow:
 *   Producer (this class)
 *     → RabbitTemplate
 *     → Exchange (routing logic)
 *     → Queue (message storage)
 *     → Consumer (message processing)
 *
 * Key Concepts Demonstrated:
 * 1. EXCHANGE TYPES - Direct, Topic, Fanout routing
 * 2. ROUTING KEYS - How messages find the right queue
 * 3. MESSAGE PUBLISHING - Sending objects as JSON
 * 4. FIRE-AND-FORGET - Async messaging pattern
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Service
public class CarEventProducer {

    private static final Logger log = LoggerFactory.getLogger(CarEventProducer.class);

    /**
     * RABBIT TEMPLATE - Spring's interface for sending messages
     *
     * What it does:
     * 1. Connects to RabbitMQ broker (localhost:5672)
     * 2. Converts Java objects to JSON (via MessageConverter)
     * 3. Sends message to specified exchange with routing key
     * 4. Handles connection pooling and error handling
     *
     * Injected by Spring automatically from RabbitMQConfig
     */
    private final RabbitTemplate rabbitTemplate;

    public CarEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 1: DIRECT EXCHANGE PUBLISHING
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Publish to DIRECT EXCHANGE with exact routing key
     *
     * How it works:
     * 1. Message sent to exchange: "car.direct.exchange"
     * 2. With routing key (e.g., "car.created")
     * 3. Exchange looks for binding with EXACT matching key
     * 4. Delivers message to bound queue(s)
     * 5. If no binding matches → MESSAGE IS DISCARDED!
     *
     * Use case: Point-to-point messaging
     * - Specific event types go to specific queues
     * - Example: payment.completed → payment-queue only
     *
     * @param message The event data to send
     * @param routingKey Exact key to match (car.created, car.updated, etc.)
     */
    public void sendToDirectExchange(CarEventMessage message, String routingKey) {
        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("📤 SENDING to DIRECT EXCHANGE");
            log.info("Exchange: {}", RabbitMQConfig.CAR_DIRECT_EXCHANGE);
            log.info("Routing Key: {}", routingKey);
            log.info("Message: {}", message);
            log.info("═══════════════════════════════════════════════════════");

            // SEND MESSAGE - This is the core AMQP operation!
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CAR_DIRECT_EXCHANGE,  // Which exchange to send to
                    routingKey,                           // Routing key for matching
                    message                               // Java object (auto-converted to JSON)
            );

            log.info("✅ Message sent successfully to direct exchange");

        } catch (Exception e) {
            log.error("❌ Failed to send message to direct exchange", e);
            // In production: Consider retry logic, DLQ, or alerting
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 2: TOPIC EXCHANGE PUBLISHING
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Publish to TOPIC EXCHANGE with pattern-based routing
     *
     * How it works:
     * 1. Message sent with hierarchical routing key (e.g., "car.price.changed")
     * 2. Exchange matches against patterns in bindings:
     *    - Pattern "car.*" matches "car.created" but NOT "car.price.changed"
     *    - Pattern "car.#" matches ALL car.* keys (zero or more words)
     *    - Pattern "*.price.*" matches "car.price.changed", "bike.price.updated"
     * 3. Message delivered to ALL queues with matching patterns
     * 4. Multiple queues can receive the SAME message
     *
     * Use case: Flexible routing, pub/sub with selective interest
     * - Log aggregation: logs.error.*, logs.warning.*, logs.#
     * - Monitoring: system.cpu.high, system.memory.low → system.#
     *
     * @param message The event data to send
     * @param routingKey Hierarchical key (use dots: resource.action.detail)
     */
    public void sendToTopicExchange(CarEventMessage message, String routingKey) {
        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("📤 SENDING to TOPIC EXCHANGE");
            log.info("Exchange: {}", RabbitMQConfig.CAR_TOPIC_EXCHANGE);
            log.info("Routing Key: {}", routingKey);
            log.info("Message: {}", message);
            log.info("Matching patterns:");
            log.info("  - 'car.#' will match (all car events)");
            log.info("  - 'car.*' will match only if routing key has 2 words");
            log.info("  - Exact key '{}' will match", routingKey);
            log.info("═══════════════════════════════════════════════════════");

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CAR_TOPIC_EXCHANGE,
                    routingKey,
                    message
            );

            log.info("✅ Message sent successfully to topic exchange");

        } catch (Exception e) {
            log.error("❌ Failed to send message to topic exchange", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 3: FANOUT EXCHANGE PUBLISHING
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Publish to FANOUT EXCHANGE (broadcast to all)
     *
     * How it works:
     * 1. Message sent to fanout exchange (routing key IGNORED!)
     * 2. Exchange broadcasts to ALL bound queues simultaneously
     * 3. Every consumer receives a copy of the message
     * 4. Fire-and-forget pattern
     *
     * Think of it like:
     * - Radio broadcast: Everyone tuned in receives the signal
     * - Email CC: Same message to multiple recipients
     * - Cache invalidation: Notify all servers to clear cache
     *
     * Use case: System-wide notifications
     * - New car added → notify inventory, analytics, search index, notifications
     * - Configuration change → all microservices update
     * - Emergency shutdown → all workers stop gracefully
     *
     * Important: Each bound queue gets its OWN COPY
     * - If 3 queues bound → 3 copies created
     * - Each consumer processes independently
     * - One failing doesn't affect others
     *
     * @param message The event data to broadcast
     */
    public void sendToFanoutExchange(CarEventMessage message) {
        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("📤 BROADCASTING to FANOUT EXCHANGE");
            log.info("Exchange: {}", RabbitMQConfig.CAR_FANOUT_EXCHANGE);
            log.info("Routing Key: (IGNORED - broadcasts to all bound queues)");
            log.info("Message: {}", message);
            log.info("Bound queues will ALL receive this message:");
            log.info("  - {}", RabbitMQConfig.CAR_EVENTS_QUEUE);
            log.info("  - {}", RabbitMQConfig.CAR_PRICE_ALERT_QUEUE);
            log.info("  - {}", RabbitMQConfig.CAR_INVENTORY_QUEUE);
            log.info("═══════════════════════════════════════════════════════");

            // Routing key is ignored but convention is to pass empty string or null
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CAR_FANOUT_EXCHANGE,
                    "",      // Routing key ignored for fanout
                    message
            );

            log.info("✅ Message broadcasted successfully to all subscribers");

        } catch (Exception e) {
            log.error("❌ Failed to broadcast message to fanout exchange", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONVENIENCE METHODS - Simplified API for common operations
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Send CAR CREATED event
     * Uses direct exchange with specific routing key
     */
    public void sendCarCreatedEvent(CarEventMessage message) {
        message.setEventType("CREATED");
        sendToDirectExchange(message, RabbitMQConfig.CAR_CREATED_KEY);
    }

    /**
     * Send CAR UPDATED event
     * Uses direct exchange with specific routing key
     */
    public void sendCarUpdatedEvent(CarEventMessage message) {
        message.setEventType("UPDATED");
        sendToDirectExchange(message, RabbitMQConfig.CAR_UPDATED_KEY);
    }

    /**
     * Send CAR DELETED event
     * Uses direct exchange with specific routing key
     */
    public void sendCarDeletedEvent(CarEventMessage message) {
        message.setEventType("DELETED");
        sendToDirectExchange(message, RabbitMQConfig.CAR_DELETED_KEY);
    }

    /**
     * Send PRICE CHANGED event
     * Uses topic exchange - will be received by:
     * - Queues bound to exact key "car.price.changed"
     * - Queues bound to pattern "car.#" (all car events)
     * - Queues bound to pattern "*.price.*" (all price changes)
     */
    public void sendPriceChangedEvent(CarEventMessage message) {
        message.setEventType("PRICE_CHANGED");
        sendToTopicExchange(message, RabbitMQConfig.CAR_PRICE_CHANGED_KEY);
    }

    /**
     * Send AVAILABILITY CHANGED event (sold/available)
     * Uses topic exchange for flexible routing
     */
    public void sendAvailabilityChangedEvent(CarEventMessage message) {
        message.setEventType("AVAILABILITY_CHANGED");
        sendToTopicExchange(message, RabbitMQConfig.CAR_AVAILABILITY_KEY);
    }

    /**
     * Broadcast to ALL subscribed services
     * Use for important system-wide events
     */
    public void broadcastCarEvent(CarEventMessage message) {
        sendToFanoutExchange(message);
    }
}

