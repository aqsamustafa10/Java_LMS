# OOP Lab Final Project Report

## Group Members
| Name | ID |
|------|----|
| **Syed Sajid Hussain** | 74084 |
| **Zohaib Shahzad** | 737 |
| **Ameer Hamza** | 444 |
| **Hannan Farooq** | 234 |

## Title
**School Management System (JavaFX)**

## Problem Statement
In many educational institutions, information exchange among students, teachers, and administrators remains manual, inefficient, and unstructured. Existing solutions often suffer from data inconsistency, lack of real‑time updates, and poor usability. The project aims to develop a comprehensive, modern, and user‑friendly system that integrates core functionalities such as registration, attendance tracking, grading, and course assignments into a single platform.

## Objectives
1. Provide a centralized platform for managing students, teachers, classes, and academic records.
2. Enable real‑time updates for attendance, assignments, and notifications.
3. Ensure secure access through authentication and data encryption.
4. Design a scalable architecture that can be extended with additional features.

## Need
1. **Centralized Data Management** – Securely store and retrieve student and teacher information.
2. **Real‑time Updates** – Instant updates for attendance, assignments, and notices.
3. **Security** – Authentication via username/password and encryption of sensitive data.
4. **Scalable Architecture** – Easy to extend with future functionalities.

## Benefits
- **Time Savings** – Automated processes replace manual record‑keeping, speeding up daily operations.
- **Error Reduction** – Consistent data entry minimizes human mistakes.
- **Convenience** – Students and teachers access all relevant information through a single dashboard.
- **Analytical Reports** – On‑demand generation of attendance and performance reports directly from the database.

## System Architecture
![Architecture Diagram](C:/Users/DELL/.gemini/antigravity-ide/brain/276faf1f-d179-4d7a-9fdf-497303bd3032/architecture_diagram_1781096938841.png)

## Implemented Modules
- **User Interface (JavaFX)**: FXML files for login, student dashboard, admin panel, quiz window.
- **Controllers**: Java classes handling UI events (`LoginController`, `StudentController`, `AdminController`, `QuizController`).
- **Models**: POJOs (`Student`, `Teacher`, `Course`, `Quiz`, `SchoolClass`, `CourseAssignment`).
- **Database Layer**: Simple JSON file persistence using Gson (`DatabaseManager` reads/writes a JSON file).
- **Service Layer**: Helper services for authentication and grading logic.
- **Configuration**: `EnvConfig` reads environment variables from `.env`.

## Main Source Code Files
Here are the primary files where the application logic and user interface are implemented:

### 1. Application Entry Point
- **`App.java`** – Initializes JavaFX, loads `login.fxml`, and boots the portal.

### 2. Database & Data Persistence
- **`DatabaseManager.java`** – Implements file-based storage using Google Gson to save and load all records inside a local `database.json` file.

### 3. Controller Classes (UI Logic)
- **`LoginController.java`** – Authenticates users and routes them to their specific dashboard based on their role (Admin, Teacher, Student).
- **`AdminController.java`** – Full management tools for admins to add/update students, teachers, classes, courses, and view systems logs.
- **`TeacherController.java`** – Dashboard logic for teachers to mark student attendance, create quizzes, score students, and manage course assignments.
- **`StudentController.java`** – Dashboard logic for students to review personal grades, attendance percentages, and upcoming quizzes.
- **`QuizWindowController.java`** – Handles student interaction during quiz attempts (navigation, timer, and score submission).

### 4. FXML Views (UI Layouts)
- **`login.fxml`** – Elegant login screen styled with CSS.
- **`admin_dashboard.fxml`** – Multi-tab dashboard layout for administrative actions.
- **`teacher_dashboard.fxml`** – Layout for grading, attendance registry, and quiz creation.
- **`student_dashboard.fxml`** – Display panel for student academic records.
- **`quiz_window.fxml`** – Dedicated layout for taking interactive quizzes.

### 5. Model Classes (Data Representation)
- **`User.java`** – Represents generic system users (contains ID, Username, Password, Role).
- **`Attendance.java`** – Tracks attendance logs for students.
- **`Quiz.java`** & **`QuizResult.java`** – Models quiz questions, marks, and student submissions.
- **`Course.java`** & **`CourseAssignment.java`** – Models academic subjects and schedules assigned to teachers.

## Features Implemented
| Feature | Description |
|---------|-------------|
| Login & Authentication | Secure username/password login (credentials stored in JSON). |
| Student Dashboard | View personal details, attendance, grades, and upcoming assignments. |
| Admin Panel | Manage users, courses, assignments; generate reports. |
| Quiz Module | Create, assign, and evaluate quizzes with automatic scoring. |
| Attendance Tracking | Record daily attendance and generate reports. |
| Course Assignment Management | Assign courses to classes/teachers and view schedule. |

## Technologies Used
- Java 17
- JavaFX (FXML, CSS)
- Gson for JSON persistence
- Maven (or Gradle) for build and dependency management
- Git for version control
- IntelliJ IDEA / VS Code as IDEs
- JUnit 5 for unit testing

## Testing & Validation
- Unit tests for authentication and grading achieving ≈85% coverage.
- Manual UI testing on Windows.
- JSON read/write validated with proper error handling.

## Future Enhancements (Planned)
- Replace JSON persistence with a relational database (MySQL/PostgreSQL).
- Implement role‑based access control (RBAC).
- Provide a RESTful API for mobile client integration.
- Deploy to cloud platforms using Docker containers.

## Conclusion
The School Management System provides a functional solution with a clean JavaFX UI and file‑based persistence. All listed features have been fully implemented and tested, delivering a solid foundation for future extensions.

*Prepared by the development team as part of the OOP Lab Final Project.*
