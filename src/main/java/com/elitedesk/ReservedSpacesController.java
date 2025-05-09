package com.elitedesk;

import com.elitedesk.model.Reservation;
import com.elitedesk.service.ReservationService;
import com.elitedesk.service.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservedSpacesController implements Initializable {
        @FXML
        private TabPane reservationsTabPane;
        @FXML
        private Tab upcomingTab;
        @FXML
        private Tab pastTab;
        @FXML
        private TableView<Reservation> upcomingReservationsTable;
        @FXML
        private TableView<Reservation> pastReservationsTable;
        @FXML
        private TableColumn<Reservation, String> upcomingSpaceNameColumn;
        @FXML
        private TableColumn<Reservation, String> upcomingSpaceLocationColumn;
        @FXML
        private TableColumn<Reservation, String> upcomingSpaceTypeColumn;
        @FXML
        private TableColumn<Reservation, String> upcomingStartTimeColumn;
        @FXML
        private TableColumn<Reservation, String> upcomingEndTimeColumn;
        @FXML
        private TableColumn<Reservation, String> upcomingStatusColumn;
        @FXML
        private TableColumn<Reservation, Void> upcomingActionColumn;
        @FXML
        private TableColumn<Reservation, String> pastSpaceNameColumn;
        @FXML
        private TableColumn<Reservation, String> pastSpaceLocationColumn;
        @FXML
        private TableColumn<Reservation, String> pastSpaceTypeColumn;
        @FXML
        private TableColumn<Reservation, String> pastStartTimeColumn;
        @FXML
        private TableColumn<Reservation, String> pastEndTimeColumn;
        @FXML
        private TableColumn<Reservation, String> pastStatusColumn;
        @FXML
        private Label debugLabel;

        private ObservableList<Reservation> upcomingReservations = FXCollections.observableArrayList();
        private ObservableList<Reservation> pastReservations = FXCollections.observableArrayList();
        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

        @Override
        public void initialize(URL location, ResourceBundle resources) {
                System.out.println("Initializing ReservedSpacesController");

                // Initialize upcoming reservations columns
                setupTableColumn(upcomingSpaceNameColumn,
                                data -> Bindings.createStringBinding(() -> data.getValue().getSpaceName()));
                setupTableColumn(upcomingSpaceLocationColumn,
                                data -> Bindings.createStringBinding(() -> data.getValue().getSpaceLocation()));
                setupTableColumn(upcomingSpaceTypeColumn,
                                data -> Bindings.createStringBinding(() -> data.getValue().getSpaceType()));
                setupDateTimeColumn(upcomingStartTimeColumn,
                                data -> Bindings.createStringBinding(
                                                () -> data.getValue().getStartTime().format(dateTimeFormatter)),
                                "#f0f8ff");
                setupDateTimeColumn(upcomingEndTimeColumn,
                                data -> Bindings.createStringBinding(
                                                () -> data.getValue().getEndTime().format(dateTimeFormatter)),
                                "#fff0f5");
                setupStatusColumn(upcomingStatusColumn);
                setupCancelButtonColumn(upcomingActionColumn);

                // Initialize past reservations columns
                setupTableColumn(pastSpaceNameColumn,
                                data -> Bindings.createStringBinding(() -> data.getValue().getSpaceName()));
                setupTableColumn(pastSpaceLocationColumn,
                                data -> Bindings.createStringBinding(() -> data.getValue().getSpaceLocation()));
                setupTableColumn(pastSpaceTypeColumn,
                                data -> Bindings.createStringBinding(() -> data.getValue().getSpaceType()));
                setupDateTimeColumn(pastStartTimeColumn,
                                data -> Bindings.createStringBinding(
                                                () -> data.getValue().getStartTime().format(dateTimeFormatter)),
                                "#f0f8ff");
                setupDateTimeColumn(pastEndTimeColumn,
                                data -> Bindings.createStringBinding(
                                                () -> data.getValue().getEndTime().format(dateTimeFormatter)),
                                "#fff0f5");
                setupStatusColumn(pastStatusColumn);

                // Set items to the tables
                upcomingReservationsTable.setItems(upcomingReservations);
                pastReservationsTable.setItems(pastReservations);

                // Log authentication state
                System.out.println("User logged in: " + SessionManager.getInstance().isLoggedIn());
                System.out.println("User email: " + SessionManager.getInstance().getEmail());
                System.out.println("User token available: " + (SessionManager.getInstance().getToken() != null));

                // Load user reservations
                loadUserReservations();
        }

        private <T> void setupTableColumn(TableColumn<Reservation, T> column,
                        javafx.util.Callback<TableColumn.CellDataFeatures<Reservation, T>, ObservableValue<T>> callback) {
                column.setCellValueFactory(callback);
        }

        private void setupDateTimeColumn(TableColumn<Reservation, String> column,
                        javafx.util.Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>> callback,
                        String bgColor) {
                column.setCellValueFactory(callback);
                column.setCellFactory(col -> {
                        return new javafx.scene.control.TableCell<Reservation, String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (item == null || empty) {
                                                setText(null);
                                                setStyle("");
                                        } else {
                                                setText(item);
                                                setStyle("-fx-font-size: 12px; -fx-padding: 5px; -fx-background-color: "
                                                                +
                                                                bgColor
                                                                + "; -fx-border-radius: 3px; -fx-text-alignment: center; -fx-wrap-text: true;");
                                        }
                                }
                        };
                });
        }

        private void setupStatusColumn(TableColumn<Reservation, String> column) {
                column.setCellValueFactory(
                                data -> Bindings.createStringBinding(() -> data.getValue().getStatus()));
                column.setCellFactory(col -> {
                        return new javafx.scene.control.TableCell<Reservation, String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (item == null || empty) {
                                                setText(null);
                                                setStyle("");
                                        } else {
                                                setText(item);
                                                if (item.equalsIgnoreCase("CONFIRMED")) {
                                                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                                } else if (item.equalsIgnoreCase("PENDING")) {
                                                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                                                } else if (item.equalsIgnoreCase("CANCELLED")) {
                                                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                                } else {
                                                        setStyle("-fx-text-fill: black;");
                                                }
                                        }
                                }
                        };
                });
        }

        private void setupCancelButtonColumn(TableColumn<Reservation, Void> column) {
                column.setCellFactory(col -> {
                        return new javafx.scene.control.TableCell<Reservation, Void>() {
                                private final Button cancelButton = new Button("Cancel");

                                {
                                        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                                        cancelButton.setOnAction(event -> {
                                                Reservation reservation = getTableView().getItems().get(getIndex());
                                                handleCancelReservation(reservation);
                                        });
                                }

                                @Override
                                protected void updateItem(Void item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty) {
                                                setGraphic(null);
                                        } else {
                                                Reservation reservation = getTableView().getItems().get(getIndex());
                                                if ("CANCELLED".equalsIgnoreCase(reservation.getStatus())) {
                                                        setGraphic(null);
                                                } else {
                                                        setGraphic(cancelButton);
                                                }
                                        }
                                }
                        };
                });
        }

        private void handleCancelReservation(Reservation reservation) {
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Cancel Reservation");
                confirmDialog.setHeaderText("Are you sure you want to cancel this reservation?");
                confirmDialog.setContentText("Space: " + reservation.getSpaceName() +
                                "\nDate: "
                                + reservation.getStartTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy")) +
                                "\nTime: " + reservation.getStartTime().format(DateTimeFormatter.ofPattern("h:mm a")) +
                                " - " + reservation.getEndTime().format(DateTimeFormatter.ofPattern("h:mm a")));

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                        if (debugLabel != null) {
                                debugLabel.setText("Cancelling reservation...");
                                debugLabel.setVisible(true);
                        }

                        ReservationService.cancelReservation(reservation.getId())
                                        .thenAccept(success -> {
                                                Platform.runLater(() -> {
                                                        if (success) {
                                                                if (debugLabel != null) {
                                                                        debugLabel.setText(
                                                                                        "Reservation cancelled successfully.");
                                                                        debugLabel.setStyle(
                                                                                        "-fx-text-fill: #4CAF50; -fx-background-color: #E8F5E9;");
                                                                        debugLabel.setVisible(true);
                                                                }
                                                                loadUserReservations(); // Refresh the tables
                                                        } else {
                                                                if (debugLabel != null) {
                                                                        debugLabel.setText(
                                                                                        "Failed to cancel reservation. Please try again.");
                                                                        debugLabel.setStyle(
                                                                                        "-fx-text-fill: #e74c3c; -fx-background-color: #ffebee;");
                                                                        debugLabel.setVisible(true);
                                                                }
                                                        }
                                                });
                                        })
                                        .exceptionally(throwable -> {
                                                Platform.runLater(() -> {
                                                        if (debugLabel != null) {
                                                                debugLabel.setText("Error: " + throwable.getMessage());
                                                                debugLabel.setStyle(
                                                                                "-fx-text-fill: #e74c3c; -fx-background-color: #ffebee;");
                                                                debugLabel.setVisible(true);
                                                        }
                                                });
                                                return null;
                                        });
                }
        }

        private void loadUserReservations() {
                System.out.println("Loading user reservations...");
                ReservationService.getUserReservations()
                                .thenAccept(reservationResponse -> {
                                        System.out.println("Reservation response received");
                                        System.out.println("Upcoming reservations: " +
                                                        (reservationResponse.getUpcomingReservations() != null
                                                                        ? reservationResponse.getUpcomingReservations()
                                                                                        .size()
                                                                        : "null"));
                                        System.out.println("Past reservations: " +
                                                        (reservationResponse.getPastReservations() != null
                                                                        ? reservationResponse.getPastReservations()
                                                                                        .size()
                                                                        : "null"));

                                        Platform.runLater(() -> {
                                                // Update our observable lists
                                                upcomingReservations.clear();
                                                upcomingReservations
                                                                .addAll(reservationResponse.getUpcomingReservations());

                                                pastReservations.clear();
                                                pastReservations.addAll(reservationResponse.getPastReservations());

                                                // Update the tables
                                                upcomingReservationsTable.setItems(upcomingReservations);
                                                pastReservationsTable.setItems(pastReservations);

                                                // Update debug label if no reservations
                                                if (upcomingReservations.isEmpty() && pastReservations.isEmpty()) {
                                                        if (debugLabel != null) {
                                                                debugLabel.setText(
                                                                                "No reservations found. You may need to make some bookings first.");
                                                                debugLabel.setVisible(true);
                                                        }
                                                        System.out.println("No reservations found.");
                                                } else {
                                                        if (debugLabel != null) {
                                                                debugLabel.setVisible(false);
                                                        }
                                                }
                                        });
                                })
                                .exceptionally(throwable -> {
                                        System.err.println("Error loading reservations: " + throwable.getMessage());
                                        throwable.printStackTrace();

                                        Platform.runLater(() -> {
                                                if (debugLabel != null) {
                                                        debugLabel.setText("Error loading reservations: "
                                                                        + throwable.getMessage());
                                                        debugLabel.setVisible(true);
                                                }
                                        });

                                        return null;
                                });
        }

        @FXML
        private void handleRefresh() {
                System.out.println("Refreshing reservations...");
                loadUserReservations();
        }
}