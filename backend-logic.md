# Backend Logic Documentation

## Overview

This document describes backend logic for the feedback application using the Software System Intelligence Ontology and the current Java implementation.

It focuses on:
- service components
- service methods
- processing steps
- persistence flow
- linked APIs
- linked requirements
- backend validation and error generation

The ontology models a clean layered backend composed of Controller, Service, and Repository components. The current codebase contains all three concepts, although the controller currently bypasses the service layer for the exposed endpoints.

## Backend Component Summary

| Component | Type | Responsibility | Key Methods |
|---|---|---|---|
| `FeedbackController` | Controller | Exposes REST endpoints for feedback operations | `save`, `getAll` |
| `FeedbackService` | Service | Contains business logic, validation, retrieval, update, delete, and count operations | `saveFeedback`, `getAllFeedback`, `getFeedbackById`, `deleteFeedback`, `updateFeedback`, `countFeedback`, `validateFeedback` |
| `FeedbackRepository` | Repository | Persists and retrieves feedback entities through Spring Data JPA | `save`, `findAll`, `findById`, `existsById`, `deleteById`, `count` |

---

## Service Component: FeedbackController

### Component Type
Controller

### Purpose
Accepts HTTP requests under `/api/feedback` and returns REST responses.

### Linked APIs
- `POST /api/feedback`
- `GET /api/feedback`

### Linked Requirements
- FR-SUBMIT-001
- FR-VIEW-001
- FR-VALIDATE-001

### Methods

#### Method: save

##### Purpose
Receives a feedback payload and persists it.

##### Current Logic
1. Accept request body as `Feedback`.
2. Delegate directly to `repo.save(feedback)`.
3. Return saved entity.

##### Processing Steps
- Request binding
- Persistence invocation
- Response return

##### Architectural Observation
This method should ideally invoke `FeedbackService.saveFeedback(...)` so that backend validation and business rules are consistently applied.

#### Method: getAll

##### Purpose
Returns all feedback records.

##### Current Logic
1. Receive GET request.
2. Delegate directly to `repo.findAll()`.
3. Return list of feedback entries.

##### Processing Steps
- Request handling
- Repository retrieval
- Response return

##### Architectural Observation
This method should ideally invoke `FeedbackService.getAllFeedback()` to preserve service-layer semantics such as empty-result handling.

---

## Service Component: FeedbackService

### Component Type
Service

### Purpose
Contains business rules, validation logic, error generation, and service-oriented feedback operations.

### Linked APIs
- Intended handler path for `POST /api/feedback`
- Intended handler path for `GET /api/feedback`
- Supports future APIs for get-by-id, update, delete, and count

### Linked Requirements
- FR-SUBMIT-001
- FR-VIEW-001
- FR-VALIDATE-001
- Reliability and security-related non-functional requirements

### Methods

#### Method: saveFeedback(Feedback feedback)

##### Purpose
Validates and saves feedback.

##### Step-by-Step ProcessingSteps
1. **Validation**  
   Call `validateFeedback(feedback)`.
2. **Persistence**  
   Call `feedbackRepository.save(feedback)`.
3. **Response Handling**  
   Return the saved `Feedback` entity.

##### Linked APIs
- Intended for `POST /api/feedback`

##### Linked Requirements
- FR-SUBMIT-001
- FR-VALIDATE-001

##### Errors
- `FEEDBACK_EMPTY`
- `FEEDBACK_TEXT_EMPTY`
- `FEEDBACK_NAME_EMPTY`
- `FEEDBACK_TEXT_TOO_LONG`
- `FEEDBACK_NAME_TOO_LONG`

---

#### Method: getAllFeedback()

##### Purpose
Retrieves all feedback entries and throws a business error if none exist.

##### Step-by-Step ProcessingSteps
1. **Persistence Read**  
   Call `feedbackRepository.findAll()`.
2. **Validation / Business Check**  
   If the list is empty, throw `FeedbackNotFoundException("FEEDBACK_NOT_FOUND", ...)`.
3. **Response Handling**  
   Return the feedback list.

##### Linked APIs
- Intended for `GET /api/feedback`

##### Linked Requirements
- FR-VIEW-001

##### Errors
- `FEEDBACK_NOT_FOUND`

---

#### Method: getFeedbackById(Long id)

##### Purpose
Retrieves one feedback entry by ID.

##### Step-by-Step ProcessingSteps
1. **Persistence Read**  
   Call `feedbackRepository.findById(id)`.
2. **Validation / Business Check**  
   If not found, throw `FEEDBACK_NOT_FOUND`.
3. **Response Handling**  
   Return the entity.

##### Linked APIs
- Not currently exposed by controller
- Candidate future API: `GET /api/feedback/{id}`

##### Linked Requirements
- Future retrieval requirement

##### Errors
- `FEEDBACK_NOT_FOUND`

---

#### Method: deleteFeedback(Long id)

##### Purpose
Deletes an existing feedback entry by ID.

##### Step-by-Step ProcessingSteps
1. **Validation**  
   Check `feedbackRepository.existsById(id)`.
2. **Error Handling**  
   Throw `FEEDBACK_NOT_FOUND` when record does not exist.
3. **Persistence**  
   Call `feedbackRepository.deleteById(id)`.
4. **Response Handling**  
   Complete without returning a body.

##### Linked APIs
- Not currently exposed by controller
- Candidate future API: `DELETE /api/feedback/{id}`

##### Linked Requirements
- Future delete requirement

##### Errors
- `FEEDBACK_NOT_FOUND`

---

#### Method: updateFeedback(Long id, Feedback updatedFeedback)

