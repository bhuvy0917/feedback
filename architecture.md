# Microservices Architecture Documentation

## Overview

This document describes the architecture of the feedback application through the lens of the Software System Intelligence Ontology.

Although the current implementation is a compact single-service application, the ontology has been designed for a microservices-based web application. This document therefore covers:
- current deployed structure
- logical service responsibilities
- future microservice decomposition
- inter-service communication model
- API gateway considerations
- traceability across architecture layers

## Current Architecture Snapshot

### Current Implementation Style
- **Frontend:** React application in `frontend/src/App.js`
- **Backend:** Spring Boot application exposing REST endpoints
- **Persistence:** Spring Data JPA repository over the `Feedback` entity
- **Deployment Style:** Monolithic runtime with clear logical layers

### Current Logical Components

| Logical Unit | Current Technology | Responsibility |
|---|---|---|
| Frontend UI | React | Collect feedback and call backend API |
| API / Controller Layer | Spring Web `FeedbackController` | Expose REST endpoints |
| Service Layer | `FeedbackService` | Business rules, validation, retrieval, update, delete |
| Repository Layer | `FeedbackRepository` | Data access through JPA |
| Database Layer | JPA-managed persistence | Store feedback records |

---

## Microservice View from the Ontology

The ontology models the application as a microservices-ready system, even though the current deployment is not yet distributed.

### Core Ontology Entities
- `Microservice`
- `InterServiceCall`
- `ServiceComponent`
- `ServiceMethod`
- `ApiEndpoint`
- `DataEntity`
- `ExceptionFlow`
- `LogPoint`

### Architectural Intent
The ontology supports evolving from:
```text
Single backend service
```

to:
```text
UI Service
→ Feedback API Service
→ Validation / Moderation Service
→ Notification Service
→ Analytics or Reporting Service
```

---

## List of Microservices

## 1. Feedback Web UI

### Responsibility
- Render the feedback form
- Collect name and feedback text
- Trigger submission API calls
- Show success or future error UI

### Current Realization
- `frontend/src/App.js`

### Exposed Interactions
- Calls `POST /api/feedback`

---

## 2. Feedback API Service

### Responsibility
- Accept feedback-related API requests
- Coordinate validation and persistence
- Return feedback data to clients
- Surface errors through API responses

### Current Realization
- `FeedbackController`
- `FeedbackService`
- `FeedbackRepository`

### Exposed Endpoints
- `POST /api/feedback`
- `GET /api/feedback`

---

## 3. Persistence Service / Data Access Boundary

### Responsibility
- Read and write `Feedback` entities
- Encapsulate data access behavior
- Support retrieval, update, delete, and count operations

### Current Realization
- `FeedbackRepository`
- JPA persistence configuration

### Notes
In the current codebase this is a repository layer, not a separate service. The ontology can still represent it as a dedicated architectural boundary.

---

## 4. Future Validation or Moderation Service

### Responsibility
- Advanced validation rules
- Content moderation
- spam detection
- profanity filtering
- policy enforcement

### Current Realization
- Not implemented
- Partially represented by `FeedbackService.validateFeedback(...)`

### Future Inter-Service Call Example
`Feedback API Service`  
→ `Validation Service`  
→ validation decision / enriched error response

---

## 5. Future Reporting or Analytics Service

### Responsibility
- Aggregate feedback trends
- Generate reports
- Support dashboards and insights

### Current Realization
- Not implemented

### Future Inter-Service Call Example
`Feedback API Service`  
→ `Analytics Service`  
→ usage metrics / summaries

---

## Responsibilities of Each Service

| Service | Responsibilities | Current Status |
|---|---|---|
| Feedback Web UI | Capture input, call APIs, display feedback to user | Implemented |
| Feedback API Service | REST interface and orchestration | Implemented |
| Service Logic Layer | Validation, retrieval, updates, business rules | Implemented but partially bypassed by controller |
| Persistence Layer | Save and fetch feedback data | Implemented |
| Validation / Moderation Service | Advanced validation and content control | Planned |
| Analytics / Reporting Service | Insights and aggregated reporting | Planned |

