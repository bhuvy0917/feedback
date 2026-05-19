# Functional Requirements Documentation

## Overview

This document describes the functional requirements defined by the Software System Intelligence Ontology and mapped to the current feedback application implementation.

Functional requirements are traced across:
- Frontend screens and components
- Business flows and flow steps
- API endpoints
- Backend controller, service, and repository logic
- Validation and error handling

## Traceability Summary

| Requirement | Description | Flow | UI | API | Backend | Status |
|---|---|---|---|---|---|---|
| FR-SUBMIT-001 | User can submit feedback from the web form | BF-SUBMIT-001 | Program Feedback Screen | POST /api/feedback | FeedbackController.save, FeedbackService.saveFeedback, FeedbackRepository.save | Partially aligned |
| FR-VIEW-001 | User or admin can retrieve submitted feedback entries | BF-VIEW-001 | Future feedback list screen / API consumer | GET /api/feedback | FeedbackController.getAll, FeedbackService.getAllFeedback, FeedbackRepository.findAll | Backend available, UI not present |
| FR-VALIDATE-001 | System validates feedback name and feedback text before persistence | BF-SUBMIT-001 | Program Feedback Screen | POST /api/feedback | FeedbackService.validateFeedback | Implemented in backend; minimal UI validation |

---

## Functional Requirement: FR-SUBMIT-001

### Name
Submit Feedback

### Description
The system shall allow a user to submit feedback using a frontend form with a name and feedback message.

### Acceptance Criteria
- User can enter a name.
- User can enter feedback text.
- User can submit the form.
- The frontend triggers `POST /api/feedback`.
- The backend persists the feedback record.
- The user receives a success indication after submission.
- The form is reset after successful submission.

### Business Rules
- `name` is required.
- `feedback` is required.
- `name` must not exceed 100 characters.
- `feedback` must not exceed 1000 characters.
- Persisted feedback is stored through the repository layer.

### Priority and Status
- **Priority:** CRITICAL
- **Status:** IMPLEMENTED

### Relationships

#### dependsOn
- FR-VALIDATE-001 — submission depends on successful validation of input data.

#### conflictsWith
- No direct conflict identified in the current system.
- Potential future conflict: stronger moderation or approval workflows may delay immediate submission confirmation.

#### verifiedBy
- `BusinessFlow`: BF-SUBMIT-001
- `ApiEndpoint`: `POST /api/feedback`
- `ServiceMethod`: `saveFeedback(Feedback feedback)`
- `UIScreen`: Program Feedback Screen

### Architecture Traceability
- **UI Screen:** Program Feedback Screen
- **UI Components:** Name input, Feedback textarea, Submit button
- **API:** `POST /api/feedback`
- **Controller:** `FeedbackController.save`
- **Service:** `FeedbackService.saveFeedback`
- **Repository:** `FeedbackRepository.save`
- **Persistence:** `Feedback` entity saved to database

### Example
```text
User enters name and feedback
→ clicks Submit
→ frontend posts payload to /api/feedback
→ backend validates
→ repository saves entity
→ success response returned
→ alert shown to user
```

---

## Functional Requirement: FR-VIEW-001

### Name
View Feedback Entries

### Description
The system shall provide a way to retrieve stored feedback entries from the backend.

### Acceptance Criteria
- A consumer can invoke `GET /api/feedback`.
- The backend returns a list of feedback entries when data exists.
- If no feedback is present, the service layer can raise a not-found exception.
- The endpoint supports list retrieval for future UI or administrative use.

### Business Rules
- Feedback is returned as a list.
- Retrieval uses the repository layer.
- Empty results may trigger a business error in the service layer (`FEEDBACK_NOT_FOUND`).

### Priority and Status
- **Priority:** HIGH
- **Status:** PARTIALLY IMPLEMENTED

### Relationships

#### dependsOn
- Availability of persisted feedback data.
- Repository access to stored feedback records.

#### conflictsWith
- No direct conflict identified.
- Potential conflict with future authorization requirements if read access becomes restricted.

#### verifiedBy
- `BusinessFlow`: BF-VIEW-001
- `ApiEndpoint`: `GET /api/feedback`
- `ServiceMethod`: `getAllFeedback()`
- `ServiceMethod`: `countFeedback()`

