package com.elitedesk;

import com.elitedesk.model.Reservation;
import com.elitedesk.service.ReservationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservedSpacesController implements Initializable {
        @FXML
        private TableView<Reservation> reservationsTable;
        @FXML
        private TableColumn<Reservation, String> spaceNameColumn;
        @FXML
        private TableColumn<Reservation, String> spaceLocationColumn;
        @FXML
        private TableColumn<Reservation, String> spaceTypeColumn;
        @FXML
        private TableColumn<Reservation, String> startTimeColumn;
        @FXML
        private TableColumn<Reservation, String> endTimeColumn;
        @FXML
        private TableColumn<Reservation, String> statusColumn;

        private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

        @Override
        public void initialize(URL location, ResourceBundle resources) {
                // Initialize columns
                spaceNameColumn.setCellValueFactory(
                                data -> javafx.beans.binding.Bindings
                                                .createStringBinding(() -> data.getValue().getSpaceName()));
                spaceLocationColumn.setCellValueFactory(
                                data -> javafx.beans.binding.Bindings
                                                .createStringBinding(() -> data.getValue().getSpaceLocation()));
                spaceTypeColumn.setCellValueFactory(
                                data -> javafx.beans.binding.Bindings
                                                .createStringBinding(() -> data.getValue().getSpaceType()));

                // Custom cell factories for date/time columns
                startTimeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings
                                .createStringBinding(() -> data.getValue().getStartTime().format(dateTimeFormatter)));
                startTimeColumn.setCellFactory(column -> {
                        return new javafx.scene.control.TableCell<Reservation, String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (item == null || empty) {
                                                setText(null);
                                                setStyle("");
                                        } else {
                                                setText(item);
                                                setStyle("-fx-font-size: 12px; -fx-padding: 5px; -fx-background-color: #f0f8ff; -fx-border-radius: 3px; -fx-text-alignment: center; -fx-wrap-text: true;");
                                        }
                                }
                        };
                });

                endTimeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings
                                .createStringBinding(() -> data.getValue().getEndTime().format(dateTimeFormatter)));
                endTimeColumn.setCellFactory(column -> {
                        return new javafx.scene.control.TableCell<Reservation, String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (item == null || empty) {
                                                setText(null);
                                                setStyle("");
                                        } else {
                                                setText(item);
                                                setStyle("-fx-font-size: 12px; -fx-padding: 5px; -fx-background-color: #fff0f5; -fx-border-radius: 3px; -fx-text-alignment: center; -fx-wrap-text: true;");
                                        }
                                }
                        };
                });

                // Status column with custom styling
                statusColumn.setCellValueFactory(
                                data -> javafx.beans.binding.Bindings
                                                .createStringBinding(() -> data.getValue().getStatus()));
                statusColumn.setCellFactory(column -> {
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

                // Set items to the table
                reservationsTable.setItems(reservations);

                // Load user reservations
                loadUserReservations();
        }

        private void loadUserReservations() {
                ReservationService.getUserReservations()
                                .thenAccept(reservationList -> {
                                        Platform.runLater(() -> {
                                                reservations.clear();
                                                reservations.addAll(reservationList);
                                        });
                                });
        }

        @FXML
        private void handleRefresh() {
                loadUserReservations();
        }
}