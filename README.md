# JavaFX School Management System

## 📚 Overview
A **feature‑rich desktop application** built with **JavaFX** for managing school operations. It helps administrators and teachers handle student records, attendance, courses, and more through an intuitive graphical interface.

---

## ✨ Features
- **Student Management** – Add, edit, delete, and view student details.
- **Attendance Tracking** – Record daily attendance and generate reports.
- **Course Catalog** – Manage courses, assign teachers, and link students.
- **Responsive UI** – Clean, modern JavaFX interface with validation and error handling.
- **Persistence** – Data stored using an embedded H2 database (or any JDBC‑compatible DB).
- **Export/Import** – CSV import/export for bulk operations.
- **Search & Filter** – Quick lookup of students, courses, and attendance records.

---

## 🛠️ Technologies
| Component | Version |
|-----------|---------|
| Java | 17 (LTS) |
| JavaFX | 21 |
| Maven | 3.9.0 |
| H2 Database | 2.2.224 |
| JUnit | 5.10 |
| Lombok *(optional)* | 1.18.30 |

---

## 📂 Project Structure
```
JavaFX_SchoolManagment/
├─ src/main/java/com/school/management/
│  ├─ controller/      # UI controllers (e.g., StudentController)
│  ├─ model/           # Domain models (Student, Course, Attendance)
│  ├─ repository/      # DAO layer for DB interaction
│  └─ util/            # Helper utilities (validation, dialogs)
├─ src/main/resources/  # FXML files, CSS, icons
├─ pom.xml               # Maven build configuration
└─ run.bat               # Windows script to launch the app
```

---

## 🚀 Getting Started
### Prerequisites
1. **JDK 17** or newer installed and `JAVA_HOME` set.
2. **Maven** (bundled with most IDEs; you can also use the `mvn` wrapper).
3. **JavaFX SDK** – the Maven `javafx-controls`, `javafx-fxml` dependencies are already declared in `pom.xml`.

### Installation
```bash
# Clone the repository
git clone https://github.com/yourusername/JavaFX_SchoolManagment.git
cd JavaFX_SchoolManagment
```

### Build & Run
```bash
# Build the project (downloads dependencies automatically)
mvn clean package

# Run the application (Windows shortcut provided)
./run.bat
```
Or directly via Maven:
```bash
mvn javafx:run
```

---

## 📋 Usage Guide
1. **Launch** – The main window displays a navigation pane (Students, Courses, Attendance).
2. **Add Student** – Click *Add*, fill the form, and press *Save*.
3. **Record Attendance** – Choose a date, mark present/absent, and submit.
4. **Manage Courses** – Create new courses and assign them to students.
5. **Search** – Use the search bar at the top of each view to filter records.

---

## 🧪 Testing
Run the unit test suite with:
```bash
mvn test
```
All tests are located under `src/test/java`.

---

## 🤝 Contributing
Contributions are welcome! Please follow these steps:
1. **Fork** the repository.
2. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Commit** your changes with clear messages.
4. **Push** to your fork and open a **Pull Request**.
5. Ensure the code passes existing tests and add new tests for added functionality.

---

## 📄 License
This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

