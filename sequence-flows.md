# Sequence and Interaction Documentation

## Overview

This document describes execution and interaction flows using the Software System Intelligence Ontology.

It is intended to make the system understandable from:
- end-to-end execution order
- normal success paths
- error scenarios
- validation boundaries
- UI to API to backend to database traceability

The ontology supports sequence reasoning through:
- `BusinessFlow`
- `FlowStep`
- `sequenceOrder`
- `requestTo`
- `responseFrom`
- `invokesService`
- `callsMicroservice`
- `propagatesTo`

## Sequence Flow Summary

| Business Flow | Main Scenario | Error Scenarios |
|---|---|---|
| BF-SUBMIT-001 | User submits feedback successfully | Empty fields, oversized fields, null payload, backend validation failure |
| BF-VIEW-001 | Consumer retrieves feedback list | No data found, future lookup failures |

---

# Sequence Flow: BF-SUBMIT-001

## Flow Name
Submit Feedback

## End-to-End Interaction
`UI → API → Controller → Service → Repository → DB → Response`

## Normal Flow

### Step-by-Step Execution

1. **UI Screen**
   - User opens the Program Feedback Screen.

2. **UI Components**
   - User enters text into:
     - Name input
     - Feedback textarea

3. **Frontend Validation**
   - Browser validates required fields.
   - If either field is empty, submission does not proceed.

4. **API Invocation**
   - Frontend triggers:
     - `POST /api/feedback`

5. **Controller Handling**
   - `FeedbackController.save(@RequestBody Feedback feedback)` receives the request.

6. **Service Logic**
   - Intended ontology path:
     - `FeedbackService.saveFeedback(feedback)`
   - Validation is executed through:
     - `validateFeedback(feedback)`

7. **Processing Steps**
   - Validation
   - Persistence

8. **Repository Call**
   - `FeedbackRepository.save(feedback)`

9. **Database Persistence**
   - Feedback entity is inserted into the backing store.

10. **Response Handling**
    - Saved entity is returned to the frontend.

11. **UI Success Behavior**
    - Frontend shows success alert.
    - Name and feedback fields are reset.

## Sequence Table

| Order | Layer | Participant | Action |
|---|---|---|---|
| 1 | Frontend | Program Feedback Screen | Render form |
| 2 | Frontend | Name Input / Feedback Textarea | Capture user input |
| 3 | Frontend | UIValidationRule | Enforce required fields |
| 4 | API | POST /api/feedback | Receive request |
| 5 | Backend | FeedbackController.save | Accept request body |
| 6 | Backend | FeedbackService.saveFeedback | Validate and orchestrate save |
| 7 | Backend | validateFeedback | Apply validation rules |
| 8 | Backend | FeedbackRepository.save | Persist entity |
| 9 | Data | Feedback table/entity | Store record |
| 10 | API | Response | Return saved feedback |
| 11 | Frontend | App.js | Show alert and reset form |

## Normal Flow Traceability
`UIScreen`  
→ `UIComponent`  
→ `UIValidationRule`  
→ `ApiEndpoint POST /api/feedback`  
→ `ServiceComponent Controller`  
→ `ServiceMethod saveFeedback`  
→ `ProcessingStep VALIDATION`  
→ `ProcessingStep PERSISTENCE`  
→ `DataEntity Feedback`  
→ `ApiResponse`

---

## Error Scenario A: Empty Name or Feedback Field at UI

### Where Error Can Occur
- Frontend validation stage

### Sequence
1. User leaves name or feedback empty.
2. Browser detects `required` constraint.
3. Form submission is blocked.
4. API call is not sent.

### Error Origin
- UI

### Ontology Interpretation
`UIValidationRule`  
→ `ValidationError`  
→ user-visible validation block

### Impact
- No backend processing
- No persistence
- No server-side exception

---

## Error Scenario B: Invalid Payload Reaches Backend

### Where Error Can Occur
- Service validation stage

### Sequence
1. Frontend sends request.
2. Controller receives payload.
3. Service validation checks request.
4. Validation fails.
5. `FeedbackNotFoundException` is thrown with one of the following:
   - `FEEDBACK_EMPTY`
   - `FEEDBACK_TEXT_EMPTY`
   - `FEEDBACK_NAME_EMPTY`
   - `FEEDBACK_TEXT_TOO_LONG`
   - `FEEDBACK_NAME_TOO_LONG`
6. Exception propagates back to API boundary.
7. Frontend currently has no explicit error rendering.

### Error Origin
- Service

