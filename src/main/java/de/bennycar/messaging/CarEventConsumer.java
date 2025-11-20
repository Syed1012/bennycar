package de.bennycar.messaging;

import com.rabbitmq.client.Channel;
import de.bennycar.config.RabbitMQConfig;
import de.bennycar.dto.CarEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * MESSAGE CONSUMER SERVICE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Purpose: RECEIVE and PROCESS messages from RabbitMQ queues
 *
 * Key AMQP Concepts Demonstrated:
 * 1. @RabbitListener - Subscribes to queue and consumes messages
 * 2. MANUAL ACKNOWLEDGEMENT - Control when message is removed from queue
 * 3. PREFETCH - How many messages to fetch at once
 * 4. CONCURRENCY - Multiple consumers processing in parallel
 * 5. ERROR HANDLING - Retry, reject, send to DLQ
 * 6. MESSAGE HEADERS - Access metadata like retry count, timestamp
 *
 * Consumer Flow:
 *   Queue → RabbitMQ delivers message → @RabbitListener method → Process → ACK/NACK
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Service
public class CarEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CarEventConsumer.class);

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 1: BASIC CONSUMER with @RabbitListener
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Main event consumer for car events (created, updated, deleted)
     *
     * @RabbitListener annotation tells Spring to:
     * 1. Connect to RabbitMQ
     * 2. Subscribe to specified queue
     * 3. Call this method whenever message arrives
     * 4. Automatically deserialize JSON to CarEventMessage object
     *
     * Parameters explained:
     *
     * A. queues = "car.events.queue"
     *    - Which queue to consume from
     *    - Must match queue name in RabbitMQConfig
     *
     * B. concurrency = "3-10"
     *    - Minimum 3 concurrent consumers always running
     *    - Maximum 10 consumers during high load
     *    - More consumers = faster processing BUT more resources
     *    - Example: 100 messages in queue
     *      • 1 consumer: processes 1 at a time (slow)
     *      • 10 consumers: processes 10 simultaneously (fast)
     *
     * C. ackMode = "MANUAL"
     *    - Consumer must explicitly ACK (acknowledge) or NACK (reject) message
     *    - Gives fine-grained control over message lifecycle
     *    - AUTO mode: message removed from queue immediately (risky!)
     *    - MANUAL mode: message stays until ACK (safer, allows retries)
     *
     * Method parameters:
     *
     * @param carEvent - The deserialized message body (JSON → Java object)
     * @param message - Raw AMQP message with headers, properties, metadata
     * @param channel - Communication channel to RabbitMQ (for ACK/NACK)
     *
     * Flow:
     *   1. Message arrives in queue
     *   2. RabbitMQ delivers to available consumer
     *   3. Spring deserializes JSON to CarEventMessage
     *   4. This method executes
     *   5. Process message (business logic)
     *   6. ACK (success) or NACK (failure)
     */
    @RabbitListener(
            queues = RabbitMQConfig.CAR_EVENTS_QUEUE,
            concurrency = "3-10",
            ackMode = "MANUAL"
    )
    public void consumeCarEvent(CarEventMessage carEvent,
                               Message message,
                               Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("📥 RECEIVED MESSAGE from queue: {}", RabbitMQConfig.CAR_EVENTS_QUEUE);
            log.info("Event Type: {}", carEvent.getEventType());
            log.info("Car ID: {}", carEvent.getCarId());
            log.info("Car: {} {} (VIN: {})", carEvent.getBrand(), carEvent.getModel(), carEvent.getVin());
            log.info("Delivery Tag: {}", deliveryTag);
            log.info("Timestamp: {}", carEvent.getTimestamp());
            log.info("Message: {}", carEvent.getMessage());
            log.info("═══════════════════════════════════════════════════════");

            // ═══════════════════════════════════════════════════════════════
            // BUSINESS LOGIC - Process the message
            // ═══════════════════════════════════════════════════════════════
            // This is where you implement your actual processing
            // Examples:
            // - Update database
            // - Send email notification
            // - Update search index
            // - Call external API
            // - Generate reports

            processCarEvent(carEvent);

            // ═══════════════════════════════════════════════════════════════
            // CONCEPT 2: MANUAL ACKNOWLEDGEMENT (ACK)
            // ═══════════════════════════════════════════════════════════════
            /**
             * basicAck() tells RabbitMQ: "Message processed successfully, delete it"
             *
             * Parameters:
             * - deliveryTag: Unique ID for this message in the channel
             * - multiple: false = ACK only this message
             *             true = ACK this and all previous unacked messages
             *
             * What happens after ACK:
             * 1. Message removed from queue permanently
             * 2. Queue size decreases by 1
             * 3. Memory freed
             * 4. Consumer ready for next message
             *
             * Important: If you DON'T ACK:
             * - Message stays in queue as "unacked"
             * - RabbitMQ won't delete it
             * - If consumer disconnects, message redelivered to another consumer
             */
            channel.basicAck(deliveryTag, false);
            log.info("✅ Message ACKNOWLEDGED and removed from queue");

        } catch (Exception e) {
            log.error("❌ Error processing message", e);

            // ═══════════════════════════════════════════════════════════════
            // CONCEPT 3: ERROR HANDLING with NACK and REQUEUE
            // ═══════════════════════════════════════════════════════════════
            /**
             * Decision tree for error handling:
             *
             * 1. TEMPORARY ERROR (network timeout, database lock)
             *    → NACK with requeue=true
             *    → Message goes back to queue for retry
             *
             * 2. PERMANENT ERROR (invalid data, business rule violation)
             *    → NACK with requeue=false
             *    → Message sent to DLQ for manual inspection
             *
             * 3. RETRY LIMIT REACHED (tried 3 times already)
             *    → NACK with requeue=false
             *    → Send to DLQ, alert team
             */

            handleError(message, channel, deliveryTag, e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 4: SPECIALIZED CONSUMERS for Different Queues
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Price Alert Consumer - Handles only price change events
     *
     * Why separate consumer?
     * 1. SEPARATION OF CONCERNS - Different business logic
     * 2. INDEPENDENT SCALING - Scale price alerts separately
     * 3. DIFFERENT PRIORITIES - Critical price alerts get dedicated resources
     * 4. FAULT ISOLATION - Price alert failures don't affect other events
     *
     * Lower concurrency (1-3) because:
     * - Price alerts less frequent than general events
     * - Each alert might send email/SMS (slower operation)
     * - Don't want to overwhelm notification service
     */
    @RabbitListener(
            queues = RabbitMQConfig.CAR_PRICE_ALERT_QUEUE,
            concurrency = "1-3",
            ackMode = "MANUAL"
    )
    public void consumePriceAlert(CarEventMessage carEvent,
                                 Message message,
                                 Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("💰 PRICE ALERT RECEIVED");
            log.info("Car: {} {}", carEvent.getBrand(), carEvent.getModel());
            log.info("Old Price: ${}", carEvent.getOldPrice());
            log.info("New Price: ${}", carEvent.getPrice());

            // Calculate price change percentage
            if (carEvent.getOldPrice() != null && carEvent.getOldPrice() > 0) {
                double changePercent = ((carEvent.getPrice() - carEvent.getOldPrice())
                        / carEvent.getOldPrice()) * 100;
                log.info("Price Change: {}{}",
                        changePercent >= 0 ? "+" : "",
                        String.format("%.2f%%", changePercent));
            }
            log.info("═══════════════════════════════════════════════════════");

            // Process price alert
            // Example: Send email to users watching this car
            // Example: Update price tracking analytics
            // Example: Trigger dynamic pricing algorithm
            processPriceAlert(carEvent);

            channel.basicAck(deliveryTag, false);
            log.info("✅ Price alert processed and acknowledged");

        } catch (Exception e) {
            log.error("❌ Error processing price alert", e);
            handleError(message, channel, deliveryTag, e);
        }
    }

    /**
     * Inventory Consumer - Handles availability changes (sold/available)
     *
     * Use case:
     * - Update inventory counts
     * - Notify sales team
     * - Update website availability
     * - Trigger restocking if needed
     */
    @RabbitListener(
            queues = RabbitMQConfig.CAR_INVENTORY_QUEUE,
            concurrency = "2-5",
            ackMode = "MANUAL"
    )
    public void consumeInventoryUpdate(CarEventMessage carEvent,
                                      Message message,
                                      Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("📦 INVENTORY UPDATE RECEIVED");
            log.info("Car: {} {}", carEvent.getBrand(), carEvent.getModel());
            log.info("VIN: {}", carEvent.getVin());
            log.info("Availability: {}", carEvent.getIsAvailable() ? "AVAILABLE ✅" : "SOLD ❌");
            log.info("═══════════════════════════════════════════════════════");

            // Process inventory update
            // Example: Update inventory database
            // Example: Notify warehouse team
            // Example: Update e-commerce availability
            processInventoryUpdate(carEvent);

            channel.basicAck(deliveryTag, false);
            log.info("✅ Inventory update processed and acknowledged");

        } catch (Exception e) {
            log.error("❌ Error processing inventory update", e);
            handleError(message, channel, deliveryTag, e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 5: DEAD LETTER QUEUE (DLQ) CONSUMER
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * DLQ Consumer - Handles failed messages for manual inspection
     *
     * Messages arrive here when:
     * 1. Consumer NACK with requeue=false
     * 2. Message TTL expired
     * 3. Queue max-length exceeded
     * 4. Consumer threw exception after max retries
     *
     * What to do with DLQ messages:
     * 1. LOG for debugging (what went wrong?)
     * 2. ALERT team (Slack, PagerDuty)
     * 3. STORE for analysis (identify patterns)
     * 4. MANUAL REVIEW (fix data, replay message)
     *
     * Single consumer (concurrency=1) because:
     * - DLQ should be small (indicates problems)
     * - Manual inspection needed
     * - Not time-critical
     */
    @RabbitListener(
            queues = RabbitMQConfig.CAR_EVENTS_DLQ,
            concurrency = "1",
            ackMode = "MANUAL"
    )
    public void consumeDeadLetterQueue(CarEventMessage carEvent,
                                      Message message,
                                      Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        log.error("═══════════════════════════════════════════════════════");
        log.error("☠️ MESSAGE IN DEAD LETTER QUEUE");
        log.error("This message FAILED processing and requires attention!");
        log.error("═══════════════════════════════════════════════════════");
        log.error("Event Type: {}", carEvent.getEventType());
        log.error("Car ID: {}", carEvent.getCarId());
        log.error("Original Timestamp: {}", carEvent.getTimestamp());

        // ═══════════════════════════════════════════════════════════════════
        // CONCEPT 6: MESSAGE HEADERS - Access metadata
        // ═══════════════════════════════════════════════════════════════════
        /**
         * Message headers contain useful debugging information:
         * - x-death: Array of death events (why message failed)
         * - x-first-death-reason: Original failure reason
         * - x-first-death-queue: Original queue name
         * - x-death-count: How many times message died
         */
        var headers = message.getMessageProperties().getHeaders();
        log.error("Death Count: {}", headers.get("x-death"));
        log.error("All Headers: {}", headers);
        log.error("═══════════════════════════════════════════════════════");

        // TODO: Implement DLQ handling strategy
        // 1. Send alert to monitoring system
        // 2. Store in database for analysis
        // 3. Create ticket for manual review
        // 4. Optionally: Attempt replay after fixing issue

        try {
            // Always ACK messages from DLQ (don't want infinite loop)
            channel.basicAck(deliveryTag, false);
            log.error("⚠️ DLQ message acknowledged (removed from DLQ)");
        } catch (IOException e) {
            log.error("Failed to ACK DLQ message", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PRIVATE HELPER METHODS - Business Logic
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Process general car events
     * This is where your business logic goes
     */
    private void processCarEvent(CarEventMessage event) {
        // Simulate processing time
        try {
            Thread.sleep(100); // Real processing would be database update, API call, etc.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // TODO: Implement actual business logic
        // Examples:
        // - carService.updateCache(event.getCarId());
        // - searchIndexService.indexCar(event);
        // - analyticsService.trackEvent(event);
        // - notificationService.notifyUsers(event);

        log.info("✓ Car event processed successfully");
    }

    /**
     * Process price alert
     */
    private void processPriceAlert(CarEventMessage event) {
        // TODO: Implement price alert logic
        // Examples:
        // - emailService.sendPriceAlert(event);
        // - pushNotificationService.notify(event);
        // - analyticsService.trackPriceChange(event);

        log.info("✓ Price alert processed successfully");
    }

    /**
     * Process inventory update
     */
    private void processInventoryUpdate(CarEventMessage event) {
        // TODO: Implement inventory logic
        // Examples:
        // - inventoryService.updateAvailability(event.getCarId(), event.getIsAvailable());
        // - warehouseService.notifyStockChange(event);
        // - reportingService.updateMetrics(event);

        log.info("✓ Inventory update processed successfully");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * CONCEPT 7: INTELLIGENT ERROR HANDLING
     * ═══════════════════════════════════════════════════════════════════════
     *
     * Error handling strategy determines system reliability
     *
     * Decision matrix:
     *
     * | Error Type          | Action              | Why                          |
     * |---------------------|---------------------|------------------------------|
     * | Network timeout     | NACK + requeue      | Temporary, retry will work   |
     * | Database lock       | NACK + requeue      | Temporary, wait and retry    |
     * | Invalid data        | NACK + DLQ          | Permanent, needs manual fix  |
     * | Business rule error | NACK + DLQ          | Permanent, needs code change |
     * | Unknown error       | NACK + requeue (3x) | Try again, then give up      |
     */
    private void handleError(Message message, Channel channel, long deliveryTag, Exception e)
            throws IOException {

        // Get retry count from message headers
        Integer retryCount = (Integer) message.getMessageProperties().getHeaders()
                .getOrDefault("x-retry-count", 0);

        final int MAX_RETRIES = 3;

        if (retryCount < MAX_RETRIES) {
            // ═══════════════════════════════════════════════════════════════
            // RETRY LOGIC - Temporary error, requeue for retry
            // ═══════════════════════════════════════════════════════════════
            log.warn("⚠️ Retry attempt {}/{} - Requeuing message", retryCount + 1, MAX_RETRIES);

            /**
             * basicNack() parameters:
             * - deliveryTag: Message identifier
             * - multiple: false = reject only this message
             * - requeue: true = put message back in queue for retry
             *           false = send to DLQ (Dead Letter Queue)
             *
             * What happens with requeue=true:
             * 1. Message goes back to END of queue
             * 2. Another consumer (or same one) will process it again
             * 3. Retry count should be tracked to prevent infinite loops
             */
            channel.basicNack(deliveryTag, false, true);

        } else {
            // ═══════════════════════════════════════════════════════════════
            // GIVE UP - Send to DLQ for manual inspection
            // ═══════════════════════════════════════════════════════════════
            log.error("❌ Max retries exceeded - Sending to Dead Letter Queue");

            /**
             * requeue=false triggers Dead Letter Exchange
             *
             * Flow:
             * 1. Message rejected with requeue=false
             * 2. RabbitMQ checks queue's x-dead-letter-exchange property
             * 3. Message routed to DLX with x-dead-letter-routing-key
             * 4. DLX routes to DLQ based on binding
             * 5. DLQ consumer can inspect failure
             */
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

