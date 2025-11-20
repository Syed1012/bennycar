# 🚀 Quick Start Guide - RabbitMQ Testing

## Prerequisites
✅ Docker running
✅ RabbitMQ container started
✅ Spring Boot application running

## Step-by-Step Testing

### 1️⃣ Start RabbitMQ
```bash
cd /Users/syed/Documents/PSE/bennycar
docker-compose up -d rabbitmq
```

**Verify:**
- Open browser: http://localhost:15672
- Login: `admin` / `admin123`
- You should see RabbitMQ Management UI

### 2️⃣ Start Spring Boot Application
```bash
./mvnw spring-boot:run
```

**Watch the logs - you should see:**
```
Creating exchange: car.direct.exchange
Creating exchange: car.topic.exchange
Creating exchange: car.fanout.exchange
Creating queue: car.events.queue
Creating queue: car.price.alert.queue
...
```

### 3️⃣ Get RabbitMQ Info
```bash
curl http://localhost:8080/api/rabbitmq/info | jq
```

This shows all exchanges, queues, and test endpoints available.

---

## 🧪 Test Scenarios

### Test A: Direct Exchange (Exact Match)
**Concept:** Message goes to queue only if routing key EXACTLY matches

```bash
# Send message with routing key "car.created"
curl -X POST http://localhost:8080/api/rabbitmq/test/direct \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "car.created",
    "message": "Testing direct exchange"
  }'
```

**Expected Result:**
1. Check application logs - you'll see:
   ```
   📤 SENDING to DIRECT EXCHANGE
   Routing Key: car.created
   📥 RECEIVED MESSAGE from queue: car.events.queue
   ✅ Message ACKNOWLEDGED
   ```

