package com.school.management.controller;

import com.school.management.App;
import com.school.management.database.DatabaseManager;
import com.school.management.model.Course;
import com.school.management.model.CourseAssignment;
import com.school.management.model.SchoolClass;
import com.school.management.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.UUID;

public class AdminController {

    // Sidebar navigation buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavTeachers;
    @FXML private Button btnNavStudents;
    @FXML private Button btnNavCourses;
    @FXML private Button btnNavAssign;

    @FXML private StackPane contentPane;

    // View panels
    @FXML private VBox paneDashboard;
    @FXML private VBox paneTeachers;
    @FXML private VBox paneStudents;
    @FXML private VBox paneCourses;
    @FXML private VBox paneAssign;

    // Overview Stats
    @FXML private Label lblStatTeachers;
    @FXML private Label lblStatStudents;
    @FXML private Label lblStatCourses;
    @FXML private Label lblStatAssignments;
    @FXML private TableView<CourseAssignment> tblDashboardAssignments;
    @FXML private TableColumn<CourseAssignment, String> colDashAssignTeacher;
    @FXML private TableColumn<CourseAssignment, String> colDashAssignClass;
    @FXML private TableColumn<CourseAssignment, String> colDashAssignCourse;

    // Teachers Management
    @FXML private TableView<User> tblTeachers;
    @FXML private TableColumn<User, String> colTeacherId;
    @FXML private TableColumn<User, String> colTeacherName;
    @FXML private TableColumn<User, String> colTeacherPassword;
    @FXML private TextField txtTeacherName;
    @FXML private TextField txtTeacherPassword;
    @FXML private Label lblTeacherError;

    // Students Management
    @FXML private TableView<User> tblStudents;
    @FXML private TableColumn<User, String> colStudentId;
    @FXML private TableColumn<User, String> colStudentName;
    @FXML private TableColumn<User, String> colStudentClass;
    @FXML private TableColumn<User, String> colStudentPassword;
    @FXML private TextField txtStudentName;
    @FXML private ComboBox<SchoolClass> cmbStudentClass;
    @FXML private ComboBox<User> cmbStudentTeacher;
    @FXML private ComboBox<Course> cmbStudentCourse;
    @FXML private TextField txtStudentPassword;
    @FXML private Label lblStudentError;

    // Courses & Classes Management
    @FXML private TableView<Course> tblCourses;
    @FXML private TableColumn<Course, String> colCourseId;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TextField txtCourseName;
    @FXML private Label lblCourseError;

    @FXML private TableView<SchoolClass> tblClasses;
    @FXML private TableColumn<SchoolClass, String> colClassId;
    @FXML private TableColumn<SchoolClass, String> colClassName;
    @FXML private TextField txtClassName;
    @FXML private Label lblClassError;

    // Assignments Management
    @FXML private TableView<CourseAssignment> tblAssignments;
    @FXML private TableColumn<CourseAssignment, String> colAssignTeacher;
    @FXML private TableColumn<CourseAssignment, String> colAssignClass;
    @FXML private TableColumn<CourseAssignment, String> colAssignCourse;
    @FXML private ComboBox<User> cmbAssignTeacher;
    @FXML private ComboBox<SchoolClass> cmbAssignClass;
    @FXML private ComboBox<Course> cmbAssignCourse;
    @FXML private Label lblAssignError;

    @FXML
    public void initialize() {
        // Initialize columns
        initTableColumns();
        
        // Setup converter mappings for combo boxes
        initComboBoxConverters();

        // Load data
        refreshData();
    }

    private void initTableColumns() {
        // Dashboard Assignments Table
        colDashAssignTeacher.setCellValueFactory(cellData -> {
            User t = DatabaseManager.getUserById(cellData.getValue().getTeacherId());
            return new SimpleStringProperty(t != null ? t.getName() + " (" + t.getId() + ")" : cellData.getValue().getTeacherId());
        });
        colDashAssignClass.setCellValueFactory(new PropertyValueFactory<>("className"));
        colDashAssignCourse.setCellValueFactory(cellData -> {
            Course c = DatabaseManager.getCourseById(cellData.getValue().getCourseId());
            return new SimpleStringProperty(c != null ? c.getCourseName() : cellData.getValue().getCourseId());
        });

        // Teachers Table
        colTeacherId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTeacherName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeacherPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Students Table
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStudentClass.setCellValueFactory(new PropertyValueFactory<>("className"));
        colStudentPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Courses Table
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        // Classes Table
        colClassId.setCellValueFactory(new PropertyValueFactory<>("classId"));
        colClassName.setCellValueFactory(new PropertyValueFactory<>("className"));

        // Assignments Table
        colAssignTeacher.setCellValueFactory(cellData -> {
            User t = DatabaseManager.getUserById(cellData.getValue().getTeacherId());
            return new SimpleStringProperty(t != null ? t.getName() + " (" + t.getId() + ")" : cellData.getValue().getTeacherId());
        });
        colAssignClass.setCellValueFactory(new PropertyValueFactory<>("className"));
        colAssignCourse.setCellValueFactory(cellData -> {
            Course c = DatabaseManager.getCourseById(cellData.getValue().getCourseId());
            return new SimpleStringProperty(c != null ? c.getCourseName() : cellData.getValue().getCourseId());
        });
    }

