# API Specification

## Overview

This document describes the API surface of the feedback application using the Software System Intelligence Ontology and the current backend implementation.

The ontology models each API in terms of:
- endpoint path and method
- request and response structures
- related functional requirements
- backend handling components
- validation and error propagation
- traceability to business flows

## API Summary

| Endpoint | Method | Purpose | Related Functional Requirements | Handler |
|---|---|---|---|---|
| `/api/feedback` | POST | Submit new feedback | FR-SUBMIT-001, FR-VALIDATE-001 | `FeedbackController.save` |
| `/api/feedback` | GET | Retrieve all feedback | FR-VIEW-001 | `FeedbackController.getAll` |

---

## API Endpoint: POST /api/feedback

### Endpoint Path and HTTP Method
- **Path:** `/api/feedback`
- **Method:** `POST`

### Description
Creates a new feedback record using the submitted name and feedback text.

### Related Functional Requirement
- FR-SUBMIT-001 — Submit Feedback
- FR-VALIDATE-001 — Validate Feedback Input

### Business Flow Mapping
- BF-SUBMIT-001 — Submit Feedback

### Request Structure

#### Ontology Mapping
- `ApiEndpoint`: `POST /api/feedback`
- `ApiRequest`: Feedback submission payload

#### JSON Shape
```json
{
  "name": "string",
  "feedback": "string"
}
```

#### Request Notes
- `name` is required.
- `feedback` is required.
- The current frontend sends this payload via `axios.post(...)`.
- The controller accepts `@RequestBody Feedback feedback`.

### Response Structure

#### Ontology Mapping
- `ApiResponse`: Feedback persistence response

#### JSON Shape
```json
{
  "id": 1,
  "name": "string",
  "feedback": "string"
}
```

#### Response Notes
- The controller currently returns the saved `Feedback` entity.
- Response status is implicitly success when persistence succeeds.
- No explicit response wrapper is used.

### Error Codes Returned

| Error Code | Description | Origin | Trigger Condition |
|---|---|---|---|
| FEEDBACK_EMPTY | Feedback object cannot be null | Service | Request body is null or mapped object is missing |
| FEEDBACK_TEXT_EMPTY | Feedback text cannot be empty | Service | Feedback text is blank |
| FEEDBACK_NAME_EMPTY | Name cannot be empty | Service | Name is blank |
| FEEDBACK_TEXT_TOO_LONG | Feedback text cannot exceed 1000 characters | Service | Feedback exceeds length limit |
| FEEDBACK_NAME_TOO_LONG | Name cannot exceed 100 characters | Service | Name exceeds length limit |

### Service / Controller Handling the API

#### Controller
- `FeedbackController.save(@RequestBody Feedback feedback)`

#### Intended Service Path
- `FeedbackService.saveFeedback(Feedback feedback)`
- `FeedbackService.validateFeedback(Feedback feedback)`

#### Repository
- `FeedbackRepository.save(feedback)`

### Current Implementation Note
The current controller saves directly through `FeedbackRepository` and does not currently call `FeedbackService.saveFeedback()`. The ontology documents the preferred layered handling model.

### Validation Mapping
- **Frontend validation:** HTML `required` attributes on input fields
- **Backend validation:** `validateFeedback(...)`
- **Validation ontology entities:** `UIValidationRule`, `ValidationError`, `ErrorCode`

### End-to-End Traceability
`UIScreen`  
→ `UIComponent`  
→ `ApiEndpoint POST /api/feedback`  
→ `FeedbackController.save`  
→ `FeedbackService.saveFeedback`  
→ `ProcessingStep: validation`  
→ `FeedbackRepository.save`  
→ `DataEntity: Feedback`

---

## API Endpoint: GET /api/feedback

### Endpoint Path and HTTP Method
- **Path:** `/api/feedback`
- **Method:** `GET`

### Description
Returns all stored feedback entries.

### Related Functional Requirement
- FR-VIEW-001 — View Feedback Entries

### Business Flow Mapping
- BF-VIEW-001 — Retrieve Feedback List

### Request Structure

#### Ontology Mapping
- `ApiRequest`: empty retrieval request

#### Request Notes
- No request body is required.
- Future extensions may support filters, sorting, or pagination.

### Response Structure

#### Ontology Mapping
- `ApiResponse`: list of feedback entries

#### JSON Shape
```json
[
  {
    "id": 1,
    "name": "string",
    "feedback": "string"
  }
]
```

#### Response Notes
- Returns a JSON array of feedback objects.
- Current controller directly delegates to repository `findAll()`.

### Error Codes Returned

| Error Code | Description | Origin | Trigger Condition |
|---|---|---|---|
| FEEDBACK_NOT_FOUND | No feedback entries found in the system | Service | Service method `getAllFeedback()` receives an empty list |

### Service / Controller Handling the API

#### Controller
- `FeedbackController.getAll()`

#### Intended Service Path
- `FeedbackService.getAllFeedback()`

#### Repository
- `FeedbackRepository.findAll()`

### Current Implementation Note
The current `GET` endpoint bypasses the service layer. If the controller were changed to use the service, the empty-list error behavior would be active.

### End-to-End Traceability
`ApiEndpoint GET /api/feedback`  
→ `FeedbackController.getAll`  
→ `FeedbackService.getAllFeedback`  
→ `FeedbackRepository.findAll`  
→ `DataEntity: Feedback`

---

## API Contracts and Ontology Mapping

| Ontology Entity | Concrete Example in This System |
|---|---|
| `ApiEndpoint` | `POST /api/feedback`, `GET /api/feedback` |
| `ApiRequest` | Feedback JSON payload for submission |
| `ApiResponse` | Saved feedback entity or list of feedback entities |
| `handledBy` | `FeedbackController` |
| `invokesService` | Intended delegation to `FeedbackService` |
| `throwsError` | Validation and not-found errors |
| `originatesFrom` | Service validation and service/repository lookup behavior |

## API-to-Requirement Traceability Matrix

| API | Functional Requirement | Backend Logic | Errors |
|---|---|---|---|
| POST /api/feedback | FR-SUBMIT-001, FR-VALIDATE-001 | Save feedback, validate input, persist entity | FEEDBACK_EMPTY, FEEDBACK_TEXT_EMPTY, FEEDBACK_NAME_EMPTY, FEEDBACK_TEXT_TOO_LONG, FEEDBACK_NAME_TOO_LONG |
| GET /api/feedback | FR-VIEW-001 | Retrieve all feedback records | FEEDBACK_NOT_FOUND (service-path design) |

## API Design Observations

- The API surface is minimal and easy to understand.
- The current controller implementation is thinner in terms of code but less aligned with the ontology’s service-layer model.
- Standardized error responses and explicit HTTP status handling would improve compatibility with the ontology’s error-handling layer.
- The ontology already supports future request/response expansion, pagination, validation error payloads, and multi-service orchestration.

## Related Documentation

- [Business Flows](./business-flows.md)
- [Backend Logic](./backend-logic.md)
- [Frontend](./frontend.md)
- [Error Codes](./error-codes.md)
- [Sequence Flows](./sequence-flows.md)

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`, `backend/src/main/java/com/example/feedback/FeedbackRepository.java`, `frontend/src/App.js`