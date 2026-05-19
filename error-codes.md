# Error Handling Documentation

## Overview

This document describes error handling in the feedback application using the Software System Intelligence Ontology and the current implementation.

It is intended to help developers, testers, and architects answer:
- What error codes exist?
- Where did an error originate?
- What triggers the error?
- Which API or flow step is affected?
- How should the issue be resolved?

The ontology models error handling through:
- `ErrorCode`
- `ErrorOrigin`
- `ValidationError`
- `ExceptionFlow`
- `LogPoint`

The current implementation exposes error conditions primarily through `FeedbackService` and `FeedbackNotFoundException`.

## Error Summary

| Error Code | Name | Origin | Related API | Related Flow |
|---|---|---|---|---|
| FEEDBACK_EMPTY | Null Feedback Object | Service | POST /api/feedback | BF-SUBMIT-001 |
| FEEDBACK_TEXT_EMPTY | Empty Feedback Text | Service | POST /api/feedback | BF-SUBMIT-001 |
| FEEDBACK_NAME_EMPTY | Empty Name | Service | POST /api/feedback | BF-SUBMIT-001 |
| FEEDBACK_TEXT_TOO_LONG | Feedback Text Too Long | Service | POST /api/feedback | BF-SUBMIT-001 |
| FEEDBACK_NAME_TOO_LONG | Name Too Long | Service | POST /api/feedback | BF-SUBMIT-001 |
| FEEDBACK_NOT_FOUND | Feedback Not Found | Service / Database Lookup | GET /api/feedback, future GET by ID, update, delete | BF-VIEW-001 and future flows |

---

## Error Code: FEEDBACK_EMPTY

### Error Code and Name
- **Code:** `FEEDBACK_EMPTY`
- **Name:** Null Feedback Object

### Description
Raised when the backend receives a null feedback object instead of a valid request body.

### Origin
- **Primary Origin:** Service
- **Ontology Origin Type:** `SERVICE`

### Trigger Condition
- `validateFeedback(feedback)` receives `null`.

### Related API
- `POST /api/feedback`

### Related Flow Step
- BF-SUBMIT-001, Step 7 — backend validates payload

### Exception Path
`ApiEndpoint POST /api/feedback`  
→ `ServiceMethod saveFeedback`  
→ `ProcessingStep VALIDATION`  
→ `ErrorCode FEEDBACK_EMPTY`

### Suggested Resolution or Fix
- Ensure the frontend sends a valid JSON body.
- Add controller-level request validation if needed.
- Add frontend defensive checks before sending empty payloads.
- Standardize API error response formatting.

---

## Error Code: FEEDBACK_TEXT_EMPTY

### Error Code and Name
- **Code:** `FEEDBACK_TEXT_EMPTY`
- **Name:** Empty Feedback Text

### Description
Raised when the feedback text field is missing, null, or blank.

### Origin
- **Primary Origin:** Service
- **Ontology Origin Type:** `SERVICE`

### Trigger Condition
- `feedback.getFeedback() == null`
- `feedback.getFeedback().trim().isEmpty()`

### Related API
- `POST /api/feedback`

### Related Flow Step
- BF-SUBMIT-001, Step 7 — backend validates payload

### Suggested Resolution or Fix
- Ensure textarea input is not blank before submission.
- Keep frontend `required` validation enabled.
- Add inline frontend error rendering for failed submissions.
- Consider returning structured `ValidationError` payloads.

---

## Error Code: FEEDBACK_NAME_EMPTY

### Error Code and Name
- **Code:** `FEEDBACK_NAME_EMPTY`
- **Name:** Empty Name

### Description
Raised when the name field is missing, null, or blank.

### Origin
- **Primary Origin:** Service
- **Ontology Origin Type:** `SERVICE`

### Trigger Condition
- `feedback.getName() == null`
- `feedback.getName().trim().isEmpty()`

### Related API
- `POST /api/feedback`

### Related Flow Step
- BF-SUBMIT-001, Step 7 — backend validates payload

### Suggested Resolution or Fix
- Ensure name input is not blank.
- Retain browser `required` validation.
- Add user-friendly error messaging in the frontend.

---

## Error Code: FEEDBACK_TEXT_TOO_LONG

### Error Code and Name
- **Code:** `FEEDBACK_TEXT_TOO_LONG`
- **Name:** Feedback Text Too Long

### Description
Raised when the feedback text exceeds the maximum permitted length of 1000 characters.

### Origin
- **Primary Origin:** Service
- **Ontology Origin Type:** `SERVICE`

### Trigger Condition
- `feedback.getFeedback().length() > 1000`

### Related API
- `POST /api/feedback`

### Related Flow Step
- BF-SUBMIT-001, Step 7 — backend validates payload

### Suggested Resolution or Fix
- Add a max-length UI hint or character counter.
- Enforce a `maxLength` attribute in the frontend textarea.
- Truncate or reject oversized content according to business policy.