### Architecture Traceability
- **UI Screen:** Not yet implemented in frontend
- **API:** `GET /api/feedback`
- **Controller:** `FeedbackController.getAll`
- **Service:** `FeedbackService.getAllFeedback`
- **Repository:** `FeedbackRepository.findAll`
- **Persistence:** Feedback table/entity records

### Gap Note
The current controller directly uses the repository and does not invoke `FeedbackService.getAllFeedback()`. The ontology supports the service-oriented trace, but current code only partially reflects that design.

---

## Functional Requirement: FR-VALIDATE-001

### Name
Validate Feedback Input

### Description
The system shall validate user-provided feedback data before saving it.

### Acceptance Criteria
- Null feedback objects are rejected.
- Empty feedback text is rejected.
- Empty name is rejected.
- Feedback text longer than 1000 characters is rejected.
- Name longer than 100 characters is rejected.
- Validation failures produce explicit error codes.

### Business Rules
- `FEEDBACK_EMPTY` when the feedback object is null.
- `FEEDBACK_TEXT_EMPTY` when feedback text is missing or blank.
- `FEEDBACK_NAME_EMPTY` when name is missing or blank.
- `FEEDBACK_TEXT_TOO_LONG` when feedback exceeds 1000 characters.
- `FEEDBACK_NAME_TOO_LONG` when name exceeds 100 characters.

### Priority and Status
- **Priority:** CRITICAL
- **Status:** IMPLEMENTED

### Relationships

#### dependsOn
- Input arriving from the frontend or API payload.
- Backend validation logic in `FeedbackService.validateFeedback`.

#### conflictsWith
- No direct conflicts identified.
- Future relaxed validation or draft-saving features may conflict with mandatory field rules.

#### verifiedBy
- `UIValidationRule`: HTML `required` on name input
- `UIValidationRule`: HTML `required` on feedback textarea
- `ApiEndpoint`: `POST /api/feedback`
- `ServiceMethod`: `validateFeedback(Feedback feedback)`
- `ProcessingStep`: Backend validation before persistence

### Architecture Traceability
- **Frontend Validation:** Browser-required validation on input and textarea
- **Backend Validation:** `FeedbackService.validateFeedback`
- **Errors Raised:** `FEEDBACK_EMPTY`, `FEEDBACK_TEXT_EMPTY`, `FEEDBACK_NAME_EMPTY`, `FEEDBACK_TEXT_TOO_LONG`, `FEEDBACK_NAME_TOO_LONG`

---

## Related Business Flows

| Flow | Description | Related Requirements |
|---|---|---|
| BF-SUBMIT-001 | User submits feedback from UI to backend persistence | FR-SUBMIT-001, FR-VALIDATE-001 |
| BF-VIEW-001 | Consumer retrieves stored feedback entries | FR-VIEW-001 |

## Related APIs

| API | Purpose | Related Requirements |
|---|---|---|
| POST /api/feedback | Submit feedback | FR-SUBMIT-001, FR-VALIDATE-001 |
| GET /api/feedback | Retrieve feedback list | FR-VIEW-001 |

## Related Backend Methods

| Method | Purpose | Related Requirements |
|---|---|---|
| `saveFeedback` | Validate and save feedback | FR-SUBMIT-001, FR-VALIDATE-001 |
| `validateFeedback` | Enforce backend rules | FR-VALIDATE-001 |
| `getAllFeedback` | Retrieve all feedback with empty-list guard | FR-VIEW-001 |
| `getFeedbackById` | Retrieve one feedback entry | Future extension |
| `updateFeedback` | Update feedback after validation | Future extension |
| `deleteFeedback` | Delete feedback by ID | Future extension |

## Notes for Developers, Testers, and Architects

- The ontology defines traceability from requirement to flow, API, service, repository, and error.
- The current controller bypasses the service layer for the implemented REST endpoints, which creates a design gap relative to the ontology.
- Backend validation is stronger than frontend validation in the current codebase.
- Future UI screens for listing, editing, and deleting feedback can extend the same ontology model without structural changes.

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `frontend/src/App.js`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`