    private void initComboBoxConverters() {
        // Class mapping
        StringConverter<SchoolClass> classConverter = new StringConverter<SchoolClass>() {
            @Override
            public String toString(SchoolClass object) {
                return object == null ? "" : object.getClassName() + " (" + object.getClassId() + ")";
            }
            @Override
            public SchoolClass fromString(String string) { return null; }
        };
        cmbStudentClass.setConverter(classConverter);
        cmbAssignClass.setConverter(classConverter);

        // Teacher mapping
        StringConverter<User> teacherConverter = new StringConverter<User>() {
            @Override
            public String toString(User object) {
                return object == null ? "" : object.getName() + " (" + object.getId() + ")";
            }
            @Override
            public User fromString(String string) { return null; }
        };
        cmbStudentTeacher.setConverter(teacherConverter);
        cmbAssignTeacher.setConverter(teacherConverter);

        // Course mapping
        StringConverter<Course> courseConverter = new StringConverter<Course>() {
            @Override
            public String toString(Course object) {
                return object == null ? "" : object.getCourseName() + " (" + object.getCourseId() + ")";
            }
            @Override
            public Course fromString(String string) { return null; }
        };
        cmbStudentCourse.setConverter(courseConverter);
        cmbAssignCourse.setConverter(courseConverter);
    }

    private void refreshData() {
        // Load all users from DatabaseManager
        ObservableList<User> teachersList = FXCollections.observableArrayList();
        ObservableList<User> studentsList = FXCollections.observableArrayList();

        for (User u : DatabaseManager.getUsers()) {
            if (u.getRole() == User.Role.TEACHER) {
                teachersList.add(u);
            } else if (u.getRole() == User.Role.STUDENT) {
                studentsList.add(u);
            }
        }

        tblTeachers.setItems(teachersList);
        tblStudents.setItems(studentsList);

        ObservableList<Course> coursesList = FXCollections.observableArrayList(DatabaseManager.getCourses());
        tblCourses.setItems(coursesList);

        ObservableList<SchoolClass> classesList = FXCollections.observableArrayList(DatabaseManager.getClasses());
        tblClasses.setItems(classesList);

        ObservableList<CourseAssignment> assignmentsList = FXCollections.observableArrayList(DatabaseManager.getAssignments());
        tblAssignments.setItems(assignmentsList);
        tblDashboardAssignments.setItems(assignmentsList);

        // Update statistics
        lblStatTeachers.setText(String.valueOf(teachersList.size()));
        lblStatStudents.setText(String.valueOf(studentsList.size()));
        lblStatCourses.setText(String.valueOf(coursesList.size()));
        lblStatAssignments.setText(String.valueOf(assignmentsList.size()));

        // Populate dropdowns
        cmbStudentClass.setItems(classesList);
        cmbStudentTeacher.setItems(teachersList);
        cmbStudentCourse.setItems(coursesList);

        cmbAssignTeacher.setItems(teachersList);
        cmbAssignClass.setItems(classesList);
        cmbAssignCourse.setItems(coursesList);
    }

    @FXML
    void switchTab(ActionEvent event) {
        Button src = (Button) event.getSource();
        
        // Remove active class from all
        btnNavDashboard.getStyleClass().remove("btn-nav-active");
        btnNavTeachers.getStyleClass().remove("btn-nav-active");
        btnNavStudents.getStyleClass().remove("btn-nav-active");
        btnNavCourses.getStyleClass().remove("btn-nav-active");
        btnNavAssign.getStyleClass().remove("btn-nav-active");

        // Hide all panes
        paneDashboard.setVisible(false);
        paneTeachers.setVisible(false);
        paneStudents.setVisible(false);
        paneCourses.setVisible(false);
        paneAssign.setVisible(false);

        // Set clicked active and show pane
        if (src == btnNavDashboard) {
            btnNavDashboard.getStyleClass().add("btn-nav-active");
            paneDashboard.setVisible(true);
        } else if (src == btnNavTeachers) {
            btnNavTeachers.getStyleClass().add("btn-nav-active");
            paneTeachers.setVisible(true);
        } else if (src == btnNavStudents) {
            btnNavStudents.getStyleClass().add("btn-nav-active");
            paneStudents.setVisible(true);
        } else if (src == btnNavCourses) {
            btnNavCourses.getStyleClass().add("btn-nav-active");
            paneCourses.setVisible(true);
        } else if (src == btnNavAssign) {
            btnNavAssign.getStyleClass().add("btn-nav-active");
            paneAssign.setVisible(true);
        }
    }

