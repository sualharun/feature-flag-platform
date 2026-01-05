# Feature Flag & Experimentation Platform

## Overview

This project is a production-grade **Feature Flag & Experimentation Platform** built with Java and AWS. It enables teams to safely release features, perform gradual rollouts, instantly roll back changes, and experiment with user experiences — all without redeploying services.

The system is designed as an internal platform tool, similar in spirit to solutions used at companies like Amazon, Google, and Microsoft.

---

## Problem Statement

Modern software systems require fast iteration with minimal risk. Traditional deployment-based releases are:

- **Risky** – all users are affected at once
- **Slow to roll back** – requires new deployment
- **Poorly suited for experimentation** – no runtime control

This platform **decouples feature releases from deployments**, allowing runtime control over application behavior with strong guarantees around safety, performance, and reliability.

---

## Goals

- ✅ Enable dynamic feature control at runtime
- ✅ Support safe, gradual rollouts
- ✅ Provide instant kill switches for emergency rollback
- ✅ Enable deterministic user experiences
- ✅ Lay the foundation for A/B testing and experimentation

---

## Core Features (MVP)

### 1. Feature Flag Management

- Create, update, and delete feature flags
- Enable or disable flags globally
- Store flag metadata (name, description, version)

### 2. Flag Evaluation

- Runtime flag evaluation via a Java SDK
- Boolean flag results (enabled / disabled)
- Deterministic evaluation (same user always gets same result)
- No application redeploy required

### 3. Percentage-Based Rollouts

- Gradual rollout from 0% to 100%
- Even traffic distribution using deterministic hashing
- User-based rollout control

### 4. Kill Switch / Emergency Rollback

- Instantly disable a feature
- Bypass cached values when necessary
- Safe rollback under partial system failure

### 5. SDK Integration

- Java SDK for flag evaluation
- Local in-memory caching with TTL
- Safe fallback defaults when service is unavailable

### 6. Data Persistence

- Persistent storage of flag configurations
- Versioned updates for rollback support
- Read-heavy, write-light optimized access pattern

---

## Non-Functional Requirements

### Performance

- Flag evaluation latency **< 10ms**
- Cache-first reads (no DB call per evaluation)

### Reliability

- SDK continues functioning during backend outages
- Graceful degradation with fallback values
- Circuit breaker protection

### Scalability

- Horizontally scalable backend services
- Stateless flag service
- Supports high read throughput

### Security

- Authenticated access to flag management APIs
- No sensitive user data stored in flags
- IAM-based access control on AWS

### Fault Tolerance

- Redis cache failure handling
- Retry with exponential backoff
- Safe defaults when dependencies fail

### Testability

- Unit tests for flag evaluation logic
- Integration tests for API endpoints
- Deterministic behavior under test conditions

---

## System Architecture (High Level)

```
┌─────────────────────┐
│ Client Application  │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────────┐
│ Feature Flag SDK (Java) │
└──────────┬──────────────┘
           │
           ↓
┌──────────────────────────────┐
│ Redis Cache (ElastiCache)    │
└──────────┬───────────────────┘
           │
           ↓
┌──────────────────────────────┐
│ Flag Service (Spring Boot)   │
└──────────┬───────────────────┘
           │
           ↓
┌──────────────────────────────┐
│ DynamoDB                     │
└──────────────────────────────┘
```

### Design Principles

- 📖 **Read-heavy optimization**
- ⚡ **Low-latency evaluation**
- 🛡️ **Failure isolation**
- 📈 **Horizontal scalability**

---

## Technology Stack

### Backend

- **Java 17**
- **Spring Boot**
- **Spring Web**
- **Spring Data**

### AWS

- **EC2 / ECS** – backend hosting
- **DynamoDB** – flag storage
- **ElastiCache Redis** – distributed caching
- **IAM** – security
- **CloudWatch** – monitoring & logs

---

## Out of Scope (MVP)

The following are intentionally excluded from the initial version:

- ❌ Web-based UI dashboard
- ❌ Multi-language SDKs
- ❌ Advanced analytics pipelines
- ❌ Complex rule DSL
- ❌ SaaS billing / multi-tenant support

These are planned as **future enhancements**.

---

## Planned Extensions

### Advanced Rollouts

- Scheduled flag activation and deactivation
- Automated gradual rollouts

### Targeting & Rules

- User targeting by attributes (region, role, environment)
- Rule priority ordering
- Sticky user assignments

### Experimentation

- Multi-variant flags (A/B/C)
- Traffic splitting
- Metric tracking per variant
- Experiment duration control

### Governance & Security

- Role-based access control (RBAC)
- Approval workflows for production flags
- Audit logs for flag changes

### Observability

- Flag evaluation latency metrics
- Per-flag usage tracking
- Alerting on error spikes after rollout

---

## Assumptions

- A user identifier is available at evaluation time
- Flags are small in size and frequently read
- Writes are infrequent compared to reads

---

## Why This Project Matters

This project demonstrates:

- 🏗️ Real-world system design skills
- 🚀 Safe production deployment practices
- ⚙️ Performance and reliability engineering
- ☁️ Cloud-native architecture on AWS
