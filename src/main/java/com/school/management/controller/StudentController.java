package com.school.management.controller;

import com.school.management.App;
import com.school.management.database.DatabaseManager;
import com.school.management.model.*;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentController {

    // Sidebar navigation
    @FXML private Label lblSidebarStudentId;
    @FXML private Button btnNavPortal;
    @FXML private Button btnNavAttendance;
    @FXML private Button btnNavAssessments;
    @FXML private StackPane contentPane;

    // Panes
    @FXML private VBox panePortal;
    @FXML private VBox paneAttendance;
    @FXML private VBox paneAssessments;

    // --- Profile Portal Fields ---
    @FXML private Label lblProfileName;
    @FXML private Label lblProfileId;
    @FXML private Label lblProfileClass;
    @FXML private Label lblPortalAttPercent;
    @FXML private Label lblPortalQuizzesTaken;
    @FXML private Label lblPortalQuizzesPending;

    // --- Attendance History Fields ---
    @FXML private TableView<AttendanceRow> tblAttendanceRecords;
    @FXML private TableColumn<AttendanceRow, String> colStudentAttDate;
    @FXML private TableColumn<AttendanceRow, String> colStudentAttCourse;
    @FXML private TableColumn<AttendanceRow, String> colStudentAttStatus;
    
    @FXML private Label lblStatTotalLectures;
    @FXML private Label lblStatPresentDays;
    @FXML private Label lblStatAbsentDays;

    // --- Quizzes Fields ---
    @FXML private TableView<PendingQuizRow> tblPendingQuizzes;
    @FXML private TableColumn<PendingQuizRow, String> colPendQuizTitle;
    @FXML private TableColumn<PendingQuizRow, String> colPendQuizCourse;
    @FXML private TableColumn<PendingQuizRow, Integer> colPendQuizQuestions;
    @FXML private TableColumn<PendingQuizRow, String> colPendQuizTime;
    @FXML private TableColumn<PendingQuizRow, PendingQuizRow> colPendQuizAction;

    @FXML private TableView<QuizResult> tblCompletedQuizzes;
    @FXML private TableColumn<QuizResult, String> colCompQuizTitle;
    @FXML private TableColumn<QuizResult, String> colCompQuizScore;
    @FXML private TableColumn<QuizResult, String> colCompQuizDate;

    // Row definitions
    public static class AttendanceRow {
        private final String date;
        private final String courseName;
        private final String status;

        public AttendanceRow(String date, String courseName, String status) {
            this.date = date;
            this.courseName = courseName;
            this.status = status;
        }

        public String getDate() { return date; }
        public String getCourseName() { return courseName; }
        public String getStatus() { return status; }
    }

    public static class PendingQuizRow {
        private final Quiz quiz;
        private final StudentController parentController;

        public PendingQuizRow(Quiz quiz, StudentController parentController) {
            this.quiz = quiz;
            this.parentController = parentController;
        }

        public Quiz getQuiz() {
            return quiz;
        }

        public String getTitle() {
            return quiz.getTitle();
        }

        public String getCourseName() {
            Course c = DatabaseManager.getCourseById(quiz.getCourseId());
            return c != null ? c.getCourseName() : quiz.getCourseId();
        }

        public int getQuestionCount() {
            return quiz.getQuestions().size();
        }

        public String getDuration() {
            return quiz.getTimeLimitMinutes() + " mins";
        }
    }

    @FXML
    public void initialize() {
        if (App.currentUser != null) {
            lblSidebarStudentId.setText(App.currentUser.getId() + " (Student)");
            lblProfileName.setText(App.currentUser.getName());
            lblProfileId.setText(App.currentUser.getId());
            lblProfileClass.setText(App.currentUser.getClassName());
        }

        initTableColumns();
        refreshData();
    }

    private void initTableColumns() {
        // Attendance Table
        colStudentAttDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStudentAttCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colStudentAttStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Pending Quizzes
        colPendQuizTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPendQuizCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colPendQuizQuestions.setCellValueFactory(new PropertyValueFactory<>("questionCount"));
        colPendQuizTime.setCellValueFactory(new PropertyValueFactory<>("duration"));

        // Custom action button in TableColumn for starting a quiz
        colPendQuizAction.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colPendQuizAction.setCellFactory(column -> new TableCell<PendingQuizRow, PendingQuizRow>() {
            private final Button btnTake = new Button("Take Quiz");

            {
                btnTake.getStyleClass().add("btn-secondary");
                btnTake.setStyle("-fx-padding: 5px 12px; -fx-font-size: 11px;");
            }

            @Override
            protected void updateItem(PendingQuizRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnTake);
                    btnTake.setOnAction(event -> {
                        item.parentController.startSecureQuiz(item.getQuiz());
                    });
                }
            }
        });

        // Completed Quizzes
        colCompQuizTitle.setCellValueFactory(new PropertyValueFactory<>("quizTitle"));
        colCompQuizScore.setCellValueFactory(cellData -> {
            QuizResult qr = cellData.getValue();
            return new SimpleStringProperty(qr.getScore() + " / " + qr.getTotalQuestions());
        });
        colCompQuizDate.setCellValueFactory(new PropertyValueFactory<>("dateTaken"));
    }

    public void refreshData() {
        if (App.currentUser == null) return;
        String studentId = App.currentUser.getId();
        String className = App.currentUser.getClassName();

        // 1. Calculate Attendance
        List<Attendance> allAttendance = DatabaseManager.getAttendanceRecords();
        List<AttendanceRow> studentAttRows = new ArrayList<>();
        int totalLectures = 0;
        int presentCount = 0;

        for (Attendance att : allAttendance) {
            if (att.getStudentId().equalsIgnoreCase(studentId)) {
                totalLectures++;
                boolean isPresent = att.getStatus().equalsIgnoreCase("PRESENT");
                if (isPresent) {
                    presentCount++;
                }

                Course c = DatabaseManager.getCourseById(att.getCourseId());
                String cName = c != null ? c.getCourseName() : att.getCourseId();
                studentAttRows.add(new AttendanceRow(att.getDate(), cName, att.getStatus()));
            }
        }

        tblAttendanceRecords.setItems(FXCollections.observableArrayList(studentAttRows));
        
        lblStatTotalLectures.setText(String.valueOf(totalLectures));
        lblStatPresentDays.setText(String.valueOf(presentCount));
        lblStatAbsentDays.setText(String.valueOf(totalLectures - presentCount));

        double attPercent = totalLectures == 0 ? 100.0 : ((double) presentCount / totalLectures) * 100.0;
        String attPercentStr = String.format("%.1f%%", attPercent);
        lblPortalAttPercent.setText(attPercentStr);

        // 2. Fetch Quizzes (Pending vs Completed)
        List<Quiz> allQuizzes = DatabaseManager.getQuizzes();
        List<QuizResult> allResults = DatabaseManager.getQuizResults();

        List<PendingQuizRow> pending = new ArrayList<>();
        List<QuizResult> completed = new ArrayList<>();

        for (QuizResult qr : allResults) {
            if (qr.getStudentId().equalsIgnoreCase(studentId)) {
                completed.add(qr);
            }
        }

        for (Quiz q : allQuizzes) {
            // Check if matches class
            if (q.getClassName() != null && q.getClassName().equalsIgnoreCase(className)) {
                // Check if already taken
                boolean alreadyTaken = false;
                for (QuizResult qr : completed) {
                    if (qr.getQuizId().equalsIgnoreCase(q.getQuizId())) {
                        alreadyTaken = true;
                        break;
                    }
                }
                if (!alreadyTaken) {
                    pending.add(new PendingQuizRow(q, this));
                }
            }
        }

        tblPendingQuizzes.setItems(FXCollections.observableArrayList(pending));
        tblCompletedQuizzes.setItems(FXCollections.observableArrayList(completed));

        lblPortalQuizzesTaken.setText(String.valueOf(completed.size()));
        lblPortalQuizzesPending.setText(String.valueOf(pending.size()));
    }

    @FXML
    void switchTab(ActionEvent event) {
        Button src = (Button) event.getSource();
        btnNavPortal.getStyleClass().remove("btn-nav-active");
        btnNavAttendance.getStyleClass().remove("btn-nav-active");
        btnNavAssessments.getStyleClass().remove("btn-nav-active");

        panePortal.setVisible(false);
        paneAttendance.setVisible(false);
        paneAssessments.setVisible(false);

        if (src == btnNavPortal) {
            btnNavPortal.getStyleClass().add("btn-nav-active");
            panePortal.setVisible(true);
        } else if (src == btnNavAttendance) {
            btnNavAttendance.getStyleClass().add("btn-nav-active");
            paneAttendance.setVisible(true);
        } else if (src == btnNavAssessments) {
            btnNavAssessments.getStyleClass().add("btn-nav-active");
            paneAssessments.setVisible(true);
        }
    }

    private void startSecureQuiz(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/school/management/fxml/quiz_window.fxml"));
            Parent root = loader.load();

            QuizWindowController controller = loader.getController();
            
            // Create undecorated, always-on-top, fullscreen exam terminal Stage
            Stage examStage = new Stage();
            examStage.initStyle(StageStyle.UNDECORATED);
            examStage.initModality(Modality.APPLICATION_MODAL);
            examStage.setAlwaysOnTop(true);
            examStage.setFullScreen(true);
            
            // Disable exiting fullscreen hint or esc exit
            examStage.setFullScreenExitHint("");
            examStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);

            // Set up scene
            Scene scene = new Scene(root);
            examStage.setScene(scene);

            // Bind the logic to controller
            controller.initQuiz(quiz, examStage, this);

            // Intercept window close requests
            examStage.setOnCloseRequest(event -> event.consume());

            examStage.showAndWait();
            
            // Refresh tables after quiz returns
            refreshData();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load examination terminal: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
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