### Ontology Interpretation
`ProcessingStep VALIDATION`  
→ `ValidationError`  
→ `ErrorCode`  
→ `ErrorOrigin(SERVICE)`  
→ `ExceptionFlow`  
→ `ApiResponse`

### Impact
- Persistence does not occur
- User likely sees failed request behavior in browser console unless explicit UI handling is added

---

## Error Scenario C: Repository / Persistence Failure

### Where Error Can Occur
- Repository or database stage

### Sequence
1. Validation passes.
2. Repository save is attempted.
3. Database or persistence layer fails.
4. Exception propagates to controller and client.

### Error Origin
- Database / Persistence

### Ontology Interpretation
`DataEntity`  
→ `ErrorCode`  
→ `ErrorOrigin(DATABASE)`  
→ `ExceptionFlow`

### Current State
No explicit persistence error mapping is implemented in the current code, but the ontology supports documenting it.

---

# Sequence Flow: BF-VIEW-001

## Flow Name
Retrieve Feedback List

## End-to-End Interaction
`Client → API → Controller → Service → Repository → DB → Response`

## Normal Flow

### Step-by-Step Execution

1. Client invokes:
   - `GET /api/feedback`

2. Controller receives request:
   - `FeedbackController.getAll()`

3. Repository access occurs:
   - `repo.findAll()`

4. Results are returned as a JSON list.

## Sequence Table

| Order | Layer | Participant | Action |
|---|---|---|---|
| 1 | Client / UI | API consumer | Request feedback list |
| 2 | API | GET /api/feedback | Receive request |
| 3 | Backend | FeedbackController.getAll | Handle request |
| 4 | Backend | FeedbackRepository.findAll | Retrieve all rows |
| 5 | Data | Feedback table/entity | Return stored data |
| 6 | API | Response | Return list to caller |

## Alternate Service-Aligned Flow
The ontology-preferred flow inserts service logic:

| Order | Layer | Participant | Action |
|---|---|---|---|
| 1 | Client / UI | API consumer | Request feedback list |
| 2 | API | GET /api/feedback | Receive request |
| 3 | Backend | FeedbackController.getAll | Delegate request |
| 4 | Backend | FeedbackService.getAllFeedback | Apply retrieval semantics |
| 5 | Backend | FeedbackRepository.findAll | Fetch data |
| 6 | Data | Feedback table/entity | Return data |
| 7 | API | Response | Return list or error |

---

## Error Scenario D: No Feedback Found

### Where Error Can Occur
- Service retrieval check

### Sequence
1. Retrieval request reaches service.
2. `findAll()` returns an empty list.
3. Service throws `FEEDBACK_NOT_FOUND`.
4. Exception propagates to API response.

### Error Origin
- Service / Database lookup semantics

### Ontology Interpretation
`ServiceMethod getAllFeedback`  
→ `ProcessingStep retrieval validation`  
→ `ErrorCode FEEDBACK_NOT_FOUND`  
→ `ErrorOrigin(SERVICE or DATABASE)`  
→ `ExceptionFlow`

### Current Implementation Note
This behavior is defined in `FeedbackService`, but the active controller endpoint currently bypasses that service path.

---

## Cross-Flow Error Points

| Layer | Potential Error Point | Example |
|---|---|---|
| UI | Missing required input | Empty name or feedback |
| API | Invalid or malformed request body | Null payload |
| Service | Business-rule validation failure | Overlength text |
| Repository | Missing record or persistence issue | Not found / DB failure |
| Database | Insert or read failure | Infrastructure or schema issue |

## Sequence Diagram Readiness

The ontology and documentation support sequence diagram generation because they identify:
- participants
- order of interaction
- branching points
- error origins
- propagation paths

### Suggested Participants for Diagram Generation
- User
- Program Feedback Screen
- API Endpoint
- FeedbackController
- FeedbackService
- FeedbackRepository
- Feedback Database
- ExceptionFlow / ErrorCode

## Related Documentation

- [Business Flows](./business-flows.md)
- [API Specification](./api-specification.md)
- [Backend Logic](./backend-logic.md)
- [Frontend](./frontend.md)
- [Error Codes](./error-codes.md)
- [Architecture](./architecture.md)

---

**Source Alignment:** `feedback-requirements-schema.jsonld`, `frontend/src/App.js`, `backend/src/main/java/com/example/feedback/FeedbackController.java`, `backend/src/main/java/com/example/feedback/FeedbackService.java`, `backend/src/main/java/com/example/feedback/FeedbackRepository.java`