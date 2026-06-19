package com.school.management;

import com.school.management.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static User currentUser = null;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/school/management/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            stage.setTitle("IQRA Academy - LMS Portal");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Critical Error starting application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
