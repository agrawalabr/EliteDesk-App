package com.elitedesk;

import com.elitedesk.service.AuthService;
import com.elitedesk.service.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MainLayoutController {
    @FXML
    private StackPane contentArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button availableSpacesButton;

    @FXML
    private Button reservedSpacesButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        // Set the welcome message with the user's name
        String username = SessionManager.getInstance().getName();
        if (username != null && !username.isEmpty()) {
            welcomeLabel.setText("Welcome, " + username + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }

        // Force CSS reload
        Scene scene = welcomeLabel.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        }

        // Show available spaces by default
        showAvailableSpaces();
    }

    private void resetButtonStyles() {
        availableSpacesButton.getStyleClass().remove("active-button");
        availableSpacesButton.getStyleClass().add("blue-button");
        reservedSpacesButton.getStyleClass().remove("active-button");
        reservedSpacesButton.getStyleClass().add("blue-button");
    }

    @FXML
    private void showAvailableSpaces() {
        try {
            // Update button styles
            resetButtonStyles();
            availableSpacesButton.getStyleClass().remove("blue-button");
            availableSpacesButton.getStyleClass().add("active-button");

            // Load view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent availableSpacesView = loader.load();
            contentArea.getChildren().setAll(availableSpacesView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showReservedSpaces() {
        try {
            // Update button styles
            resetButtonStyles();
            reservedSpacesButton.getStyleClass().remove("blue-button");
            reservedSpacesButton.getStyleClass().add("active-button");

            // Load view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reserved_spaces.fxml"));
            Parent reservedSpacesView = loader.load();
            contentArea.getChildren().setAll(reservedSpacesView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        AuthService.logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginView = loader.load();

            // Get the current stage
            Stage stage = (Stage) contentArea.getScene().getWindow();

            // Create a new scene with the stylesheet
            Scene scene = new Scene(loginView);
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

            // Set the new scene
            stage.setScene(scene);
            stage.setTitle("EliteDesk - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}