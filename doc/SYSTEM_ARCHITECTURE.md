# System Architecture

---

## Architecture Style

Clean Architecture (Modular Monolith)

Controller
↓
Application Service
↓
Domain Layer
↓
Repository Layer
↓
Database

---

## Core Modules

- tenant
- employee
- salary
- tax
- bpjs
- overtime
- payroll
- approval
- audit

---

## Technology Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Batch
- Quartz Scheduler
- PostgreSQL
- Redis
- Flyway
- OpenPDF
- Docker Compose

---

## Multi-Tenant Strategy

Schema-per-tenant approach.

Example:

tenant_a.employee  
tenant_b.employee

Tenant resolved via:
- JWT claim
- X-Tenant-ID header

---

## Batch Processing

Monthly payroll executed using:
- Spring Batch
- Quartz Scheduler

---

## Caching

Redis used for:
- Payroll locking
- Rate caching
- Session invalidation
