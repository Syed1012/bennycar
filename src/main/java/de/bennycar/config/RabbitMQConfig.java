package de.bennycar.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * AMQP / RabbitMQ Configuration Class
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This class demonstrates key AMQP concepts:
 * 1. EXCHANGES - Route messages to queues based on routing rules
 * 2. QUEUES - Store messages until consumers process them
 * 3. BINDINGS - Connect exchanges to queues with routing keys
 * 4. DEAD LETTER EXCHANGE (DLX) - Handle failed/rejected messages
 * 5. MESSAGE CONVERTER - Serialize/deserialize messages (JSON)
 * 6. RABBIT TEMPLATE - Send messages to RabbitMQ
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Configuration
public class RabbitMQConfig {

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 1: QUEUE NAMES - Define constants for queue identification
    // ═══════════════════════════════════════════════════════════════════════
    // Purpose: Centralize queue names to avoid typos and make refactoring easier
    // These are logical names that RabbitMQ uses to store messages

    /** Main queue for car-related events (create, update, delete) */
    public static final String CAR_EVENTS_QUEUE = "car.events.queue";

    /** Queue for handling car price change notifications */
    public static final String CAR_PRICE_ALERT_QUEUE = "car.price.alert.queue";

    /** Queue for inventory updates when car availability changes */
    public static final String CAR_INVENTORY_QUEUE = "car.inventory.queue";

    /** Dead Letter Queue - stores messages that failed processing */
    public static final String CAR_EVENTS_DLQ = "car.events.dlq";

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 2: EXCHANGE NAMES & TYPES
    // ═══════════════════════════════════════════════════════════════════════
    // Purpose: Exchanges route messages to queues based on routing patterns
    // Think of exchanges as "post offices" that deliver messages to "mailboxes" (queues)

    /**
     * DIRECT EXCHANGE - Routes messages to queues based on EXACT routing key match
     * Use case: When you want specific message types to go to specific queues
     * Example: routing_key="car.created" goes ONLY to queues bound with "car.created"
     */
    public static final String CAR_DIRECT_EXCHANGE = "car.direct.exchange";

    /**
     * TOPIC EXCHANGE - Routes messages using PATTERN matching with wildcards
     * Use case: When you want flexible routing based on patterns
     * Wildcards:
     *   * (star) = matches exactly ONE word
     *   # (hash) = matches ZERO or MORE words
     * Example:
     *   - routing_key="car.created.luxury" matches pattern "car.*.luxury"
     *   - routing_key="car.updated.sedan.tesla" matches pattern "car.#"
     */
    public static final String CAR_TOPIC_EXCHANGE = "car.topic.exchange";

    /**
     * FANOUT EXCHANGE - Broadcasts messages to ALL bound queues (ignores routing key)
     * Use case: Pub/Sub pattern - notify all subscribers simultaneously
     * Example: When a new car is added, notify inventory, analytics, and notification services
     */
    public static final String CAR_FANOUT_EXCHANGE = "car.fanout.exchange";

    /**
     * DEAD LETTER EXCHANGE (DLX) - Receives failed/rejected/expired messages
     * Purpose: Handle errors gracefully, analyze failures, retry logic
     */
    public static final String CAR_DLX_EXCHANGE = "car.dlx.exchange";

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 3: ROUTING KEYS
    // ═══════════════════════════════════════════════════════════════════════
    // Purpose: Specify HOW messages are routed from exchange to queue
    // Format convention: use dots (.) to separate hierarchy levels
    // Example: "resource.action.details" → "car.created.luxury"

    public static final String CAR_CREATED_KEY = "car.created";
    public static final String CAR_UPDATED_KEY = "car.updated";
    public static final String CAR_DELETED_KEY = "car.deleted";
    public static final String CAR_PRICE_CHANGED_KEY = "car.price.changed";
    public static final String CAR_AVAILABILITY_KEY = "car.availability.changed";