2. In RabbitMQ UI (http://localhost:15672):
   - Go to **Queues** tab
   - See `car.events.queue` - message count increases then decreases (consumed)

**Try Different Routing Keys:**
```bash
# These will work (matched bindings)
curl -X POST http://localhost:8080/api/rabbitmq/test/direct \
  -H "Content-Type: application/json" \
  -d '{"routingKey": "car.updated", "message": "Update test"}'

curl -X POST http://localhost:8080/api/rabbitmq/test/direct \
  -H "Content-Type: application/json" \
  -d '{"routingKey": "car.deleted", "message": "Delete test"}'

# This will NOT work (no binding for this key)
curl -X POST http://localhost:8080/api/rabbitmq/test/direct \
  -H "Content-Type: application/json" \
  -d '{"routingKey": "car.unknown", "message": "No match - message LOST!"}'
```

---

### Test B: Topic Exchange (Pattern Matching)
**Concept:** Message routed based on wildcard patterns

```bash
# Send price change event
curl -X POST http://localhost:8080/api/rabbitmq/test/topic \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "car.price.changed",
    "message": "Price reduced by 10%"
  }'
```

**Expected Result:**
TWO queues receive the message:
1. `car.events.queue` (matches pattern `car.#`)
2. `car.price.alert.queue` (matches exact `car.price.changed`)

**In Logs:**
```
📥 RECEIVED MESSAGE from queue: car.events.queue
Event Type: TEST_TOPIC

💰 PRICE ALERT RECEIVED
Old Price: $40000.0
New Price: $45000.0
```

**Try Other Patterns:**
```bash
# Matches "car.#" pattern → car.events.queue only
curl -X POST http://localhost:8080/api/rabbitmq/test/topic \
  -H "Content-Type: application/json" \
  -d '{"routingKey": "car.created", "message": "Pattern test 1"}'

# Matches TWO patterns → TWO queues
curl -X POST http://localhost:8080/api/rabbitmq/test/topic \
  -H "Content-Type: application/json" \
  -d '{"routingKey": "car.availability.changed", "message": "Pattern test 2"}'
```

---

### Test C: Fanout Exchange (Broadcasting)
**Concept:** Message sent to ALL bound queues (routing key ignored)

```bash
curl -X POST http://localhost:8080/api/rabbitmq/test/fanout \
  -H "Content-Type: application/json" \
  -d '{"message": "System-wide announcement!"}'
```

**Expected Result:**
ALL THREE queues receive the message:
1. `car.events.queue`
2. `car.price.alert.queue`
3. `car.inventory.queue`

**In Logs - You'll see 3 consumers processing the SAME message:**
```
📥 RECEIVED MESSAGE from queue: car.events.queue
Event Type: TEST_FANOUT

💰 PRICE ALERT RECEIVED
Car: FanoutBrand FanoutModel

📦 INVENTORY UPDATE RECEIVED
Car: FanoutBrand FanoutModel
```

---

### Test D: Concurrency & Batch Processing
**Concept:** Multiple consumers process messages in parallel

```bash
# Send 50 messages
curl -X POST "http://localhost:8080/api/rabbitmq/test/batch?count=50"
```

**Expected Result:**
- Response shows throughput: `"messagesPerSecond": 500.0` (very fast!)
- In logs, you'll see timestamps showing parallel processing:
  ```
  📥 RECEIVED MESSAGE ... Car ID: 1 [Thread: SimpleAsyncTaskExecutor-1]
  📥 RECEIVED MESSAGE ... Car ID: 2 [Thread: SimpleAsyncTaskExecutor-2]
  📥 RECEIVED MESSAGE ... Car ID: 3 [Thread: SimpleAsyncTaskExecutor-3]
  (Processing simultaneously!)
  ```

**In RabbitMQ UI:**
- Go to **Queues** → `car.events.queue`
- Click "Get Messages" to inspect
- See consumers count: `3-10` active consumers

---

### Test E: Real Car Operations with Events

#### Create a Car
```bash
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{
    "name": "BMW X5",
    "vin": "5UXCR6C0XL9A12345",
    "brand": "BMW",
    "model": "X5",
    "year": 2023,
    "color": "Black",
    "transmission": "AUTOMATIC",
    "fuelType": "DIESEL",
    "bodyType": "SUV",
    "mileage": 5000,
    "price": 75000,
    "description": "Luxury SUV",
    "condition": "USED",
    "location": "Berlin",
    "isAvailable": true
  }'
```

**Expected:**
```
Car saved to database: ID=1, VIN=5UXCR6C0XL9A12345
Car CREATED event published to RabbitMQ
📥 RECEIVED MESSAGE from queue: car.events.queue
Event Type: CREATED
✓ Car event processed successfully
```

#### Update Car Price
```bash
# First, get the car ID from the response above, let's say it's 1
curl -X PUT http://localhost:8080/api/cars/1 \
  -H "Content-Type: application/json" \
  -d '{
    "price": 69000
  }'
```

**Expected - TWO events published:**
```
Price CHANGED event published: 75000.0 → 69000.0
Car UPDATED event published

📥 RECEIVED MESSAGE from queue: car.events.queue (UPDATE event)
💰 PRICE ALERT RECEIVED
Old Price: $75000.0
New Price: $69000.0
Price Change: -8.00%
```

#### Mark Car as Sold
```bash
curl -X PUT http://localhost:8080/api/cars/1 \
  -H "Content-Type: application/json" \
  -d '{
    "isAvailable": false
  }'
```

**Expected - TWO events:**
```
Availability CHANGED event published: true → false
Car UPDATED event published

📦 INVENTORY UPDATE RECEIVED
Availability: SOLD ❌
```

#### Delete Car
```bash
curl -X DELETE http://localhost:8080/api/cars/1
```

**Expected:**
```
Car deleted from database: ID=1
Car DELETED event published
📥 RECEIVED MESSAGE from queue: car.events.queue
Event Type: DELETED
Message: Car removed from inventory
```

---

## 🔍 Monitoring in RabbitMQ UI

### View Exchanges
1. Go to http://localhost:15672
2. Click **Exchanges** tab
3. Click on `car.topic.exchange`
4. See **Bindings** section - shows which queues are bound with which patterns

### View Queues
1. Click **Queues** tab
2. See all queues with message counts
3. Click on `car.events.queue`
4. Click **Get messages** to inspect message content

### View Consumers
1. In queue details, scroll to **Consumers** section
2. See active consumers with their prefetch count
3. Shows which consumer is processing messages

### Publish Test Message Manually
1. In queue details, go to **Publish message** section
2. Set:
   - Payload: `{"eventType":"TEST","message":"Manual test"}`
   - Headers: Leave empty
3. Click **Publish message**
4. Check application logs - consumer will process it!

---

## 📊 Understanding Logs

### Producer Logs (Sending)
```
═══════════════════════════════════════════════════════
📤 SENDING to DIRECT EXCHANGE
Exchange: car.direct.exchange
Routing Key: car.created
Message: CarEventMessage{...}
═══════════════════════════════════════════════════════
✅ Message sent successfully to direct exchange
```

### Consumer Logs (Receiving)
```
═══════════════════════════════════════════════════════
📥 RECEIVED MESSAGE from queue: car.events.queue
Event Type: CREATED
Car ID: 1
Car: BMW X5 (VIN: 5UXCR6C0XL9A12345)
Delivery Tag: 1
Timestamp: 2024-01-15T10:30:00
═══════════════════════════════════════════════════════
✓ Car event processed successfully
✅ Message ACKNOWLEDGED and removed from queue
```

### Error Logs (Failures)
```
❌ Error processing message
⚠️ Retry attempt 1/3 - Requeuing message
(or)
❌ Max retries exceeded - Sending to Dead Letter Queue
☠️ MESSAGE IN DEAD LETTER QUEUE
This message FAILED processing and requires attention!
```

---

## 🎯 Learning Checkpoints

### ✅ Checkpoint 1: Basic Understanding
- [ ] I understand what producers and consumers are
- [ ] I can explain the difference between direct, topic, and fanout exchanges
- [ ] I know what a routing key does
- [ ] I understand what a queue is

### ✅ Checkpoint 2: Hands-On Testing
- [ ] I successfully sent a message using direct exchange
- [ ] I tested topic exchange with different routing keys
- [ ] I observed fanout broadcasting to multiple queues
- [ ] I created a car and saw the event in logs

### ✅ Checkpoint 3: Advanced Concepts
- [ ] I understand manual vs auto acknowledgement
- [ ] I know what a Dead Letter Queue (DLQ) is for
- [ ] I understand concurrency and prefetch settings
- [ ] I can monitor messages in RabbitMQ UI

### ✅ Checkpoint 4: Real-World Application
- [ ] I can explain event-driven architecture benefits
- [ ] I understand how to scale consumers independently
- [ ] I know how to handle message failures
- [ ] I can design my own messaging patterns

---

## 🚨 Common Issues & Solutions

### Issue: "Connection refused"
**Solution:**
```bash
# Check RabbitMQ is running
docker ps | grep rabbitmq

# If not running, start it
docker-compose up -d rabbitmq

# Check logs
docker logs rabbitmq-bennycar
```

### Issue: Messages not being consumed
**Solution:**
1. Check application is running: `./mvnw spring-boot:run`
2. Check logs for errors
3. Verify queue name matches in consumer annotation
4. Check RabbitMQ UI - are consumers connected?

### Issue: Message goes to DLQ
**Reason:** Consumer threw an exception or message expired

**Solution:**
1. Check DLQ consumer logs for error details
2. Fix the bug in consumer code
3. Optionally replay message after fix

---

## 📚 Next Steps

1. **Read:** `RABBITMQ_LEARNING_GUIDE.md` for deep-dive explanations
2. **Practice:** Try all test scenarios above
3. **Experiment:** Modify routing keys and observe behavior
4. **Build:** Add your own event types and consumers
5. **Monitor:** Use RabbitMQ UI to visualize message flow

Happy Learning! 🎓

