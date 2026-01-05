# Design Tradeoffs

## DynamoDB vs RDS
Chosen: DynamoDB

Pros:
- Low-latency reads
- Auto-scaling
- No connection management

Cons:
- Limited querying
- No joins

---

## SDK Evaluation vs Server Evaluation
Chosen: SDK Evaluation

Pros:
- No network calls during requests
- Extremely low latency
- Works during outages

Cons:
- SDK complexity
- Cache invalidation challenges

---

## Redis Cache
Used to:
- Reduce DynamoDB load
- Share state across services

Risk:
- Redis failure mitigated with local SDK cache