    // --- Teachers Panel ---
    @FXML
    void handleSaveTeacher() {
        String name = txtTeacherName.getText().trim();
        String password = txtTeacherPassword.getText();

        if (name.isEmpty() || password.isEmpty()) {
            showLabelError(lblTeacherError, "All fields are required.");
            return;
        }

        // Auto generate sequential teacher ID
        String teacherId = DatabaseManager.generateNextTeacherId();

        User teacher = new User(teacherId, name, "", password, User.Role.TEACHER);
        DatabaseManager.addUser(teacher);

        txtTeacherName.clear();
        txtTeacherPassword.clear();
        lblTeacherError.setVisible(false);

        refreshData();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Teacher registered successfully!\nGenerated Teacher ID: " + teacherId, ButtonType.OK);
        alert.setHeaderText("Success Details");
        alert.showAndWait();
    }

    // --- Students Panel ---
    @FXML
    void handleSaveStudent() {
        String name = txtStudentName.getText().trim();
        SchoolClass selectedClass = cmbStudentClass.getValue();
        User selectedTeacher = cmbStudentTeacher.getValue();
        Course selectedCourse = cmbStudentCourse.getValue();
        String password = txtStudentPassword.getText();

        if (name.isEmpty() || selectedClass == null || selectedTeacher == null || selectedCourse == null || password.isEmpty()) {
            showLabelError(lblStudentError, "All fields (including dropdown selections) are required.");
            return;
        }

        // Auto generate sequential student ID
        String studentId = DatabaseManager.generateNextStudentId();

        User student = new User(studentId, name, password, User.Role.STUDENT, selectedClass.getClassName(), selectedTeacher.getId(), selectedCourse.getCourseId());
        DatabaseManager.addUser(student);

        txtStudentName.clear();
        cmbStudentClass.setValue(null);
        cmbStudentTeacher.setValue(null);
        cmbStudentCourse.setValue(null);
        txtStudentPassword.clear();
        lblStudentError.setVisible(false);

        refreshData();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student registered successfully!\nGenerated Student ID: " + studentId, ButtonType.OK);
        alert.setHeaderText("Success Details");
        alert.showAndWait();
    }

    // --- Courses & Classes Panel (Add Course) ---
    @FXML
    void handleSaveCourse() {
        String name = txtCourseName.getText().trim();

        if (name.isEmpty()) {
            showLabelError(lblCourseError, "Course Name is required.");
            return;
        }

        // Auto generate course ID
        String courseId = DatabaseManager.generateNextCourseId();

        Course course = new Course(courseId, name);
        DatabaseManager.addCourse(course);

        txtCourseName.clear();
        lblCourseError.setVisible(false);

        refreshData();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Course added successfully!\nGenerated Course ID: " + courseId, ButtonType.OK);
        alert.setHeaderText("Success Details");
        alert.showAndWait();
    }

    // --- Courses & Classes Panel (Add Class) ---
    @FXML
    void handleSaveClass() {
        String className = txtClassName.getText().trim();

        if (className.isEmpty()) {
            showLabelError(lblClassError, "Class Name is required.");
            return;
        }

        // Check if class with same name already exists
        if (DatabaseManager.getClassByName(className) != null) {
            showLabelError(lblClassError, "Class with this name already exists.");
            return;
        }

        // Auto generate class ID
        String classId = DatabaseManager.generateNextClassId();

        SchoolClass sc = new SchoolClass(classId, className);
        DatabaseManager.addClass(sc);

        txtClassName.clear();
        lblClassError.setVisible(false);

        refreshData();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Class added successfully!\nGenerated Class ID: " + classId, ButtonType.OK);
        alert.setHeaderText("Success Details");
        alert.showAndWait();
    }

    // --- Assignments Panel ---
    @FXML
    void handleSaveAssignment() {
        User teacher = cmbAssignTeacher.getValue();
        SchoolClass sc = cmbAssignClass.getValue();
        Course course = cmbAssignCourse.getValue();

        if (teacher == null || sc == null || course == null) {
            showLabelError(lblAssignError, "All fields are required.");
            return;
        }

        String className = sc.getClassName();

        // Check if identical assignment already exists
        for (CourseAssignment ca : DatabaseManager.getAssignments()) {
            if (ca.getTeacherId().equalsIgnoreCase(teacher.getId()) &&
                ca.getClassName().equalsIgnoreCase(className) &&
                ca.getCourseId().equalsIgnoreCase(course.getCourseId())) {
                showLabelError(lblAssignError, "This class & course is already allocated to this teacher.");
                return;
            }
        }

        String assignId = UUID.randomUUID().toString();
        CourseAssignment ca = new CourseAssignment(assignId, teacher.getId(), className, course.getCourseId());
        DatabaseManager.addAssignment(ca);

        cmbAssignTeacher.setValue(null);
        cmbAssignClass.setValue(null);
        cmbAssignCourse.setValue(null);
        lblAssignError.setVisible(false);

        refreshData();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Class and Course assigned to Teacher successfully!", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    void handleRemoveAssignment() {
        CourseAssignment selected = tblAssignments.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseManager.removeAssignment(selected.getAssignmentId());
            refreshData();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an assignment to remove.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void showLabelError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        App.currentUser = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/school/management/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
