# Non-Functional Requirements Documentation

## Overview

This document captures quality-oriented requirements for the feedback application based on the Software System Intelligence Ontology and the current implementation.

Non-functional requirements explain how well the system should behave, including:
- usability
- reliability
- maintainability
- scalability
- availability

Performance and security have dedicated documents and are referenced here to avoid duplication.

## Quality Attribute Summary

| Requirement | Quality Attribute | Description | Related Artifacts | Status |
|---|---|---|---|---|
| NFR-USABILITY-001 | USABILITY | Feedback submission UI should be simple and easy to complete | Program Feedback Screen, Name input, Feedback textarea, Submit button | Implemented |
| NFR-RELIABILITY-001 | RELIABILITY | System should reject invalid feedback consistently and return deterministic errors | FeedbackService.validateFeedback, FeedbackNotFoundException | Implemented |
| NFR-MAINTAINABILITY-001 | MAINTAINABILITY | System logic should be organized across controller, service, and repository layers | FeedbackController, FeedbackService, FeedbackRepository | Partially aligned |
| NFR-SCALABILITY-001 | SCALABILITY | Architecture should support future service decomposition and API expansion | Ontology microservice layer, REST API model | Planned |
| NFR-AVAILABILITY-001 | AVAILABILITY | Feedback retrieval and submission endpoints should remain reachable when backend is running | POST /api/feedback, GET /api/feedback | Implemented at basic level |

---

## Non-Functional Requirement: NFR-USABILITY-001

### Name
Simple Feedback Submission Experience

### Description
The system should provide a minimal and understandable user experience for collecting feedback.

### Acceptance Criteria
- The feedback screen displays a clear title.
- The form presents a name field and a feedback field.
- The user can submit the form with a single submit action.
- Browser-level validation prevents empty submissions.
- Users receive a clear success indication after submission.

### Business Rules
- The screen should minimize cognitive load.
- Required fields should be clearly enforced.
- The submission flow should not require navigation to another screen.

### Priority and Status
- **Priority:** HIGH
- **Status:** IMPLEMENTED

### Relationships

#### dependsOn
- `UIScreen`: Program Feedback Screen
- `UIComponent`: Name input
- `UIComponent`: Feedback textarea
- `UIComponent`: Submit button

#### conflictsWith
- Potential conflict with future advanced workflows that add more steps or moderation stages.

#### verifiedBy
- Frontend form implementation in `frontend/src/App.js`
- HTML required validation attributes
- Success alert on submission

---

## Non-Functional Requirement: NFR-RELIABILITY-001

### Name
Consistent Validation and Error Responses

### Description
The system should consistently detect invalid feedback payloads and reject them using explicit backend validation rules.

### Acceptance Criteria
- Null payloads are rejected.
- Blank names are rejected.
- Blank feedback text is rejected.
- Overlength names are rejected.
- Overlength feedback text is rejected.
- Each failure path maps to a stable error code.

### Business Rules
- Validation must execute before persistence.
- Validation logic must be centralized in service logic.
- Invalid data must not be persisted.

### Priority and Status
- **Priority:** CRITICAL
- **Status:** IMPLEMENTED

### Relationships

#### dependsOn
- `ServiceMethod`: `validateFeedback`
- `ProcessingStep`: backend validation
- `ErrorCode`: validation-related codes

#### conflictsWith
- Potential conflict with draft-save or partial-save flows.

#### verifiedBy
- `FeedbackService.validateFeedback`
- Exception-based failure paths in `FeedbackService`
- Ontology validation and error model

---

## Non-Functional Requirement: NFR-MAINTAINABILITY-001

### Name
Layered Backend Design

### Description
The system should preserve separation of concerns between controller, service, and repository responsibilities for easier maintenance and evolution.

### Acceptance Criteria
- Controllers expose request endpoints.
- Services contain validation and business logic.
- Repositories manage persistence.
- New features can be added by extending the service layer without rewriting the API contract.

### Business Rules
- Business rules should live in the service layer.
- Persistence access should be isolated to repositories.
- Controllers should remain thin orchestration entry points.

### Priority and Status
- **Priority:** HIGH
- **Status:** PARTIALLY IMPLEMENTED

### Relationships

#### dependsOn
- `ServiceComponent`: Controller
- `ServiceComponent`: Service
- `ServiceComponent`: Repository

#### conflictsWith
- Current controller implementation directly uses the repository, which conflicts with the ideal service-oriented layering.

#### verifiedBy
- Ontology definitions for `ServiceComponent`, `ServiceMethod`, and `ProcessingStep`
- Existing `FeedbackService` implementation
- Existing `FeedbackController` implementation showing partial deviation

### Architectural Observation
The ontology supports a clean layered design, but the current `FeedbackController` injects `FeedbackRepository` directly instead of delegating to `FeedbackService`.

---

## Non-Functional Requirement: NFR-SCALABILITY-001

### Name
Scalable Service and Interaction Model

### Description
The architecture should support future decomposition into multiple microservices and explicit inter-service interactions.

### Acceptance Criteria
- Ontology includes `Microservice` and `InterServiceCall`.
- Business flows can span multiple services.
- Sequence diagram generation can model cross-service execution order.
- API, backend, and error semantics remain traceable as the system grows.

### Business Rules
- Traceability must remain intact even when logic is distributed.
- Inter-service calls should be modelled explicitly.
- Error propagation should remain observable across service boundaries.

### Priority and Status
- **Priority:** MEDIUM
- **Status:** PLANNED

### Relationships

#### dependsOn
- `Microservice`
- `InterServiceCall`
- `SequenceDiagramPattern`

#### conflictsWith
- No direct implementation conflict in current codebase; this is a forward-looking design requirement.

#### verifiedBy
- `feedback-requirements-schema.jsonld`
- Ontology sequence and architecture entities

---

## Non-Functional Requirement: NFR-AVAILABILITY-001

### Name
Basic Endpoint Availability

### Description
The feedback submission and retrieval capabilities should be available whenever the frontend and backend applications are running and connected.

### Acceptance Criteria
- Frontend can reach backend on `http://localhost:8080`.
- `POST /api/feedback` accepts submissions.
- `GET /api/feedback` returns available feedback.
- Cross-origin requests are permitted from the frontend.

### Business Rules
- Backend service must be reachable for successful submission.
- API downtime prevents successful completion of business flows.
- Cross-origin configuration must allow frontend interaction.

### Priority and Status
- **Priority:** HIGH
- **Status:** IMPLEMENTED

### Relationships

#### dependsOn
- `ApiEndpoint`: `POST /api/feedback`
- `ApiEndpoint`: `GET /api/feedback`
- `FeedbackController` CORS configuration

#### conflictsWith
- Potential future security hardening may restrict broad cross-origin access.

#### verifiedBy
- `@CrossOrigin` on `FeedbackController`
- Current frontend `axios.post(...)` integration

---

## Cross-References

### See Also
- [Functional Requirements](./functional-requirements.md)
- [Security Requirements](./security-requirements.md)
- [Performance Requirements](./performance-requirements.md)
- [Business Flows](./business-flows.md)
- [API Specification](./api-specification.md)
- [Backend Logic](./backend-logic.md)

## Notes

- This document focuses on non-functional concerns except for security and performance, which are split into dedicated documents.
- The ontology provides broader capability coverage than the currently implemented application.
- Where the implementation is incomplete, the document records the current state and the target architectural intent.

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `frontend/src/App.js`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`