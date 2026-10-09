
MediKiosk is a proposed healthcare software platform designed to help patients record their medical history before a consultation. It aims to combine guided patient intake, medical-document digitization, and an editable clinical summary so healthcare professionals can review relevant information more efficiently.
Project status: In development — initial planning and setup. Features below describe the intended scope; they should not be considered implemented until they are completed and tested.
The Problem
In busy healthcare settings, collecting a patient's history and reviewing paper prescriptions, laboratory reports, and discharge summaries can take valuable consultation time. Records may be incomplete, difficult to read, or spread across multiple providers. Patients may also have difficulty explaining their history in a language or format they are comfortable with.
MediKiosk aims to provide a guided digital intake workflow that organizes this information before the patient meets the healthcare professional.
Proposed Solution
The platform is planned to let a patient:
1. Review a privacy notice and provide consent for the intake session.
2. Enter basic information and answer guided medical-history questions.
3. Provide answers through text, with voice and multilingual interaction planned as later enhancements.
4. Upload previous prescriptions, laboratory reports, or discharge summaries.
5. Generate a structured draft of the patient's reported history and extracted document information.
6. Allow an authorized healthcare professional to review, correct, and approve the draft before it is used in care.
MediKiosk is intended to support clinical documentation—not replace a clinician, provide an autonomous diagnosis, or make treatment decisions.
Planned Features
- Guided patient intake: Capture presenting complaint, history of present illness, past medical and surgical history, medication and allergy history, family and personal history, and relevant review-of-systems information.
- Adaptive questions: Ask follow-up questions based on the patient's answers using defined clinical questionnaires and safety rules.
- Medical document processing: Use OCR to extract text from uploaded documents, with uncertain results clearly marked for human verification.
- Structured clinical summary: Produce a concise, editable draft from patient responses and document extracts.
- Doctor dashboard: Review patient intake sessions, inspect source information, amend summaries, and record review status.
- Patient and session management: Maintain organized records with controlled access.
- Privacy and security: Plan for authentication, role-based authorization, consent tracking, and audit logging.
- Accessibility: Design for simple navigation, readable prompts, and future multilingual and voice-assisted interaction.
- Future interoperability: Evaluate standards-based integration with approved hospital systems and ABDM/FHIR interfaces if access and requirements permit.
Planned Technology Stack
Layer	Planned technology
Frontend	React + Vite
Backend	Java 21 + Spring Boot
API	REST APIs
Database	PostgreSQL
Persistence	Spring Data JPA / Hibernate
Authentication and authorization	Spring Security
Validation	Jakarta Bean Validation
AI-assisted summarization	An LLM service with structured output and validation
Document digitization	OCR pipeline for uploaded medical documents
API testing	Postman
Development tools	IntelliJ IDEA, Git and GitHub


The stack may be adjusted as the project is implemented and tested.
High-Level Workflow
Patient
  │
  ▼
Consent and Guided Intake
  │
  ├──────────────► Upload Previous Medical Documents
  │                         │
  ▼                         ▼
Structured Answers       OCR and Extraction
  │                         │
  └────────────┬────────────┘
               ▼
      Draft Clinical Summary
               │
               ▼
      Healthcare Professional Review
               │
               ▼
      Corrected / Approved Record
Planned Core Modules
1. Patient Management — patient profile and basic demographic information.
2. Intake Session — complaint, guided questions, answers, and session status.
3. Medical Documents — uploaded file metadata, extracted text, and verification status.
4. Clinical Summary — generated draft, source references, corrections, and review status.
5. User and Access Management — patient and authorized healthcare-professional roles.
6. Audit History — traceable records of important actions and changes.
Development Roadmap
- [ ] Create the Spring Boot backend and configure PostgreSQL.
- [ ] Design entities, relationships, repositories, services, and REST controllers.
- [ ] Implement patient intake and validation APIs.
- [ ] Build the React patient intake interface.
- [ ] Add secure document upload and OCR extraction.
- [ ] Generate structured summaries and support clinician review.
- [ ] Build the doctor dashboard and review workflow.
- [ ] Add authentication, role-based access, consent records, and audit logs.
- [ ] Add multilingual/voice interaction as a later enhancement.
- [ ] Test the end-to-end workflow and document setup instructions.
Getting Started
The project setup and run instructions will be added once the backend and frontend scaffolding are in place. The intended local development setup uses Java 21, Maven, Node.js with npm, and PostgreSQL.
Privacy, Safety, and Test Data
- Use synthetic patient data for development, testing, screenshots, and demonstrations unless appropriate authorization and safeguards are in place for real health information.
- Patient information and uploaded documents must be protected through access controls and secure storage.
- OCR and AI outputs can be wrong. Extracted data and generated summaries must be verifiable against their source documents and reviewed by an authorized healthcare professional.
- Potential emergency symptoms require an appropriately designed escalation process; the application must not delay emergency care.
- This project is an educational software initiative and is not a certified medical device or a replacement for professional medical advice.
Success Criteria
The prototype will be evaluated by whether it can:
- Capture and validate a guided patient history.
- Organize uploaded medical information and expose uncertain extractions for review.
- Produce an editable, source-verifiable clinical summary.
- Let an authorized healthcare professional review and amend that summary.
- Enforce access permissions and preserve an audit history for important actions.
- Complete the core workflow reliably using synthetic test data.
Contributing
This is an educational project currently under development. Contributions, suggestions, and feedback on usability, accessibility, security, and implementation are welcome.
Disclaimer
MediKiosk is a student software project in development. Its planned features and integrations are subject to implementation, testing, and applicable permissions. It must not be used for real clinical decisions without appropriate clinical validation, security review, and authorization.
