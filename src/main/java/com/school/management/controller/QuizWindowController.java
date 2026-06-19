package com.school.management.controller;

import com.school.management.App;
import com.school.management.database.DatabaseManager;
import com.school.management.model.Question;
import com.school.management.model.Quiz;
import com.school.management.model.QuizResult;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuizWindowController {

    @FXML private Label lblQuizTitle;
    @FXML private Label lblTimer;
    @FXML private VBox paneQuestionBox;
    @FXML private Label lblQuestionIndex;
    @FXML private Text txtQuestionText;
    @FXML private RadioButton radOptionA;
    @FXML private ToggleGroup grpOptions;
    @FXML private RadioButton radOptionB;
    @FXML private RadioButton radOptionC;
    @FXML private RadioButton radOptionD;
    @FXML private Button btnNext;
    @FXML private Button btnSubmit;

    private Quiz quiz;
    private Stage stage;
    private StudentController parentController;

    private int currentQuestionIndex = 0;
    private List<Integer> studentAnswers; // Index of options chosen per question
    
    private int secondsRemaining;
    private Timeline timerTimeline;

    private int focusLostCount = 0;
    private boolean quizFinished = false;

    public void initQuiz(Quiz quiz, Stage stage, StudentController parentController) {
        this.quiz = quiz;
        this.stage = stage;
        this.parentController = parentController;
        this.lblQuizTitle.setText(quiz.getTitle());

        int totalQuestions = quiz.getQuestions().size();
        this.studentAnswers = new ArrayList<>(Collections.nCopies(totalQuestions, -1));

        // Start timer
        this.secondsRemaining = quiz.getTimeLimitMinutes() * 60;
        startTimer();

        // Load first question
        showQuestion(0);

        // Security monitoring: Monitor focus loss
        Platform.runLater(() -> {
            stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (!isFocused && !quizFinished) {
                    handleSecurityViolation();
                }
            });
        });
    }

    private void startTimer() {
        updateTimerLabel();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            updateTimerLabel();
            if (secondsRemaining <= 0) {
                timerTimeline.stop();
                Platform.runLater(this::autoSubmitQuizDueToTimeout);
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateTimerLabel() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        lblTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= quiz.getQuestions().size()) return;

        currentQuestionIndex = index;
        Question q = quiz.getQuestions().get(index);

        lblQuestionIndex.setText("Question " + (index + 1) + " of " + quiz.getQuestions().size());
        txtQuestionText.setText(q.getQuestionText());

        radOptionA.setText(q.getOptions().get(0));
        radOptionB.setText(q.getOptions().get(1));
        radOptionC.setText(q.getOptions().get(2));
        radOptionD.setText(q.getOptions().get(3));

        // Reset radio selection or select previously saved answer
        grpOptions.selectToggle(null);
        int savedAnswer = studentAnswers.get(index);
        if (savedAnswer != -1) {
            switch (savedAnswer) {
                case 0: grpOptions.selectToggle(radOptionA); break;
                case 1: grpOptions.selectToggle(radOptionB); break;
                case 2: grpOptions.selectToggle(radOptionC); break;
                case 3: grpOptions.selectToggle(radOptionD); break;
            }
        }

        // Adjust buttons
        if (index == quiz.getQuestions().size() - 1) {
            btnNext.setVisible(false);
            btnSubmit.setVisible(true);
        } else {
            btnNext.setVisible(true);
            btnSubmit.setVisible(false);
        }
    }

    private void saveCurrentAnswer() {
        Toggle selected = grpOptions.getSelectedToggle();
        if (selected == null) {
            studentAnswers.set(currentQuestionIndex, -1);
            return;
        }

        int answerIndex = -1;
        if (selected == radOptionA) answerIndex = 0;
        else if (selected == radOptionB) answerIndex = 1;
        else if (selected == radOptionC) answerIndex = 2;
        else if (selected == radOptionD) answerIndex = 3;

        studentAnswers.set(currentQuestionIndex, answerIndex);
    }

    @FXML
    void handleNext(ActionEvent event) {
        saveCurrentAnswer();
        showQuestion(currentQuestionIndex + 1);
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        saveCurrentAnswer();
        submitQuiz(false, "Quiz Submitted Successfully");
    }

    private void submitQuiz(boolean isViolation, String reasonTitle) {
        if (quizFinished) return;
        quizFinished = true;

        if (timerTimeline != null) {
            timerTimeline.stop();
        }

        // Calculate score
        int tempScore = 0;
        List<Question> questionsList = quiz.getQuestions();
        for (int i = 0; i < questionsList.size(); i++) {
            if (studentAnswers.get(i) == questionsList.get(i).getCorrectOptionIndex()) {
                tempScore++;
            }
        }
        final int score = tempScore;
        final int totalQuestions = questionsList.size();

        // Save result
        String resultId = UUID.randomUUID().toString();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        QuizResult result = new QuizResult(
                resultId,
                quiz.getQuizId(),
                quiz.getTitle(),
                App.currentUser.getId(),
                App.currentUser.getName(),
                score,
                totalQuestions,
                today
        );
        DatabaseManager.addQuizResult(result);

        // Show Score Dialog
        final String finalReason = isViolation ? "Auto-submitted due to focus loss (attempting to change window)" : "Regular Submission";
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(reasonTitle);
            alert.setHeaderText(isViolation ? "⚠️ SECURITY INFRACTION ENFORCEMENT" : "Exam Session Terminated");
            alert.setContentText(String.format("You scored: %d out of %d\nPercentage: %.1f%%\n\nReason: %s",
                    score, totalQuestions, ((double) score / totalQuestions) * 100.0, finalReason));
            alert.showAndWait();

            // Exit fullscreen lock stage
            stage.close();
        });
    }

    private void handleSecurityViolation() {
        focusLostCount++;
        if (focusLostCount == 1) {
            // Warn student
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠️ SECURITY WARNING");
                alert.setHeaderText("Focus Loss Detected");
                alert.setContentText("You are not allowed to switch windows or applications during this quiz.\n"
                        + "Any further attempts to switch screens will cause your quiz to be INSTANTLY submitted with a score calculated up to this point.");
                alert.showAndWait();

                // Re-force fullscreen
                stage.setFullScreen(true);
                stage.requestFocus();
            });
        } else {
            // Auto submit
            submitQuiz(true, "⚠️ SECURITY INFRACTION AUTO-SUBMISSION");
        }
    }

    private void autoSubmitQuizDueToTimeout() {
        saveCurrentAnswer();
        submitQuiz(false, "Timeout Reached");
    }
}