    /** Pattern for topic exchange - matches all car events */
    public static final String CAR_ALL_EVENTS_PATTERN = "car.#";

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 4: MESSAGE CONVERTER - JSON Serialization
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Purpose: Convert Java objects to JSON before sending, and JSON back to objects when receiving
     *
     * Why needed?
     * - RabbitMQ sends raw bytes over the network
     * - We want to send/receive Java POJOs (Plain Old Java Objects)
     * - Jackson2JsonMessageConverter handles this automatically
     *
     * Without this: You'd have to manually convert objects to byte[] and back
     * With this: Just send Car object, RabbitMQ handles serialization
     *
     * Example:
     *   Car car = new Car("Tesla", "Model 3");
     *   rabbitTemplate.convertAndSend(exchange, routingKey, car); // Auto-converts to JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 5: RABBIT TEMPLATE - Message Producer
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Purpose: Main interface for SENDING messages to RabbitMQ
     *
     * What it does:
     * - Establishes connection to RabbitMQ broker
     * - Converts objects to messages using MessageConverter
     * - Publishes messages to specified exchange with routing key
     * - Handles connection pooling and error handling
     *
     * Usage in your code:
     *   rabbitTemplate.convertAndSend("exchange.name", "routing.key", messageObject);
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 6: EXCHANGE DECLARATIONS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 6.1 - DIRECT EXCHANGE Declaration
     *
     * Creates a direct exchange in RabbitMQ with these properties:
     * - durable(true): Exchange survives RabbitMQ restart (persisted to disk)
     * - durable(false): Exchange deleted when RabbitMQ restarts (in-memory only)
     *
     * Use Direct Exchange when:
     * - You need exact routing key matches
     * - Point-to-point message delivery
     * - Example: Payment notifications go to payment-queue only
     */
    @Bean
    public DirectExchange carDirectExchange() {
        return new DirectExchange(CAR_DIRECT_EXCHANGE, true, false);
        //                        name ↑              durable ↑  auto-delete ↑
        // durable=true: Exchange persists across RabbitMQ restarts
        // auto-delete=false: Exchange NOT deleted when last queue unbinds
    }

    /**
     * 6.2 - TOPIC EXCHANGE Declaration
     *
     * Creates a topic exchange for pattern-based routing
     *
     * Use Topic Exchange when:
     * - You need wildcard pattern matching
     * - Hierarchical routing (logs.error.*, logs.#)
     * - Multiple consumers interested in different patterns
     *
     * Example routing patterns:
     *   - "car.*" matches "car.created", "car.updated" but NOT "car.created.luxury"
     *   - "car.#" matches "car.created", "car.created.luxury", "car.updated.price.high"
     */
    @Bean
    public TopicExchange carTopicExchange() {
        return new TopicExchange(CAR_TOPIC_EXCHANGE, true, false);
    }

    /**
     * 6.3 - FANOUT EXCHANGE Declaration
     *
     * Creates a fanout exchange that broadcasts to ALL bound queues
     *
     * Use Fanout Exchange when:
     * - Broadcasting to multiple subscribers (Pub/Sub pattern)
     * - Same message needs to go to different services
     * - Routing key is ignored (broadcasts to everyone)
     *
     * Example use case:
     *   When car is added:
     *   - Inventory service updates stock
     *   - Analytics service logs event
     *   - Notification service alerts users
     *   - All receive the SAME message simultaneously
     */
    @Bean
    public FanoutExchange carFanoutExchange() {
        return new FanoutExchange(CAR_FANOUT_EXCHANGE, true, false);
    }

