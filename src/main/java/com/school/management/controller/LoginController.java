package com.school.management.controller;

import com.school.management.App;
import com.school.management.config.EnvConfig;
import com.school.management.database.DatabaseManager;
import com.school.management.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // 1. Check Admin Credentials from EnvConfig
        String adminEmail = EnvConfig.get("ADMIN_EMAIL", "admin@school.com");
        String adminPassword = EnvConfig.get("ADMIN_PASSWORD", "admin123");

        if (username.equalsIgnoreCase(adminEmail) && password.equals(adminPassword)) {
            // Log in as default admin
            User admin = new User("admin", "System Administrator", adminEmail, adminPassword, User.Role.ADMIN);
            App.currentUser = admin;
            navigateToDashboard(event, "/com/school/management/fxml/admin_dashboard.fxml");
            return;
        }

        // 2. Check Database for Teachers or Students
        User dbUser = DatabaseManager.getUserById(username);
        if (dbUser != null && dbUser.getPassword().equals(password)) {
            App.currentUser = dbUser;
            if (dbUser.getRole() == User.Role.TEACHER) {
                navigateToDashboard(event, "/com/school/management/fxml/teacher_dashboard.fxml");
            } else if (dbUser.getRole() == User.Role.STUDENT) {
                navigateToDashboard(event, "/com/school/management/fxml/student_dashboard.fxml");
            }
            return;
        }

        showError("Invalid Username/ID or Password.");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void navigateToDashboard(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            // Adjust window size for dashboard
            stage.setWidth(1280);
            stage.setHeight(800);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }
}
