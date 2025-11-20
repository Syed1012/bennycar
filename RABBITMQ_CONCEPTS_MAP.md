# 🎓 AMQP Concepts - Visual Learning Map

## 📊 Complete Message Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        RABBITMQ BROKER (Message Router)                      │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                          EXCHANGES (Routers)                          │  │
│  │                                                                        │  │
│  │  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐         │  │
│  │  │ DIRECT         │  │ TOPIC          │  │ FANOUT         │         │  │
│  │  │ Exact Match    │  │ Pattern Match  │  │ Broadcast All  │         │  │
│  │  │ car.created ✓  │  │ car.# matches  │  │ Ignores Key    │         │  │
│  │  │ car.updated ✓  │  │ car.* matches  │  │ → All Queues   │         │  │
│  │  └────────┬───────┘  └────────┬───────┘  └────────┬───────┘         │  │
│  │           │                   │                     │                  │  │
│  │           └─────────BINDINGS (Routing Rules)───────┘                  │  │
│  │                     (routingKey + pattern)                            │  │
│  └───────────────────────────────┬──────────────────────────────────────┘  │
│                                   │                                          │
│  ┌────────────────────────────────┴──────────────────────────────────────┐ │
│  │                        QUEUES (Message Storage)                        │ │
│  │                                                                         │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       │ │
│  │  │ car.events.queue│  │ car.price.alert │  │ car.inventory   │       │ │
│  │  │                 │  │                 │  │                 │       │ │
│  │  │ [Msg1][Msg2]... │  │ [Msg1]...       │  │ [Msg1]...       │       │ │
│  │  │                 │  │                 │  │                 │       │ │
│  │  │ TTL: 60s        │  │ Durable: Yes    │  │ Max: 10000      │       │ │
│  │  │ DLX: Configured │  │                 │  │                 │       │ │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘       │ │
│  └───────────┼─────────────────────┼─────────────────────┼───────────────┘ │
└──────────────┼─────────────────────┼─────────────────────┼─────────────────┘
               │                     │                     │
               ▼                     ▼                     ▼
      ┌────────────────┐   ┌────────────────┐   ┌────────────────┐
      │ Consumer 1     │   │ Consumer 2     │   │ Consumer 3     │
      │ (3-10 threads) │   │ (1-3 threads)  │   │ (2-5 threads)  │
      │ Prefetch: 5    │   │ Prefetch: 5    │   │ Prefetch: 5    │
      │ ACK: Manual    │   │ ACK: Manual    │   │ ACK: Manual    │
      └────────────────┘   └────────────────┘   └────────────────┘
```

---

## 🔄 Message Lifecycle

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         MESSAGE LIFECYCLE                                │
└─────────────────────────────────────────────────────────────────────────┘

STEP 1: PRODUCTION
┌─────────────────┐
│ CarService      │  Car created in database
│ saveCar()       │       │
└────────┬────────┘       │
         │                ▼
         │         ┌──────────────┐
         └────────>│ CarEventProducer │  Create CarEventMessage
                   └────────┬─────────┘
                            │
                            ▼
                   ┌──────────────┐
                   │ RabbitTemplate│  Convert to JSON
                   └────────┬──────┘
                            │
                            ▼
                   ┌──────────────┐
                   │ Exchange     │  Route based on key
                   └────────┬─────┘
                            │
                            ▼

STEP 2: ROUTING
                   ┌──────────────┐
                   │ Binding Check│  Match routing key?
                   └────────┬─────┘
                            │
                 ┌──────────┴──────────┐
                 │                     │
                 ▼                     ▼
            ✅ MATCH                ❌ NO MATCH
                 │                     │
                 ▼                     ▼
         ┌──────────────┐      Message DISCARDED
         │ Queue        │      (Lost forever!)
         └────────┬─────┘
                  │
                  ▼

STEP 3: STORAGE
         ┌──────────────┐
         │ Queue        │  Message stored
         │ [Msg] ←      │  (Durable = survives restart)
         └────────┬─────┘
                  │
                  │ Waiting for consumer...
                  │ (TTL timer starts)
                  ▼

STEP 4: CONSUMPTION
         ┌──────────────┐
         │ Consumer     │  Receives message
         │ @RabbitListener│
         └────────┬─────┘
                  │
        ┌─────────┴─────────┐
        │ Process Message   │
        └─────────┬─────────┘
                  │
     ┌────────────┴────────────┐
     │                         │
     ▼                         ▼
 ✅ SUCCESS                ❌ FAILURE
     │                         │
     ▼                         ▼
┌──────────┐            ┌──────────┐
│ ACK      │            │ NACK     │
│ (Delete) │            │ (Retry)  │
└──────────┘            └────┬─────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
              requeue=true      requeue=false
                    │                 │
                    ▼                 ▼
            Back to Queue      Dead Letter Queue
            (Try again)         (Manual review)
```