    /**
     * 6.4 - DEAD LETTER EXCHANGE (DLX) Declaration
     *
     * Purpose: Handle messages that failed processing
     *
     * When does a message go to DLX?
     * 1. Consumer rejects message with requeue=false
     * 2. Message TTL (Time To Live) expires
     * 3. Queue length limit exceeded
     * 4. Consumer throws exception and doesn't acknowledge
     *
     * Why use DLX?
     * - Debug failed messages
     * - Implement retry logic
     * - Prevent message loss
     * - Analyze error patterns
     */
    @Bean
    public DirectExchange carDeadLetterExchange() {
        return new DirectExchange(CAR_DLX_EXCHANGE, true, false);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 7: QUEUE DECLARATIONS WITH ADVANCED PROPERTIES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 7.1 - MAIN EVENTS QUEUE with Dead Letter Queue (DLQ) Configuration
     *
     * This queue demonstrates several important AMQP concepts:
     *
     * A. DURABLE QUEUE (durable=true)
     *    - Queue survives RabbitMQ restart
     *    - Messages are persisted to disk
     *    - Critical for production systems
     *
     * B. DEAD LETTER EXCHANGE (x-dead-letter-exchange)
     *    - When message fails, send it to DLX instead of discarding
     *    - Allows error handling and retry logic
     *
     * C. DEAD LETTER ROUTING KEY (x-dead-letter-routing-key)
     *    - Specifies routing key when sending to DLX
     *    - Helps categorize different failure types
     *
     * D. MESSAGE TTL (x-message-ttl)
     *    - Messages expire after specified milliseconds
     *    - Expired messages go to DLX if configured
     *    - Prevents queue from holding stale messages forever
     *    - 60000ms = 60 seconds
     *
     * E. MAX LENGTH (x-max-length)
     *    - Queue holds maximum N messages
     *    - Oldest messages dropped (or sent to DLX) when limit reached
     *    - Prevents memory overflow
     */
    @Bean
    public Queue carEventsQueue() {
        return QueueBuilder.durable(CAR_EVENTS_QUEUE)
                // PROPERTY 1: Dead Letter Exchange - where failed messages go
                .withArgument("x-dead-letter-exchange", CAR_DLX_EXCHANGE)

                // PROPERTY 2: Routing key for failed messages sent to DLX
                .withArgument("x-dead-letter-routing-key", "car.events.failed")

                // PROPERTY 3: Message Time-To-Live - messages expire after 60 seconds
                // Use case: Price alerts are only relevant for short time
                .withArgument("x-message-ttl", 60000) // 60 seconds

                // PROPERTY 4: Max queue length - holds max 10,000 messages
                // Prevents queue from growing infinitely
                .withArgument("x-max-length", 10000)

                .build();
    }

    /**
     * 7.2 - PRICE ALERT QUEUE
     *
     * Simpler queue for price change notifications
     * - Durable: survives restarts
     * - No DLX: failures are logged but not reprocessed
     * - No TTL: price history kept indefinitely
     */
    @Bean
    public Queue carPriceAlertQueue() {
        return QueueBuilder.durable(CAR_PRICE_ALERT_QUEUE).build();
    }

    /**
     * 7.3 - INVENTORY QUEUE
     *
     * Tracks car availability changes (available/sold)
     */
    @Bean
    public Queue carInventoryQueue() {
        return QueueBuilder.durable(CAR_INVENTORY_QUEUE).build();
    }

    /**
     * 7.4 - DEAD LETTER QUEUE (DLQ)
     *
     * Purpose: Store all failed messages from other queues
     *
     * Characteristics:
     * - Durable: Don't lose failure information
     * - No DLX: Terminal queue (no further routing)
     * - No TTL: Keep failures for analysis
     *
     * How to use:
     * 1. Monitor this queue for errors
     * 2. Manually inspect failed messages
     * 3. Fix bugs in consumer code
     * 4. Optionally replay messages after fix
     */
    @Bean
    public Queue carEventsDeadLetterQueue() {
        return QueueBuilder.durable(CAR_EVENTS_DLQ).build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 8: BINDINGS - Connect Exchanges to Queues
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Bindings define the ROUTING RULES between exchanges and queues
     *
     * Flow: Producer → Exchange → Binding (routing rules) → Queue → Consumer
     *
     * Each binding specifies:
     * - Which queue receives messages
     * - From which exchange
     * - Using which routing key/pattern
     */

    /**
     * 8.1 - DIRECT EXCHANGE BINDINGS
     *
     * Direct bindings require EXACT routing key match
     *
     * Example flow:
     *   Producer sends: exchange="car.direct.exchange", routingKey="car.created"
     *   This binding matches: delivers to carEventsQueue
     *   Producer sends: exchange="car.direct.exchange", routingKey="car.deleted"
     *   No binding matches: message is DISCARDED (important!)
     */
    @Bean
    public Binding bindingCarCreated(Queue carEventsQueue, DirectExchange carDirectExchange) {
        return BindingBuilder
                .bind(carEventsQueue)           // Destination queue
                .to(carDirectExchange)          // Source exchange
                .with(CAR_CREATED_KEY);         // Exact routing key: "car.created"
    }

    @Bean
    public Binding bindingCarUpdated(Queue carEventsQueue, DirectExchange carDirectExchange) {
        return BindingBuilder
                .bind(carEventsQueue)
                .to(carDirectExchange)
                .with(CAR_UPDATED_KEY);         // Exact routing key: "car.updated"
    }

    @Bean
    public Binding bindingCarDeleted(Queue carEventsQueue, DirectExchange carDirectExchange) {
        return BindingBuilder
                .bind(carEventsQueue)
                .to(carDirectExchange)
                .with(CAR_DELETED_KEY);         // Exact routing key: "car.deleted"
    }

    /**
     * 8.2 - TOPIC EXCHANGE BINDINGS with WILDCARD PATTERNS
     *
     * Topic bindings use pattern matching:
     * - * (asterisk) matches EXACTLY ONE word
     * - # (hash) matches ZERO or MORE words
     *
     * Example pattern matching:
     *   Pattern: "car.#"
     *   ✓ Matches: "car.created"
     *   ✓ Matches: "car.updated.price"
     *   ✓ Matches: "car.deleted.luxury.tesla"
     *   ✗ Does NOT match: "vehicle.created"
     *
     * Use case: One queue receives ALL car events with single binding
     */
    @Bean
    public Binding bindingCarAllEventsTopic(Queue carEventsQueue, TopicExchange carTopicExchange) {
        return BindingBuilder
                .bind(carEventsQueue)
                .to(carTopicExchange)
                .with(CAR_ALL_EVENTS_PATTERN);  // Pattern: "car.#" matches all car.* keys
    }

    /**
     * 8.3 - SPECIFIC TOPIC BINDING for Price Changes
     *
     * Example: Only price change events go to price alert queue
     *   Pattern: "car.price.changed"
     *   ✓ Matches: "car.price.changed"
     *   ✗ Does NOT match: "car.created"
     */
    @Bean
    public Binding bindingCarPriceAlert(Queue carPriceAlertQueue, TopicExchange carTopicExchange) {
        return BindingBuilder
                .bind(carPriceAlertQueue)
                .to(carTopicExchange)
                .with(CAR_PRICE_CHANGED_KEY);   // Only price changes
    }

    /**
     * 8.4 - SPECIFIC TOPIC BINDING for Inventory Changes
     *
     * Only availability changes (sold/available) go to inventory queue
     */
    @Bean
    public Binding bindingCarInventory(Queue carInventoryQueue, TopicExchange carTopicExchange) {
        return BindingBuilder
                .bind(carInventoryQueue)
                .to(carTopicExchange)
                .with(CAR_AVAILABILITY_KEY);    // Only availability changes
    }

    /**
     * 8.5 - FANOUT EXCHANGE BINDINGS
     *
     * Fanout exchanges IGNORE routing keys and broadcast to ALL bound queues
     *
     * Example flow:
     *   Producer sends to fanout exchange with ANY routing key
     *   ALL three queues receive the message:
     *   - carEventsQueue
     *   - carPriceAlertQueue
     *   - carInventoryQueue
     *
     * Use case: System-wide announcements, cache invalidation, logging
     */
    @Bean
    public Binding bindingFanoutEvents(Queue carEventsQueue, FanoutExchange carFanoutExchange) {
        return BindingBuilder.bind(carEventsQueue).to(carFanoutExchange);
        // No routing key needed - fanout broadcasts to everyone
    }

    @Bean
    public Binding bindingFanoutPrice(Queue carPriceAlertQueue, FanoutExchange carFanoutExchange) {
        return BindingBuilder.bind(carPriceAlertQueue).to(carFanoutExchange);
    }

    @Bean
    public Binding bindingFanoutInventory(Queue carInventoryQueue, FanoutExchange carFanoutExchange) {
        return BindingBuilder.bind(carInventoryQueue).to(carFanoutExchange);
    }

    /**
     * 8.6 - DEAD LETTER QUEUE BINDING
     *
     * Connect DLX (Dead Letter Exchange) to DLQ (Dead Letter Queue)
     *
     * Flow of failed message:
     *   1. Consumer fails to process message from carEventsQueue
     *   2. Message rejected or TTL expires
     *   3. RabbitMQ sends message to DLX (x-dead-letter-exchange property)
     *   4. DLX routes to DLQ using this binding
     *   5. Failed message stored in DLQ for analysis
     *
     * Routing key "car.events.failed" matches what we configured in queue properties
     */
    @Bean
    public Binding bindingDeadLetterQueue(Queue carEventsDeadLetterQueue,
                                         DirectExchange carDeadLetterExchange) {
        return BindingBuilder
                .bind(carEventsDeadLetterQueue)
                .to(carDeadLetterExchange)
                .with("car.events.failed");     // Must match x-dead-letter-routing-key
    }
}

