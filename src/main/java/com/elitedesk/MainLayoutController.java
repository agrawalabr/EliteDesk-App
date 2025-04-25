package com.elitedesk;

import com.elitedesk.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainLayoutController {
    @FXML
    private StackPane contentArea;

    @FXML
    private void initialize() {
        showAvailableSpaces();
    }

    @FXML
    private void showAvailableSpaces() {
        try {
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

            // Set the new scene
            stage.setScene(new Scene(loginView));
            stage.setTitle("EliteDesk - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}