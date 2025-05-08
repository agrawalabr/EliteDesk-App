package com.elitedesk;

import com.elitedesk.service.SpaceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.net.URL;
import java.util.ResourceBundle;
import java.math.BigDecimal;
import java.util.Map;

public class CreateSpaceController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<SpaceType> typeComboBox;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField priceField;
    @FXML
    private Button createButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label errorLabel;

    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize type combo box with all space types
        typeComboBox.setItems(FXCollections.observableArrayList(SpaceType.values()));

        // Set default selection to the first type
        if (SpaceType.values().length > 0) {
            typeComboBox.setValue(SpaceType.values()[0]);
        }

        // Set up numeric validation for capacity field
        capacityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capacityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Set up decimal validation for price field
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleCreateSpace() {
        if (!validateForm()) {
            return;
        }

        try {
            // Create a new Space object from form data
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            SpaceType type = typeComboBox.getValue();
            int capacity = Integer.parseInt(capacityField.getText().trim());
            BigDecimal price = new BigDecimal(priceField.getText().trim());

            Space newSpace = new Space(name, location, type, capacity, price);

            // Call API to create space
            Map<String, Object> createdSpace = SpaceService.createSpace(newSpace);

            // Set the generated ID from the response if available
            if (createdSpace != null && createdSpace.get("id") != null) {
                Number id = (Number) createdSpace.get("id");
                newSpace.setId(id.longValue());
            }

            // Add the new space to the main controller's list
            if (mainController != null) {
                mainController.refreshSpaces(); // Refresh the spaces list
            }

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Space Created");
            alert.setContentText("The space has been created successfully.");
            alert.showAndWait();

            // Close the window
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    private boolean validateForm() {
        // Reset error message
        errorLabel.setVisible(false);

        // Check if name is empty
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter a name for the space");
            return false;
        }

        // Check if location is empty
        if (locationField.getText().trim().isEmpty()) {
            showError("Please enter a location for the space");
            return false;
        }

        // Check if type is selected
        if (typeComboBox.getValue() == null) {
            showError("Please select a space type");
            return false;
        }

        // Check if capacity is valid
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                showError("Capacity must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for capacity");
            return false;
        }

        // Check if price is valid
        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for price");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}