---

## Inter-Service Communication

## Current State
There are no actual networked inter-service calls in the current implementation.

### Current Runtime Interaction
```text
React UI
→ REST API (/api/feedback)
→ Controller
→ Repository / Service
→ Database
```

## Ontology-Supported InterServiceCall Model
The ontology supports explicit service-to-service interaction with:
- `from`
- `to`
- `protocol`
- `isAsync`
- `sequenceOrder`
- `throwsError`
- `propagatesTo`

### Example Future InterServiceCall Scenarios

#### Scenario 1: Validation Service Call
```text
Feedback API Service
→ Validation Service
→ response: approved / rejected / flagged
```

#### Scenario 2: Notification Service Call
```text
Feedback API Service
→ Notification Service
→ send acknowledgment email or event
```

#### Scenario 3: Analytics Event Publication
```text
Feedback API Service
→ Analytics Service
→ publish feedback-submitted event
```

---

## API Gateway Interactions

## Current State
No API gateway is present in the current codebase.

## Future API Gateway Role
If the application evolves into true microservices, an API Gateway may:
- route requests to feedback APIs
- centralize authentication and authorization
- perform rate limiting
- collect telemetry
- standardize error handling

### Example Gateway Flow
```text
Frontend UI
→ API Gateway
→ Feedback API Service
→ Validation / Reporting services
```

### Gateway Benefits
- unified public API surface
- centralized cross-cutting concerns
- simplified client configuration
- improved observability and request tracing

---

## Architecture Traceability

## End-to-End Logical Path
`UIScreen`  
→ `ApiEndpoint`  
→ `ServiceComponent (Controller)`  
→ `ServiceMethod`  
→ `ProcessingStep`  
→ `ServiceComponent (Repository)`  
→ `DataEntity`

## Error Traceability Path
`UIValidationRule / ProcessingStep`  
→ `ValidationError / ErrorCode`  
→ `ErrorOrigin`  
→ `ExceptionFlow`

## Sequence Traceability Path
`BusinessFlow`  
→ `FlowStep`  
→ `ApiEndpoint`  
→ `ServiceMethod`  
→ `InterServiceCall`  
→ `DataEntity / ApiResponse`

---

## Architectural Observations

### Strengths
- Clear separation of frontend and backend concerns
- Explicit repository abstraction
- Service layer already exists for business rules
- Ontology supports future decomposition without redesign

### Current Gaps
- Controller bypasses service layer for active endpoints
- No API gateway
- No actual inter-service communication
- No structured logging or centralized error mapping
- No explicit infrastructure-level tracing

### Recommended Next Steps
- Route controller requests through `FeedbackService`
- Add global exception mapping for consistent API errors
- Add request and error logging
- Add pagination to retrieval APIs
- Introduce gateway or edge-service patterns if system expands
- Introduce true `InterServiceCall` implementations as new capabilities are added

---

## Architecture Mapping Table

| Ontology Concept | Current Application Mapping |
|---|---|
| `Microservice` | Feedback backend service (logical) |
| `InterServiceCall` | None currently implemented |
| `ServiceComponent` | `FeedbackController`, `FeedbackService`, `FeedbackRepository` |
| `ServiceMethod` | `save`, `getAll`, `saveFeedback`, `validateFeedback`, etc. |
| `DataEntity` | `Feedback` JPA entity / table |
| `ApiEndpoint` | `/api/feedback` GET and POST |
| `UIScreen` | Program Feedback Screen |

## Related Documentation

- [API Specification](./api-specification.md)
- [Backend Logic](./backend-logic.md)
- [Business Flows](./business-flows.md)
- [Frontend](./frontend.md)
- [Sequence Flows](./sequence-flows.md)

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `frontend/src/App.js`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`, `backend/src/main/java/com/example/feedback/FeedbackRepository.java`