---

## Error Code: FEEDBACK_NAME_TOO_LONG

### Error Code and Name
- **Code:** `FEEDBACK_NAME_TOO_LONG`
- **Name:** Name Too Long

### Description
Raised when the name exceeds the maximum permitted length of 100 characters.

### Origin
- **Primary Origin:** Service
- **Ontology Origin Type:** `SERVICE`

### Trigger Condition
- `feedback.getName().length() > 100`

### Related API
- `POST /api/feedback`

### Related Flow Step
- BF-SUBMIT-001, Step 7 — backend validates payload

### Suggested Resolution or Fix
- Add a frontend `maxLength` attribute on the name input.
- Provide a UI hint indicating the allowed limit.
- Keep backend validation as authoritative.

---

## Error Code: FEEDBACK_NOT_FOUND

### Error Code and Name
- **Code:** `FEEDBACK_NOT_FOUND`
- **Name:** Feedback Not Found

### Description
Raised when requested feedback data is not present in the system.

### Origin
- **Primary Origin:** Service
- **Possible Extended Origin:** Database lookup / repository result semantics
- **Ontology Origin Type:** `SERVICE` or `DATABASE`

### Trigger Condition
- `getAllFeedback()` receives an empty list.
- `getFeedbackById(id)` cannot find a record.
- `deleteFeedback(id)` finds no matching record.
- Future update or delete flow references a missing ID.

### Related API
- `GET /api/feedback` (intended service-path behavior)
- Future `GET /api/feedback/{id}`
- Future `PUT /api/feedback/{id}`
- Future `DELETE /api/feedback/{id}`

### Related Flow Step
- BF-VIEW-001, retrieval and existence checks
- Future edit/delete flows

### Exception Path
`ApiEndpoint`  
→ `ServiceMethod getAllFeedback / getFeedbackById / deleteFeedback / updateFeedback`  
→ `ProcessingStep retrieval or existence validation`  
→ `ErrorCode FEEDBACK_NOT_FOUND`

### Suggested Resolution or Fix
- Verify that the requested record exists.
- Seed test data before retrieval tests.
- Return clearer empty-state responses for list flows if required by product design.
- Consider whether empty list should be an error or a valid response in the API contract.

---

## Error Origin Matrix

| Error Code | Error Origin | Concrete Source |
|---|---|---|
| FEEDBACK_EMPTY | SERVICE | `FeedbackService.validateFeedback` |
| FEEDBACK_TEXT_EMPTY | SERVICE | `FeedbackService.validateFeedback` |
| FEEDBACK_NAME_EMPTY | SERVICE | `FeedbackService.validateFeedback` |
| FEEDBACK_TEXT_TOO_LONG | SERVICE | `FeedbackService.validateFeedback` |
| FEEDBACK_NAME_TOO_LONG | SERVICE | `FeedbackService.validateFeedback` |
| FEEDBACK_NOT_FOUND | SERVICE / DATABASE | `getAllFeedback`, `getFeedbackById`, `deleteFeedback`, `updateFeedback` |

## Validation Error Mapping

| Field | Validation Failure | Error Code |
|---|---|---|
| `name` | blank | FEEDBACK_NAME_EMPTY |
| `name` | length > 100 | FEEDBACK_NAME_TOO_LONG |
| `feedback` | blank | FEEDBACK_TEXT_EMPTY |
| `feedback` | length > 1000 | FEEDBACK_TEXT_TOO_LONG |
| request object | null | FEEDBACK_EMPTY |

## Error Propagation Notes

### Current Propagation Model
1. Frontend submits request
2. Backend service validates or retrieves data
3. `FeedbackNotFoundException` is thrown with a business error code and message
4. Error propagates back through controller
5. Frontend currently has no custom error-rendering behavior

### Ontology-Aligned Propagation Model
`UIValidationRule` or `ProcessingStep`  
→ `ValidationError`  
→ `ErrorCode`  
→ `ErrorOrigin`  
→ `ExceptionFlow`  
→ `ApiResponse`  
→ frontend error display

### Current Gap
The implementation defines error codes but does not yet expose a standardized structured HTTP error response contract.

## Suggested Improvements

- Introduce a global exception handler (`@ControllerAdvice`).
- Return standardized JSON error payloads.
- Add explicit HTTP status codes for validation and not-found scenarios.
- Add frontend `try/catch` handling for API failures.
- Map backend error codes to inline UI messages.

## Related Documentation

- [API Specification](./api-specification.md)
- [Backend Logic](./backend-logic.md)
- [Business Flows](./business-flows.md)
- [Frontend](./frontend.md)
- [Sequence Flows](./sequence-flows.md)

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `backend/src/main/java/com/example/feedback/FeedbackService.java`, `backend/src/main/java/com/example/feedback/FeedbackNotFoundException.java`, `frontend/src/App.js`