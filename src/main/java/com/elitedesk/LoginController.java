package com.elitedesk;

import com.elitedesk.service.AuthService;
import com.elitedesk.service.SpaceService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.List;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField nameField;

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
                if (response.isSuccess()) {
                    try {
                        // After successful login, fetch spaces
                        List<Map<String, Object>> spaces = SpaceService.getSpaces();
                        showAlert(AlertType.INFORMATION, "Login Successful",
                                "Welcome!\nFound " + spaces.size() + " spaces");
                    } catch (Exception e) {
                        errorLabel.setText("Error fetching spaces: " + e.getMessage());
                    }
                } else {
                    errorLabel.setText("Login failed: " + response.getError());
                }
            }).exceptionally(throwable -> {
                errorLabel.setText("Login failed: " + throwable.getMessage());
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
                if (response.isSuccess()) {
                    showAlert(AlertType.INFORMATION, "Registration Successful",
                            "Account created for " + name + "\nPlease login to continue");

                    // Clear the form after successful registration
                    nameField.clear();
                    emailField.clear();
                    passwordField.clear();
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Registration failed: " + response.getError());
                }
            }).exceptionally(throwable -> {
                errorLabel.setText("Registration failed: " + throwable.getMessage());
                return null;
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Unknown error occurred";
            }
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