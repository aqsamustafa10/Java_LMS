package com.school.management.controller;

import com.school.management.App;
import com.school.management.database.DatabaseManager;
import com.school.management.model.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeacherController {

    // Sidebar navigation
    @FXML private Button btnNavAttendance;
    @FXML private Button btnNavQuizzes;
    @FXML private StackPane contentPane;
    @FXML private Label lblTeacherName;

    // Panes
    @FXML private VBox paneAttendance;
    @FXML private VBox paneQuizzes;

    // --- Attendance portal fields ---
    @FXML private ComboBox<CourseAssignment> cmbAttendanceClassCourse;
    @FXML private DatePicker dpAttendanceDate;
    @FXML private TableView<StudentAttendanceRow> tblAttendance;
    @FXML private TableColumn<StudentAttendanceRow, String> colAttStudentId;
    @FXML private TableColumn<StudentAttendanceRow, String> colAttStudentName;
    @FXML private TableColumn<StudentAttendanceRow, CheckBox> colAttStatus;
    @FXML private Button btnSaveAttendance;

    // --- Quiz creation fields ---
    @FXML private ComboBox<CourseAssignment> cmbQuizClassCourse;
    @FXML private TextField txtQuizTitle;
    @FXML private TextField txtQuizTime;
    @FXML private ListView<String> lstQuizQuestions;
    @FXML private Button btnSaveQuiz;

    // MCQ Builder fields
    @FXML private TextField txtQuestionText;
    @FXML private TextField txtOptionA;
    @FXML private TextField txtOptionB;
    @FXML private TextField txtOptionC;
    @FXML private TextField txtOptionD;
    @FXML private ComboBox<String> cmbCorrectOption;
    @FXML private Label lblQuestionError;

    // State
    private final ObservableList<Question> tempQuestions = FXCollections.observableArrayList();
    private final ObservableList<CourseAssignment> teacherAssignments = FXCollections.observableArrayList();

    public static class StudentAttendanceRow {
        private final String studentId;
        private final String studentName;
        private final CheckBox presentCheckBox;

        public StudentAttendanceRow(String studentId, String studentName, boolean isPresent) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.presentCheckBox = new CheckBox();
            this.presentCheckBox.setSelected(isPresent);
        }

        public String getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public CheckBox getPresentCheckBox() {
            return presentCheckBox;
        }
    }

    @FXML
    public void initialize() {
        if (App.currentUser != null) {
            lblTeacherName.setText(App.currentUser.getName() + " (Teacher)");
        }

        // Set default date for attendance
        dpAttendanceDate.setValue(LocalDate.now());

        // Configure ComboBoxes formatting
        StringConverter<CourseAssignment> assignConverter = new StringConverter<CourseAssignment>() {
            @Override
            public String toString(CourseAssignment object) {
                if (object == null) return "";
                Course c = DatabaseManager.getCourseById(object.getCourseId());
                String courseName = c != null ? c.getCourseName() : object.getCourseId();
                return object.getClassName() + " - " + courseName;
            }
            @Override
            public CourseAssignment fromString(String string) {
                return null;
            }
        };

        cmbAttendanceClassCourse.setConverter(assignConverter);
        cmbQuizClassCourse.setConverter(assignConverter);

        // Populate MCQ Correct Option Combobox
        cmbCorrectOption.setItems(FXCollections.observableArrayList("Option A", "Option B", "Option C", "Option D"));

        // Initialize Attendance Table Columns
        colAttStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colAttStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colAttStatus.setCellValueFactory(new PropertyValueFactory<>("presentCheckBox"));

        // Load assignments
        loadTeacherAssignments();
    }

    private void loadTeacherAssignments() {
        teacherAssignments.clear();
        if (App.currentUser == null) return;

        String teacherId = App.currentUser.getId();
        for (CourseAssignment ca : DatabaseManager.getAssignments()) {
            if (ca.getTeacherId().equalsIgnoreCase(teacherId)) {
                teacherAssignments.add(ca);
            }
        }

        cmbAttendanceClassCourse.setItems(teacherAssignments);
        cmbQuizClassCourse.setItems(teacherAssignments);
    }

    @FXML
    void switchTab(ActionEvent event) {
        Button src = (Button) event.getSource();
        btnNavAttendance.getStyleClass().remove("btn-nav-active");
        btnNavQuizzes.getStyleClass().remove("btn-nav-active");

        paneAttendance.setVisible(false);
        paneQuizzes.setVisible(false);

        if (src == btnNavAttendance) {
            btnNavAttendance.getStyleClass().add("btn-nav-active");
            paneAttendance.setVisible(true);
        } else if (src == btnNavQuizzes) {
            btnNavQuizzes.getStyleClass().add("btn-nav-active");
            paneQuizzes.setVisible(true);
        }
    }

    // --- Attendance Logic ---
    @FXML
    void handleClassCourseSelected() {
        loadStudentRoster();
    }

    @FXML
    void handleDateChanged() {
        loadStudentRoster();
    }

    private void loadStudentRoster() {
        CourseAssignment ca = cmbAttendanceClassCourse.getValue();
        if (ca == null) {
            tblAttendance.setItems(FXCollections.emptyObservableList());
            return;
        }

        LocalDate date = dpAttendanceDate.getValue();
        if (date == null) return;
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Fetch students in this class
        ObservableList<StudentAttendanceRow> rows = FXCollections.observableArrayList();
        
        // Find existing attendance records for this class/course/date to pre-populate checkbox
        List<Attendance> existingRecords = DatabaseManager.getAttendanceRecords();

        for (User u : DatabaseManager.getUsers()) {
            if (u.getRole() == User.Role.STUDENT && u.getClassName() != null && u.getClassName().equalsIgnoreCase(ca.getClassName())
                && u.getTeacherId() != null && u.getTeacherId().equalsIgnoreCase(App.currentUser.getId())
                && u.getCourseId() != null && u.getCourseId().equalsIgnoreCase(ca.getCourseId())) {
                
                boolean wasPresent = false;
                for (Attendance att : existingRecords) {
                    if (att.getStudentId().equalsIgnoreCase(u.getId()) &&
                        att.getClassName().equalsIgnoreCase(ca.getClassName()) &&
                        att.getCourseId().equalsIgnoreCase(ca.getCourseId()) &&
                        att.getDate().equals(formattedDate)) {
                        
                        wasPresent = att.getStatus().equalsIgnoreCase("PRESENT");
                        break;
                    }
                }
                rows.add(new StudentAttendanceRow(u.getId(), u.getName(), wasPresent));
            }
        }

        tblAttendance.setItems(rows);
    }

    @FXML
    void handleSaveAttendance() {
        CourseAssignment ca = cmbAttendanceClassCourse.getValue();
        LocalDate date = dpAttendanceDate.getValue();

        if (ca == null || date == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select Class/Course and Date.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        ObservableList<StudentAttendanceRow> rows = tblAttendance.getItems();

        if (rows.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No students in this class to mark attendance.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        for (StudentAttendanceRow row : rows) {
            String status = row.getPresentCheckBox().isSelected() ? "PRESENT" : "ABSENT";
            String id = UUID.randomUUID().toString();
            Attendance att = new Attendance(id, formattedDate, ca.getClassName(), ca.getCourseId(), row.getStudentId(), status);
            DatabaseManager.addAttendance(att);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance saved successfully for date: " + formattedDate, ButtonType.OK);
        alert.showAndWait();
    }

    // --- Quiz Builder Logic ---
    @FXML
    void handleAddQuestion() {
        String questionText = txtQuestionText.getText().trim();
        String optA = txtOptionA.getText().trim();
        String optB = txtOptionB.getText().trim();
        String optC = txtOptionC.getText().trim();
        String optD = txtOptionD.getText().trim();
        String correctStr = cmbCorrectOption.getValue();

        if (questionText.isEmpty() || optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty() || correctStr == null) {
            lblQuestionError.setText("All question fields and the correct option selection are required.");
            lblQuestionError.setVisible(true);
            return;
        }

        lblQuestionError.setVisible(false);

        List<String> options = new ArrayList<>();
        options.add(optA);
        options.add(optB);
        options.add(optC);
        options.add(optD);

        int correctIndex = cmbCorrectOption.getSelectionModel().getSelectedIndex(); // 0: A, 1: B, 2: C, 3: D
        Question question = new Question(questionText, options, correctIndex);

        tempQuestions.add(question);
        lstQuizQuestions.getItems().add(tempQuestions.size() + ". " + questionText);

        // Clear only question fields, keep quiz config fields
        handleClearQuestionFields();
    }

    @FXML
    void handleClearQuestionFields() {
        txtQuestionText.clear();
        txtOptionA.clear();
        txtOptionB.clear();
        txtOptionC.clear();
        txtOptionD.clear();
        cmbCorrectOption.setValue(null);
        lblQuestionError.setVisible(false);
    }

    @FXML
    void handleSaveQuiz() {
        CourseAssignment ca = cmbQuizClassCourse.getValue();
        String title = txtQuizTitle.getText().trim();
        String timeStr = txtQuizTime.getText().trim();

        if (ca == null || title.isEmpty() || timeStr.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please set Class/Course, Title, and Time Limit.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeStr);
            if (timeLimit <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Time Limit must be a positive integer.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (tempQuestions.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please add at least one question to the quiz.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        String quizId = UUID.randomUUID().toString();
        Quiz quiz = new Quiz(quizId, title, ca.getClassName(), ca.getCourseId(), App.currentUser.getId(), new ArrayList<>(tempQuestions), timeLimit);
        DatabaseManager.addQuiz(quiz);

        // Clear everything
        cmbQuizClassCourse.setValue(null);
        txtQuizTitle.clear();
        txtQuizTime.clear();
        tempQuestions.clear();
        lstQuizQuestions.getItems().clear();
        handleClearQuestionFields();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Quiz published successfully for " + ca.getClassName(), ButtonType.OK);
        alert.showAndWait();
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
