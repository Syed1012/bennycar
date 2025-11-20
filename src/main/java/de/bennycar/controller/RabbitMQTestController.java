package de.bennycar.controller;

import de.bennycar.dto.CarEventMessage;
import de.bennycar.messaging.CarEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * RABBITMQ TEST CONTROLLER
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Purpose: TEST and LEARN different AMQP/RabbitMQ concepts
 *
 * This controller provides REST endpoints to:
 * 1. Test DIRECT exchange routing
 * 2. Test TOPIC exchange pattern matching
 * 3. Test FANOUT exchange broadcasting
 * 4. Simulate error scenarios (DLQ testing)
 * 5. Learn message flow through different exchanges
 *
 * Use RabbitMQ Management UI (http://localhost:15672) to:
 * - See queues and message counts
 * - Monitor message rates
 * - Inspect message contents
 * - View exchange bindings
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/rabbitmq")
@CrossOrigin(origins = "*")
public class RabbitMQTestController {

    @Autowired
    private CarEventProducer carEventProducer;

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 1: DIRECT EXCHANGE TESTING
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Test DIRECT exchange with exact routing key
     *
     * Try different routing keys:
     * - "car.created" → goes to car.events.queue
     * - "car.updated" → goes to car.events.queue
     * - "car.deleted" → goes to car.events.queue
     * - "car.unknown" → MESSAGE DISCARDED (no binding!)
     *
     * POST http://localhost:8080/api/rabbitmq/test/direct
     * Body: { "routingKey": "car.created", "message": "Test message" }
     */
    @PostMapping("/test/direct")
    public ResponseEntity<Map<String, Object>> testDirectExchange(
            @RequestBody Map<String, String> request) {

        String routingKey = request.getOrDefault("routingKey", "car.created");
        String message = request.getOrDefault("message", "Test direct exchange message");

        CarEventMessage event = new CarEventMessage(
                "TEST_DIRECT",
                999L,
                "TEST-VIN-123",
                "TestBrand",
                "TestModel",
                50000.0,
                true,
                message
        );

        carEventProducer.sendToDirectExchange(event, routingKey);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Message sent to DIRECT exchange");
        response.put("exchange", "car.direct.exchange");
        response.put("routingKey", routingKey);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("tip", "Check RabbitMQ UI at http://localhost:15672 to see message");

        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 2: TOPIC EXCHANGE TESTING with PATTERN MATCHING
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Test TOPIC exchange with wildcard patterns
     *
     * Try these routing keys and observe which queues receive the message:
     *
     * 1. "car.created"
     *    → Matches pattern "car.#" → car.events.queue receives
     *
     * 2. "car.price.changed"
     *    → Matches "car.#" (all car events) → car.events.queue receives
     *    → Matches "car.price.changed" (exact) → car.price.alert.queue receives
     *    → BOTH queues get the message!
     *
     * 3. "car.availability.changed"
     *    → Matches "car.#" → car.events.queue
     *    → Matches "car.availability.changed" → car.inventory.queue
     *    → BOTH queues receive!
     *
     * 4. "vehicle.created"
     *    → Does NOT match "car.#" → MESSAGE DISCARDED
     *
     * POST http://localhost:8080/api/rabbitmq/test/topic
     * Body: { "routingKey": "car.price.changed", "message": "Test pattern matching" }
     */
    @PostMapping("/test/topic")
    public ResponseEntity<Map<String, Object>> testTopicExchange(
            @RequestBody Map<String, String> request) {

        String routingKey = request.getOrDefault("routingKey", "car.price.changed");
        String message = request.getOrDefault("message", "Test topic exchange with patterns");

        CarEventMessage event = new CarEventMessage(
                "TEST_TOPIC",
                888L,
                "TEST-VIN-TOPIC",
                "TopicBrand",
                "TopicModel",
                45000.0,
                true,
                message
        );
        event.setOldPrice(40000.0); // For price change testing

        carEventProducer.sendToTopicExchange(event, routingKey);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Message sent to TOPIC exchange");
        response.put("exchange", "car.topic.exchange");
        response.put("routingKey", routingKey);
        response.put("message", message);
        response.put("patternMatching", Map.of(
                "car.#", "Matches all car.* keys (0 or more words)",
                "car.*", "Matches car.word only (exactly 1 word)",
                "*.price.*", "Matches anything.price.anything"
        ));
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 3: FANOUT EXCHANGE TESTING (Broadcasting)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Test FANOUT exchange - broadcasts to ALL bound queues
     *
     * What happens:
     * 1. Message sent to fanout exchange (routing key ignored)
     * 2. ALL 3 bound queues receive a copy:
     *    - car.events.queue
     *    - car.price.alert.queue
     *    - car.inventory.queue
     * 3. Each queue's consumer processes independently
     *
     * Use case examples:
     * - System shutdown notification
     * - Cache invalidation across servers
     * - Important announcements
     * - Analytics events (multiple systems interested)
     *
     * POST http://localhost:8080/api/rabbitmq/test/fanout
     * Body: { "message": "Broadcast to everyone!" }
     */
    @PostMapping("/test/fanout")
    public ResponseEntity<Map<String, Object>> testFanoutExchange(
            @RequestBody Map<String, String> request) {

        String message = request.getOrDefault("message", "Broadcasting to all subscribers!");

        CarEventMessage event = new CarEventMessage(
                "TEST_FANOUT",
                777L,
                "TEST-VIN-FANOUT",
                "FanoutBrand",
                "FanoutModel",
                60000.0,
                true,
                message
        );

        carEventProducer.sendToFanoutExchange(event);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Message BROADCASTED to all queues");
        response.put("exchange", "car.fanout.exchange");
        response.put("routingKey", "N/A (ignored for fanout)");
        response.put("message", message);
        response.put("broadcastTo", java.util.List.of(
                "car.events.queue",
                "car.price.alert.queue",
                "car.inventory.queue"
        ));
        response.put("timestamp", LocalDateTime.now());
        response.put("note", "Check logs - all 3 consumers will process this message!");

        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 4: BATCH MESSAGE TESTING (Load Testing)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Send multiple messages to test:
     * - Consumer concurrency (multiple consumers processing simultaneously)
     * - Message ordering
     * - Throughput (messages per second)
     * - Queue behavior under load
     *
     * POST http://localhost:8080/api/rabbitmq/test/batch?count=100
     */
    @PostMapping("/test/batch")
    public ResponseEntity<Map<String, Object>> testBatchMessages(
            @RequestParam(defaultValue = "10") int count) {

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= count; i++) {
            CarEventMessage event = new CarEventMessage(
                    "TEST_BATCH",
                    (long) i,
                    "BATCH-VIN-" + i,
                    "BatchBrand",
                    "BatchModel" + i,
                    30000.0 + (i * 1000),
                    true,
                    "Batch test message #" + i
            );

            // Rotate between different routing keys
            String routingKey = switch (i % 3) {
                case 0 -> "car.created";
                case 1 -> "car.price.changed";
                default -> "car.availability.changed";
            };

            carEventProducer.sendToTopicExchange(event, routingKey);
        }

        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Batch messages sent successfully");
        response.put("messageCount", count);
        response.put("durationMs", duration);
        response.put("messagesPerSecond", count * 1000.0 / duration);
        response.put("tip", "Watch application logs to see consumers processing messages in parallel!");

        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CONCEPT 5: ERROR SIMULATION (DLQ Testing)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Send a message that will cause consumer to fail
     * Used to test Dead Letter Queue (DLQ) behavior
     *
     * Message will be:
     * 1. Received by consumer
     * 2. Processing fails (simulated error)
     * 3. Retried 3 times
     * 4. After 3 failures, sent to Dead Letter Queue
     * 5. DLQ consumer logs the failure
     *
     * POST http://localhost:8080/api/rabbitmq/test/error
     */
    @PostMapping("/test/error")
    public ResponseEntity<Map<String, Object>> testErrorHandling() {

        CarEventMessage event = new CarEventMessage(
                "TEST_ERROR",
                666L,
                "ERROR-VIN-666",
                "ErrorBrand",
                "ErrorModel",
                0.0, // Invalid price will cause processing error
                true,
                "This message will fail processing and go to DLQ"
        );

        carEventProducer.sendCarCreatedEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Error test message sent");
        response.put("expectedBehavior", Map.of(
                "step1", "Consumer receives message",
                "step2", "Processing fails",
                "step3", "Retried 3 times",
                "step4", "Sent to Dead Letter Queue (DLQ)",
                "step5", "DLQ consumer logs failure"
        ));
        response.put("note", "Check logs to see retry and DLQ behavior");

        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILITY ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get information about AMQP configuration
     *
     * GET http://localhost:8080/api/rabbitmq/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getRabbitMQInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("rabbitMQ", Map.of(
                "managementUI", "http://localhost:15672",
                "credentials", "admin / admin123",
                "amqpPort", 5672,
                "managementPort", 15672
        ));

        info.put("exchanges", Map.of(
                "direct", "car.direct.exchange - Exact routing key matching",
                "topic", "car.topic.exchange - Pattern matching with wildcards",
                "fanout", "car.fanout.exchange - Broadcasts to all bound queues",
                "dlx", "car.dlx.exchange - Receives failed messages"
        ));

        info.put("queues", Map.of(
                "events", "car.events.queue - General car events",
                "priceAlerts", "car.price.alert.queue - Price change notifications",
                "inventory", "car.inventory.queue - Availability changes",
                "dlq", "car.events.dlq - Failed messages for manual review"
        ));

        info.put("routingKeys", Map.of(
                "car.created", "New car added",
                "car.updated", "Car details changed",
                "car.deleted", "Car removed",
                "car.price.changed", "Price modified",
                "car.availability.changed", "Sold/Available status changed"
        ));

        info.put("testEndpoints", Map.of(
                "direct", "POST /api/rabbitmq/test/direct",
                "topic", "POST /api/rabbitmq/test/topic",
                "fanout", "POST /api/rabbitmq/test/fanout",
                "batch", "POST /api/rabbitmq/test/batch?count=10",
                "error", "POST /api/rabbitmq/test/error"
        ));

        return ResponseEntity.ok(info);
    }
}

