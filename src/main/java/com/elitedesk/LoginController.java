package com.elitedesk;

import com.elitedesk.service.AuthService;
import com.elitedesk.service.SessionManager;
import com.elitedesk.service.SpaceService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField nameField;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorLabel;

    private boolean isRegisterMode = false;

    @FXML
    private void initialize() {
        // Set up button actions
        loginButton.setOnAction(e -> {
            isRegisterMode = false;
            updateFormVisibility();
            handleLogin();
        });

        registerButton.setOnAction(e -> {
            isRegisterMode = true;
            updateFormVisibility();
            handleRegister();
        });
    }

    private void updateFormVisibility() {
        nameLabel.setVisible(isRegisterMode);
        nameField.setVisible(isRegisterMode);
        nameField.setManaged(isRegisterMode);
        nameLabel.setManaged(isRegisterMode);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password");
            return;
        }

        try {
            // Clear any previous error
            errorLabel.setText("");

            // Attempt login
            AuthService.login(email, password).thenAccept(response -> {
                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        // Store session details
                        SessionManager sessionManager = SessionManager.getInstance();
                        sessionManager.setSession(
                                response.getData().get("token").toString(),
                                response.getData().get("email").toString(),
                                response.getData().get("name").toString(),
                                response.getData().get("role").toString());

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_layout.fxml"));
                            Parent mainLayout = loader.load();

                            // Get the welcome label from the main layout
                            Label welcomeLabel = (Label) mainLayout.lookup("#welcomeLabel");
                            if (welcomeLabel != null) {
                                welcomeLabel.setText("Welcome, " + sessionManager.getName() + "!");
                            }

                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setScene(new Scene(mainLayout));
                            stage.setTitle("EliteDesk - Workspace Management");
                        } catch (Exception e) {
                            errorLabel.setText("Error navigating to main page: " + e.getMessage());
                        }
                    } else {
                        errorLabel.setText("Login failed: " + response.getError());
                    }
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    errorLabel.setText("Login failed: " + throwable.getMessage());
                });
                return null;
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Unknown error occurred";
            }
            errorLabel.setText("Login failed: " + errorMessage);
        }
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        try {
            AuthService.register(email, password, name).thenAccept(response -> {
                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        // Show success message
                        errorLabel.setStyle("-fx-text-fill: green;");
                        errorLabel.setText("Registration successful! Please login with your credentials.");

                        // Clear the form
                        nameField.clear();
                        emailField.clear();
                        passwordField.clear();

                        // Switch back to login mode
                        isRegisterMode = false;
                        updateFormVisibility();
                    } else {
                        errorLabel.setStyle("-fx-text-fill: red;");
                        errorLabel.setText("Registration failed: " + response.getError());
                    }
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    errorLabel.setStyle("-fx-text-fill: red;");
                    errorLabel.setText("Registration failed: " + throwable.getMessage());
                });
                return null;
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Unknown error occurred";
            }
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Registration failed: " + errorMessage);
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}