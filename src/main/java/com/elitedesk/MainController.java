package com.elitedesk;

import com.elitedesk.service.SpaceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class MainController implements Initializable {
    @FXML
    private TableView<Space> spacesTable;
    @FXML
    private TableColumn<Space, Number> idColumn;
    @FXML
    private TableColumn<Space, String> nameColumn;
    @FXML
    private TableColumn<Space, SpaceType> typeColumn;
    @FXML
    private TableColumn<Space, String> locationColumn;
    @FXML
    private TableColumn<Space, Number> capacityColumn;
    @FXML
    private TableColumn<Space, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Space, Void> bookingColumn;
    @FXML
    private Button createSpaceButton;

    private ObservableList<Space> spaces = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacityProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerHourProperty());

        // Initialize booking button column
        bookingColumn.setCellFactory(param -> new TableCell<>() {
            private final Button bookButton = new Button("Book");

            {
                bookButton.getStyleClass().add("blue-button");
                bookButton.setOnAction(event -> {
                    Space space = getTableView().getItems().get(getIndex());
                    openBookingView(space);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(bookButton);
                }
            }
        });

        loadSpaces();
    }

    private void loadSpaces() {
        try {
            // Fetch spaces from API
            List<Map<String, Object>> spacesData = SpaceService.getSpaces();
            spaces.clear(); // Clear existing spaces before adding new ones
            for (Map<String, Object> spaceData : spacesData) {
                Space space = new Space(
                        (String) spaceData.get("name"),
                        (String) spaceData.get("location"),
                        SpaceType.valueOf(((String) spaceData.get("type")).toUpperCase()),
                        ((Number) spaceData.get("capacity")).intValue(),
                        new BigDecimal(spaceData.get("pricePerHour").toString()));
                space.setId(((Number) spaceData.get("id")).longValue());
                spaces.add(space);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Show empty list if API fails
            spaces.clear();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load spaces", e.getMessage());
        }

        // Set items to the table
        spacesTable.setItems(spaces);
    }

    @FXML
    private void handleCreateSpace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_space.fxml"));
            Parent root = loader.load();

            CreateSpaceController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Create New Space");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open create space window", e.getMessage());
        }
    }

    // Method to add a new space
    public void addSpace(Space space) {
        spaces.add(space);
    }

    // Method to refresh the spaces list from the API
    public void refreshSpaces() {
        loadSpaces();
    }

    // Method to remove a space
    public void removeSpace(Space space) {
        spaces.remove(space);
    }

    // Method to update a space
    public void updateSpace(Space oldSpace, Space newSpace) {
        int index = spaces.indexOf(oldSpace);
        if (index != -1) {
            spaces.set(index, newSpace);
        }
    }

    private void openBookingView(Space space) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/space_booking.fxml"));
            Parent root = loader.load();

            SpaceBookingController controller = loader.getController();
            if (controller == null) {
                System.err.println("Failed to get SpaceBookingController");
                return;
            }
            controller.setSpace(space);

            Stage stage = new Stage();
            stage.setTitle("Book Space - " + space.getName());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading booking view: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open booking window",
                    "An error occurred while trying to open the booking window. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Apply custom styling to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        // Add appropriate style class based on alert type
        if (alertType == Alert.AlertType.ERROR) {
            dialogPane.getStyleClass().add("error-alert");
        } else if (alertType == Alert.AlertType.INFORMATION) {
            dialogPane.getStyleClass().add("info-alert");
        } else if (alertType == Alert.AlertType.WARNING) {
            dialogPane.getStyleClass().add("warning-alert");
        }

        alert.showAndWait();
    }

    @FXML
    private void handleSpaceSelection(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) {
            Space selectedSpace = spacesTable.getSelectionModel().getSelectedItem();
            if (selectedSpace != null) {
                openBookingView(selectedSpace);
            }
        }
    }
}