---

## 🎯 Exchange Type Comparison

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         EXCHANGE TYPES                                   │
└─────────────────────────────────────────────────────────────────────────┘

1️⃣  DIRECT EXCHANGE (Exact Match)
════════════════════════════════════════

Message: routingKey = "car.created"

Binding 1: queue=car-events, key="car.created"  ✅ MATCH → Delivered
Binding 2: queue=price-alerts, key="car.price"  ❌ NO MATCH
Binding 3: queue=inventory, key="car.created"   ✅ MATCH → Delivered

Result: Message goes to 2 queues (car-events & inventory)

Use Case: Task distribution, point-to-point messaging


2️⃣  TOPIC EXCHANGE (Pattern Match)
════════════════════════════════════════

Message: routingKey = "car.price.changed.luxury"

Binding 1: queue=all-events, pattern="car.#"              ✅ MATCH (# = 0+ words)
Binding 2: queue=price-events, pattern="car.price.*"     ✅ MATCH (* = 1 word)
Binding 3: queue=luxury-events, pattern="*.*.*.luxury"   ✅ MATCH
Binding 4: queue=created-events, pattern="car.created"   ❌ NO MATCH

Result: Message goes to 3 queues (all-events, price-events, luxury-events)

Wildcard Rules:
  *  = matches EXACTLY ONE word
  #  = matches ZERO or MORE words

Examples:
  car.*            → car.created ✅  car.price.changed ❌
  car.#            → car.created ✅  car.price.changed ✅
  *.price.*        → car.price.low ✅  car.price.changed.alert ❌
  car.price.#      → car.price ✅  car.price.changed.high ✅

Use Case: Flexible routing, selective subscriptions


3️⃣  FANOUT EXCHANGE (Broadcast)
════════════════════════════════════════

Message: routingKey = "ignored" (doesn't matter!)

Binding 1: queue=events       ✅ ALWAYS RECEIVES
Binding 2: queue=analytics    ✅ ALWAYS RECEIVES
Binding 3: queue=logs         ✅ ALWAYS RECEIVES
Binding 4: queue=monitoring   ✅ ALWAYS RECEIVES

Result: ALL bound queues receive message

Use Case: 
  - Pub/Sub pattern
  - Broadcasting announcements
  - Cache invalidation across servers
  - System-wide events
```

---

## 🔧 Configuration Properties Explained

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    QUEUE CONFIGURATION                                   │
└─────────────────────────────────────────────────────────────────────────┘

QueueBuilder.durable("car.events.queue")
    ↓
    ├─ .durable(true)                  → Queue survives RabbitMQ restart
    │                                    (Stored on disk)
    │
    ├─ .withArgument("x-dead-letter-exchange", "car.dlx")
    │                                  → Failed messages go to DLX
    │                                    (Instead of being lost)
    │
    ├─ .withArgument("x-dead-letter-routing-key", "failed")
    │                                  → Routing key when sending to DLX
    │                                    (Categorize failure types)
    │
    ├─ .withArgument("x-message-ttl", 60000)
    │                                  → Messages expire after 60 seconds
    │                                    (Prevents stale data)
    │                                    → Expired → DLX
    │
    └─ .withArgument("x-max-length", 10000)
                                       → Queue holds max 10,000 messages
                                         (Prevents memory overflow)
                                         → Oldest → DLX when full


┌─────────────────────────────────────────────────────────────────────────┐
│                    CONSUMER CONFIGURATION                                │
└─────────────────────────────────────────────────────────────────────────┘

@RabbitListener(
    queues = "car.events.queue",
    ↓
    ├─ concurrency = "3-10"           → Min 3, Max 10 parallel consumers
    │                                   More consumers = faster processing
    │                                   
    │                                   Example with 100 messages:
    │                                   1 consumer:  100s (1 msg/s)
    │                                   10 consumers: 10s (10 msg/s)
    │
    └─ ackMode = "MANUAL"              → Must explicitly ACK/NACK
                                         Manual = safer (retry on failure)
                                         Auto = faster (but risky)


spring.rabbitmq.listener.simple:
    ↓
    ├─ prefetch: 5                     → Each consumer fetches 5 msgs at once
    │                                    Balance between speed & safety
    │                                    
    │                                    prefetch=1: Slow, safe
    │                                    prefetch=100: Fast, risky
    │                                    prefetch=5: Balanced ✅
    │
    ├─ acknowledge-mode: manual         → Same as ackMode above
    │
    └─ retry:
        ├─ enabled: true                → Enable automatic retries
        ├─ initial-interval: 1000       → Wait 1s before first retry
        ├─ max-attempts: 3              → Try 3 times total
        ├─ max-interval: 10000          → Max wait 10s between retries
        └─ multiplier: 2.0              → Exponential backoff (1s, 2s, 4s)
```

---

## 🔄 Acknowledgement Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ACKNOWLEDGEMENT MODES                                 │
└─────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  AUTO ACK (ackMode = "AUTO")                           │
│  ⚠️ RISKY - Not Recommended                            │
└────────────────────────────────────────────────────────┘

Message arrives → Consumer receives → ✅ ACK automatically → Process
                                                               │
                                                               ▼
                                                          ❌ Crash!
                                                          Message LOST!

Problem: If consumer crashes during processing, message already deleted!


┌────────────────────────────────────────────────────────┐
│  MANUAL ACK (ackMode = "MANUAL")                       │
│  ✅ SAFE - Recommended                                 │
└────────────────────────────────────────────────────────┘

Message arrives → Consumer receives → Process → Success?
                                         │          │
                                         │     ┌────┴────┐
                                         │     │         │
                                         │     ▼         ▼
                                         │   ✅ YES    ❌ NO
                                         │     │         │
                                         │     ▼         ▼
                                         │   ACK      NACK
                                         │     │         │
                                         │     ▼         ▼
                                         │  Delete   Requeue or DLQ


ACK Options:
━━━━━━━━━━
channel.basicAck(deliveryTag, false)
    │                          │
    │                          └─> multiple=false: ACK only this message
    │                              multiple=true:  ACK this + all previous
    └─> Message ID

Effect: ✅ Message removed from queue permanently


NACK Options:
━━━━━━━━━━━
channel.basicNack(deliveryTag, false, requeue)
    │                          │      │
    │                          │      └─> requeue=true:  Back to queue (retry)
    │                          │          requeue=false: Send to DLQ
    │                          └─> multiple=false: NACK only this message
    └─> Message ID

Effect: 
  requeue=true  → Message goes back to END of queue
  requeue=false → Message goes to Dead Letter Exchange (if configured)
```

---

## 🎯 Dead Letter Queue (DLQ) Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    DEAD LETTER QUEUE FLOW                                │
└─────────────────────────────────────────────────────────────────────────┘

┌──────────────┐
│ Normal Queue │  car.events.queue
│              │  x-dead-letter-exchange = "car.dlx"
│              │  x-message-ttl = 60000
│              │  x-max-length = 10000
└──────┬───────┘
       │
       │ Message arrives...
       │
       ▼
   ┌──────────────────────┐
   │ Consumer processes   │
   └──────────┬───────────┘
              │
      ┌───────┴───────┐
      │               │
      ▼               ▼
  ✅ SUCCESS      ❌ FAILURE
      │               │
      │               ▼
      │         ┌──────────────────┐
      │         │ Retry attempt 1  │
      │         └─────────┬────────┘
      │                   │
      │         ┌─────────┴─────────┐
      │         │                   │
      │         ▼                   ▼
      │      ✅ OK              ❌ FAIL
      │         │                   │
      │         │                   ▼
      │         │         ┌──────────────────┐
      │         │         │ Retry attempt 2  │
      │         │         └─────────┬────────┘
      │         │                   │
      │         │         ┌─────────┴─────────┐
      │         │         │                   │
      │         │         ▼                   ▼
      │         │      ✅ OK              ❌ FAIL
      │         │         │                   │
      │         │         │                   ▼
      │         │         │         ┌──────────────────┐
      │         │         │         │ Retry attempt 3  │
      │         │         │         └─────────┬────────┘
      │         │         │                   │
      │         │         │         ┌─────────┴─────────┐
      │         │         │         │                   │
      │         │         │         ▼                   ▼
      │         │         │      ✅ OK              ❌ FAIL
      │         │         │         │                   │
      ▼         ▼         ▼         ▼                   ▼
   ┌─────────────────────────────────┐         ┌──────────────┐
   │ Message DELETED                 │         │ Give Up!     │
   │ ✅ Success!                     │         └──────┬───────┘
   └─────────────────────────────────┘                │
                                                       ▼
                                              ┌────────────────┐
                                              │ NACK           │
                                              │ requeue=false  │
                                              └────────┬───────┘
                                                       │
                                                       ▼
                                              ┌────────────────┐
                                              │ Dead Letter    │
                                              │ Exchange (DLX) │
                                              └────────┬───────┘
                                                       │
                                                       │ Route via
                                                       │ "car.events.failed"
                                                       ▼
                                              ┌────────────────┐
                                              │ Dead Letter    │
                                              │ Queue (DLQ)    │
                                              │ car.events.dlq │
                                              └────────┬───────┘
                                                       │
                                                       ▼
                                              ┌────────────────┐
                                              │ DLQ Consumer   │
                                              │ - Log error    │
                                              │ - Alert team   │
                                              │ - Store for    │
                                              │   analysis     │
                                              └────────────────┘


OTHER DLQ TRIGGERS:
━━━━━━━━━━━━━━━━━━

1. TTL Expired:
   Message in queue > 60s → Automatically sent to DLX

2. Queue Full:
   Queue has 10,000 messages → New message → Oldest to DLX

3. Explicit Reject:
   Consumer calls basicNack(requeue=false) → To DLX immediately
```

---

## 🎓 Concept Summary Table

| Concept | Purpose | When to Use | Example |
|---------|---------|-------------|---------|
| **Producer** | Send messages | When event happens | Car created → publish event |
| **Consumer** | Process messages | React to events | Send email when car created |
| **Exchange** | Route messages | Always (messages go to exchange first) | car.direct.exchange |
| **Queue** | Store messages | Always (consumers read from queues) | car.events.queue |
| **Binding** | Connect exchange to queue | Define routing rules | "car.created" → car.events.queue |
| **Routing Key** | Identify message type | Categorize events | "car.price.changed" |
| **Direct Exchange** | Exact matching | Specific message routing | Payment events → payment queue |
| **Topic Exchange** | Pattern matching | Flexible routing | "car.#" catches all car events |
| **Fanout Exchange** | Broadcasting | Notify everyone | System shutdown |
| **ACK** | Confirm success | After processing | Delete message from queue |
| **NACK** | Reject message | Processing failed | Requeue or send to DLQ |
| **DLQ** | Handle failures | Store failed messages | Manual review and replay |
| **TTL** | Message expiration | Time-sensitive data | Price alerts expire in 60s |
| **Prefetch** | Batch size | Performance tuning | Fetch 5 messages at once |
| **Concurrency** | Parallel processing | Faster throughput | 10 consumers processing simultaneously |
| **Durable** | Persistence | Don't lose data on restart | Queue survives RabbitMQ restart |

---

## 🎯 When to Use What

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    EXCHANGE TYPE DECISION TREE                           │
└─────────────────────────────────────────────────────────────────────────┘

Question 1: Do all subscribers need the message?
              │
              ├─> YES → Use FANOUT Exchange
              │         (Broadcast to everyone)
              │         Example: System announcement
              │
              └─> NO  → Question 2
                        │
                        ├─> Need pattern matching? (wildcards)
                        │   │
                        │   ├─> YES → Use TOPIC Exchange
                        │   │         (Pattern routing)
                        │   │         Example: "car.*.changed"
                        │   │
                        │   └─> NO  → Use DIRECT Exchange
                        │             (Exact match)
                        │             Example: "payment.completed"
                        │
                        └─> Special cases:
                            ├─> Route by headers → HEADERS Exchange
                            └─> Default routing → DEFAULT Exchange
```

Happy Learning! 🚀

