# Frontend Documentation

## Overview

This document describes the frontend behavior of the feedback application using the Software System Intelligence Ontology and the current React implementation.

The frontend currently consists of a single screen with a simple feedback form. Even with this minimal implementation, the ontology supports tracing:
- screen purpose
- UI components
- field validation rules
- navigation behavior
- APIs triggered from the UI
- resulting backend and error flow

## Frontend Summary

| UIScreen | Purpose | Components | APIs Triggered | Navigation |
|---|---|---|---|---|
| Program Feedback Screen | Collect feedback from the user | Name input, Feedback textarea, Submit button, Form container | POST /api/feedback | Self-contained single-screen flow |

---

## UIScreen: Program Feedback Screen

### Screen Name and Purpose
**Program Feedback Screen**

This screen allows a user to enter their name and a feedback message and submit that data to the backend.

### Route / Screen Notes
- Implemented in `frontend/src/App.js`
- No explicit router is used in the current frontend
- This screen behaves as the application entry screen

### Related Functional Requirements
- FR-SUBMIT-001 — Submit Feedback
- FR-VALIDATE-001 — Validate Feedback Input

### Related Business Flow
- BF-SUBMIT-001 — Submit Feedback

### UI Components

| Component | Type | Purpose | Required |
|---|---|---|---|
| Feedback Form | FORM | Wraps the submission workflow | Yes |
| Name Input | INPUT | Captures the user name | Yes |
| Feedback Textarea | INPUT / TEXTAREA | Captures feedback text | Yes |
| Submit Button | BUTTON | Triggers form submission | Yes |
| Title Header | DISPLAY | Labels the page as Program Feedback | No |

### Component Details

#### Feedback Form
- **Purpose:** groups the input fields and submission action
- **Behavior:** submits via `onSubmit={handleSubmit}`
- **API Trigger:** POST `/api/feedback`

#### Name Input
- **Purpose:** capture the user name
- **React State:** `name`
- **Validation:** `required`
- **Change Handler:** `setName(e.target.value)`

#### Feedback Textarea
- **Purpose:** capture feedback text
- **React State:** `feedback`
- **Validation:** `required`
- **Change Handler:** `setFeedback(e.target.value)`

#### Submit Button
- **Purpose:** initiate submission
- **Type:** `submit`
- **Behavior:** triggers form submit handler

---

## UI Validation Rules

### Rule Set Summary

| Field | Rule Type | Source | Message / Effect |
|---|---|---|---|
| `name` | REQUIRED | Browser / HTML | Submission blocked if empty |
| `feedback` | REQUIRED | Browser / HTML | Submission blocked if empty |

### Rule: Name Required

- **Field:** `name`
- **Validation Type:** REQUIRED
- **Rule Expression:** non-empty input
- **User Visible:** Yes
- **Effect:** Browser prevents form submission if name is empty

### Rule: Feedback Required

- **Field:** `feedback`
- **Validation Type:** REQUIRED
- **Rule Expression:** non-empty textarea
- **User Visible:** Yes
- **Effect:** Browser prevents form submission if feedback is empty

### Backend Validation Complements UI Validation
The frontend only enforces required fields. The backend additionally enforces:
- max name length = 100
- max feedback length = 1000
- null request handling

This is important because browser validation alone is not sufficient for trusted enforcement.

---

## APIs Triggered from UI

### POST /api/feedback

#### Trigger Source
- Feedback Form submission
- Submit Button

#### Trigger Logic
```javascript
await axios.post("http://localhost:8080/api/feedback", {
  name,
  feedback
});
```

#### Purpose
Sends user-entered feedback data to the backend for persistence.

#### Success Handling
- Shows `alert("Feedback submitted!")`
- Clears `name` state
- Clears `feedback` state

#### Error Handling Note
The current frontend does not have explicit `try/catch` or UI rendering for backend errors. Failed API calls are therefore not yet mapped to user-friendly error messages on the screen.

---

## Navigation Flow

## Current Navigation Model
The application is currently a single-screen interface.

### Screen-to-Screen Transitions
There are no implemented multi-screen transitions yet.

### Navigation Semantics in Ontology
The ontology still supports `Navigation` and `navigatesTo` relationships for future scenarios such as:
- submission confirmation screen
- feedback history screen
- admin review screen
- error redirect screen

### Current Navigation Representation

| From Screen | To Screen | Trigger | Condition |
|---|---|---|---|
| Program Feedback Screen | Program Feedback Screen | Form interaction | User stays on same view after submit |
| Program Feedback Screen | Future Confirmation / Error Screen | Not implemented | Future extension |

---

## UI-to-API Traceability

| UI Element | Action | API |
|---|---|---|
| Feedback Form | Submit | POST /api/feedback |
| Submit Button | Triggers form submit | POST /api/feedback |
| Name Input | Contributes request data | POST /api/feedback |
| Feedback Textarea | Contributes request data | POST /api/feedback |

## UI-to-Backend Traceability

| UI Element | Backend Path |
|---|---|
| Name Input | Included in request body → validation → persistence |
| Feedback Textarea | Included in request body → validation → persistence |
| Submit Button | Triggers request handling in controller |
| Required field validation | Prevents empty request when browser validation is active |

## Error and Validation Traceability

### Possible Frontend-Originated Validation Errors
- Empty `name`
- Empty `feedback`

### Backend-Originated Errors Relevant to Frontend
- `FEEDBACK_EMPTY`
- `FEEDBACK_TEXT_EMPTY`
- `FEEDBACK_NAME_EMPTY`
- `FEEDBACK_TEXT_TOO_LONG`
- `FEEDBACK_NAME_TOO_LONG`

### Current Gap
Frontend does not yet map backend validation errors into inline field messages, banners, or structured error UI.

---

## Example User Journey

```text
User opens Program Feedback screen
→ enters name
→ enters feedback
→ browser checks required fields
→ user clicks Submit
→ frontend sends POST /api/feedback
→ backend persists data
→ success alert is shown
→ form fields are cleared
```

## Recommended Frontend Enhancements

- Add inline validation messages for backend error codes.
- Add max-length hints for name and feedback fields.
- Introduce loading state during API calls.
- Introduce success and error panels instead of `alert(...)`.
- Add navigation to a feedback-history or confirmation screen.
- Standardize frontend error handling using ontology `ValidationError` and `ErrorCode` concepts.

## Related Documentation

- [Functional Requirements](./functional-requirements.md)
- [Business Flows](./business-flows.md)
- [API Specification](./api-specification.md)
- [Error Codes](./error-codes.md)
- [Sequence Flows](./sequence-flows.md)

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `frontend/src/App.js`