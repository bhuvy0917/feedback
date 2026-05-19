# Business Flows Documentation

## Overview

This document describes end-to-end business flows for the feedback application using the Software System Intelligence Ontology.

It connects:
- functional requirements
- frontend screens and components
- API calls
- backend controller/service/repository logic
- persistence
- error conditions

The current implementation directly supports feedback submission and backend retrieval. The ontology also supports future flows for richer UI behavior and cross-service execution.

## Business Flow Summary

| Flow ID | Flow Name | Actors | Related Functional Requirements | APIs |
|---|---|---|---|---|
| BF-SUBMIT-001 | Submit Feedback | User, Frontend, Backend API, Repository | FR-SUBMIT-001, FR-VALIDATE-001 | POST /api/feedback |
| BF-VIEW-001 | Retrieve Feedback List | API Consumer, Backend API, Repository | FR-VIEW-001 | GET /api/feedback |

---

## Business Flow: BF-SUBMIT-001

### Flow Name
Submit Feedback

### Description
This flow captures the end-to-end process where a user enters feedback in the frontend form and submits it for backend persistence.

### Actors Involved
- User
- Frontend application
- Feedback API
- Controller layer
- Service layer
- Repository layer
- Database

### Related Functional Requirements
- FR-SUBMIT-001 — Submit Feedback
- FR-VALIDATE-001 — Validate Feedback Input

### Step-by-Step Flow

| Step | Actor | Flow Step | Description | API Triggered |
|---|---|---|---|---|
| 1 | User | Open feedback screen | User accesses the Program Feedback screen | None |
| 2 | User | Enter name | User types a name into the name input field | None |
| 3 | User | Enter feedback text | User types text into the feedback textarea | None |
| 4 | Frontend | Validate required fields | Browser enforces `required` validation for both fields | None |
| 5 | User | Submit form | User clicks the Submit button | POST /api/feedback |
| 6 | API | Receive request | Backend receives JSON payload containing name and feedback | POST /api/feedback |
| 7 | Service | Validate payload | Backend validation checks null, empty, and max-length constraints | Internal |
| 8 | Repository | Persist feedback | Repository saves the feedback entity | Internal DB write |
| 9 | API | Return success response | Backend returns saved feedback object | Response from POST |
| 10 | Frontend | Show success and reset form | UI shows alert and clears input state | None |

### APIs Triggered in Each Step

| Flow Step | API | Method | Purpose |
|---|---|---|---|
| Submit form | `/api/feedback` | POST | Submit feedback payload for validation and persistence |

### Ontology Traceability Path
`UIScreen` → `UIComponent` → `UIValidationRule` → `ApiEndpoint` → `ApiRequest` → `ServiceComponent` → `ServiceMethod` → `ProcessingStep` → `DataEntity`

### Current Implementation Mapping

| Ontology Element | Current Implementation |
|---|---|
| UIScreen | Program Feedback screen in `App.js` |
| UIComponent | Name input, Feedback textarea, Submit button |
| UIValidationRule | HTML `required` attributes |
| ApiEndpoint | `POST /api/feedback` |
| Controller | `FeedbackController.save` |
| Service | `FeedbackService.saveFeedback` |
| Validation Step | `validateFeedback` |
| Repository | `FeedbackRepository.save` |

### Error Paths
- UI required validation may block submission before API call.
- Backend validation may raise:
  - `FEEDBACK_EMPTY`
  - `FEEDBACK_TEXT_EMPTY`
  - `FEEDBACK_NAME_EMPTY`
  - `FEEDBACK_TEXT_TOO_LONG`
  - `FEEDBACK_NAME_TOO_LONG`

### Example Narrative
```text
User opens the feedback page
→ enters a name and feedback message
→ clicks Submit
→ frontend posts to /api/feedback
→ backend validates payload
→ repository saves entity
→ backend returns success
→ frontend alerts "Feedback submitted!" and clears the form
```

---

## Business Flow: BF-VIEW-001

### Flow Name
Retrieve Feedback List

### Description
This flow captures how a consumer retrieves all stored feedback entries from the backend.

### Actors Involved
- API consumer
- Backend API
- Service layer
- Repository layer
- Database

### Related Functional Requirements
- FR-VIEW-001 — View Feedback Entries

### Step-by-Step Flow

| Step | Actor | Flow Step | Description | API Triggered |
|---|---|---|---|---|
| 1 | Consumer | Send retrieval request | Consumer invokes the list endpoint | GET /api/feedback |
| 2 | API | Accept request | Controller receives request for all feedback | GET /api/feedback |
| 3 | Service / Repository | Fetch data | Repository or service fetches all feedback records | Internal DB read |
| 4 | API | Return list | Backend returns all feedback records | Response from GET |
| 5 | Consumer | Consume result | UI or external client renders or processes results | None |

### APIs Triggered in Each Step

| Flow Step | API | Method | Purpose |
|---|---|---|---|
| Send retrieval request | `/api/feedback` | GET | Fetch all stored feedback entries |

### Current Implementation Mapping

| Ontology Element | Current Implementation |
|---|---|
| ApiEndpoint | `GET /api/feedback` |
| Controller | `FeedbackController.getAll` |
| Service | `FeedbackService.getAllFeedback` exists but is not used by controller |
| Repository | `FeedbackRepository.findAll` |

### Error Paths
- If the service path is used, an empty result can trigger `FEEDBACK_NOT_FOUND`.
- In the current controller implementation, `findAll()` returns an empty list directly.

### Architecture Gap
The ontology suggests the list flow should pass through the service layer for consistent business semantics, but the current controller directly queries the repository.

---

## Flow-to-Requirement Matrix

| Flow | Functional Requirements |
|---|---|
| BF-SUBMIT-001 | FR-SUBMIT-001, FR-VALIDATE-001 |
| BF-VIEW-001 | FR-VIEW-001 |

## Flow-to-API Matrix

| Flow | APIs Called |
|---|---|
| BF-SUBMIT-001 | POST /api/feedback |
| BF-VIEW-001 | GET /api/feedback |

## Flow-to-Backend Matrix

| Flow | Controller | Service | Repository |
|---|---|---|---|
| BF-SUBMIT-001 | `FeedbackController.save` | Intended: `FeedbackService.saveFeedback` | `FeedbackRepository.save` |
| BF-VIEW-001 | `FeedbackController.getAll` | Intended: `FeedbackService.getAllFeedback` | `FeedbackRepository.findAll` |

## Notes for Developers and Testers

- Submission is the clearest fully observable business flow in the current UI.
- Retrieval is backend-implemented but not yet exposed through a dedicated frontend screen.
- Error behavior is stronger in the service layer than in the controller layer currently used by REST endpoints.
- The ontology can support future flows such as edit, delete, approval, moderation, and export without restructuring the flow model.

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `frontend/src/App.js`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`, `backend/src/main/java/com/example/feedback/FeedbackRepository.java`