##### Purpose
Updates an existing feedback entry after validating new input.

##### Step-by-Step ProcessingSteps
1. **Persistence Read**  
   Load existing entity using `getFeedbackById(id)`.
2. **Validation**  
   Call `validateFeedback(updatedFeedback)`.
3. **Transformation**  
   Copy name and feedback from input into existing entity.
4. **Persistence**  
   Call `feedbackRepository.save(existingFeedback)`.
5. **Response Handling**  
   Return updated entity.

##### Linked APIs
- Not currently exposed by controller
- Candidate future API: `PUT /api/feedback/{id}`

##### Linked Requirements
- Future update requirement
- FR-VALIDATE-001

##### Errors
- `FEEDBACK_NOT_FOUND`
- `FEEDBACK_EMPTY`
- `FEEDBACK_TEXT_EMPTY`
- `FEEDBACK_NAME_EMPTY`
- `FEEDBACK_TEXT_TOO_LONG`
- `FEEDBACK_NAME_TOO_LONG`

---

#### Method: countFeedback()

##### Purpose
Returns the total number of feedback records.

##### Step-by-Step ProcessingSteps
1. **Persistence Read**
   Call `feedbackRepository.count()`.
2. **Response Handling**
   Return count.

##### Linked APIs
- Not currently exposed by controller
- Candidate future API: `GET /api/feedback/count`

##### Linked Requirements
- Operational / reporting extension

---

#### Method: validateFeedback(Feedback feedback)

##### Purpose
Performs backend validation of the feedback payload.

##### Step-by-Step ProcessingSteps

1. **Null Check**
   - If `feedback == null`, throw `FEEDBACK_EMPTY`.

2. **Feedback Text Validation**
   - If feedback text is null or blank, throw `FEEDBACK_TEXT_EMPTY`.
   - If feedback text length exceeds 1000, throw `FEEDBACK_TEXT_TOO_LONG`.

3. **Name Validation**
   - If name is null or blank, throw `FEEDBACK_NAME_EMPTY`.
   - If name length exceeds 100, throw `FEEDBACK_NAME_TOO_LONG`.

##### Linked APIs
- Intended for `POST /api/feedback`
- Intended for future update APIs

##### Linked Requirements
- FR-VALIDATE-001
- SR-INPUT-001
- NFR-RELIABILITY-001

##### Processing Step Categories
- Validation
- Error generation
- Early rejection before persistence

---

## Service Component: FeedbackRepository

### Component Type
Repository

### Purpose
Provides persistence operations for `Feedback` entities through Spring Data JPA.

### Linked APIs
- Indirectly supports all current and future feedback APIs

### Linked Requirements
- FR-SUBMIT-001
- FR-VIEW-001
- FR-VALIDATE-001

### Repository Operations

| Repository Method | Purpose |
|---|---|
| `save` | Insert or update feedback |
| `findAll` | Retrieve all feedback |
| `findById` | Retrieve one feedback by ID |
| `existsById` | Check if a feedback record exists |
| `deleteById` | Delete feedback by ID |
| `count` | Count feedback records |

---

## Processing Steps by Use Case

### Submission Path
1. Request accepted by controller
2. Validation of input
3. Persistence of entity
4. Success response returned

### Retrieval Path
1. Request accepted by controller
2. Repository list retrieval
3. Optional empty-result business validation
4. Response returned

### Update Path (Service-ready, API not exposed)
1. Fetch existing entity
2. Validate updated payload
3. Transform fields
4. Persist updated entity
5. Return updated entity

### Delete Path (Service-ready, API not exposed)
1. Check existence
2. Throw not-found error if absent
3. Delete entity

---

## Linked APIs Matrix

| Backend Method | Linked API |
|---|---|
| `FeedbackController.save` | POST /api/feedback |
| `FeedbackController.getAll` | GET /api/feedback |
| `FeedbackService.saveFeedback` | Intended POST /api/feedback path |
| `FeedbackService.getAllFeedback` | Intended GET /api/feedback path |
| `FeedbackService.getFeedbackById` | Future GET by ID |
| `FeedbackService.updateFeedback` | Future PUT/PATCH |
| `FeedbackService.deleteFeedback` | Future DELETE |
| `FeedbackService.countFeedback` | Future count endpoint |

## Error Generation Matrix

| Method | Error Codes |
|---|---|
| `validateFeedback` | FEEDBACK_EMPTY, FEEDBACK_TEXT_EMPTY, FEEDBACK_NAME_EMPTY, FEEDBACK_TEXT_TOO_LONG, FEEDBACK_NAME_TOO_LONG |
| `getAllFeedback` | FEEDBACK_NOT_FOUND |
| `getFeedbackById` | FEEDBACK_NOT_FOUND |
| `deleteFeedback` | FEEDBACK_NOT_FOUND |
| `updateFeedback` | FEEDBACK_NOT_FOUND + validation errors |

## Architectural Notes

- The ontology’s ideal path is `ApiEndpoint -> Controller -> Service -> Repository -> DataEntity`.
- The current controller takes a shortcut to `Repository`, which weakens enforcement of service-layer invariants.
- The service layer is more complete than the currently exposed API surface.
- Error handling exists, but standardized exception-to-HTTP mapping is not yet documented in the controller layer.

## Related Documentation

- [API Specification](./api-specification.md)
- [Business Flows](./business-flows.md)
- [Error Codes](./error-codes.md)
- [Sequence Flows](./sequence-flows.md)

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`, `backend/src/main/java/com/example/feedback/FeedbackRepository.java`