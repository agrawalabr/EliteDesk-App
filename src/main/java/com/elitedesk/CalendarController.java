package com.elitedesk;

import com.elitedesk.model.Reservation;
import com.elitedesk.service.ReservationService;
import com.elitedesk.service.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class CalendarController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TextField titleField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label errorLabel;
    @FXML
    private Pane calendarPane;

    private ObservableList<Reservation> reservations;
    private long currentSpaceId;
    private LocalDate currentDate;
    private static final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    @FXML
    private void initialize() {
        reservations = FXCollections.observableArrayList();
        reservationsTable.setItems(reservations);
        currentDate = LocalDate.now();

        // Set up table columns
        TableColumn<Reservation, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Reservation, LocalDateTime> startTimeColumn = new TableColumn<>("Start Time");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Reservation, LocalDateTime> endTimeColumn = new TableColumn<>("End Time");
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        TableColumn<Reservation, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        reservationsTable.getColumns().setAll(titleColumn, startTimeColumn, endTimeColumn, statusColumn);

        // Set up selection listener
        reservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateFormWithReservation(newSelection);
            }
        });

        // Set default dates
        datePicker.setValue(currentDate);
        startDatePicker.setValue(currentDate);
        endDatePicker.setValue(currentDate);

        // Initialize calendar
        drawCalendar();
    }

    private void drawCalendar() {
        calendarPane.getChildren().clear();

        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.get(WeekFields.of(Locale.getDefault()).dayOfWeek());

        // Draw month and year
        Text monthYearText = new Text(yearMonth.format(monthYearFormatter));
        monthYearText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        monthYearText.setLayoutX(10);
        monthYearText.setLayoutY(20);
        calendarPane.getChildren().add(monthYearText);

        // Draw day names
        String[] dayNames = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        for (int i = 0; i < 7; i++) {
            Text dayName = new Text(dayNames[i]);
            dayName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            dayName.setLayoutX(10 + i * 40);
            dayName.setLayoutY(40);
            calendarPane.getChildren().add(dayName);
        }

        // Draw days
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            int row = (day + dayOfWeek - 2) / 7;
            int col = (day + dayOfWeek - 2) % 7;

            Rectangle dayRect = new Rectangle(35, 35);
            dayRect.setFill(Color.WHITE);
            dayRect.setStroke(Color.LIGHTGRAY);
            dayRect.setLayoutX(10 + col * 40);
            dayRect.setLayoutY(50 + row * 40);
            calendarPane.getChildren().add(dayRect);

            Text dayText = new Text(String.valueOf(day));
            dayText.setStyle("-fx-font-size: 14px;");
            dayText.setLayoutX(15 + col * 40);
            dayText.setLayoutY(70 + row * 40);
            calendarPane.getChildren().add(dayText);

            // Highlight current day
            if (day == currentDate.getDayOfMonth() &&
                    yearMonth.getMonth() == currentDate.getMonth() &&
                    yearMonth.getYear() == currentDate.getYear()) {
                dayRect.setFill(Color.LIGHTBLUE);
            }
        }
    }

    @FXML
    private void handleToday() {
        currentDate = LocalDate.now();
        datePicker.setValue(currentDate);
        drawCalendar();
        loadReservations();
    }

    @FXML
    private void handleThisWeek() {
        currentDate = LocalDate.now();
        datePicker.setValue(currentDate);
        drawCalendar();
        loadReservations();
    }

    @FXML
    private void handleThisMonth() {
        currentDate = LocalDate.now();
        datePicker.setValue(currentDate);
        drawCalendar();
        loadReservations();
    }

    public void setSpaceId(long spaceId) {
        this.currentSpaceId = spaceId;
        loadReservations();
    }

    private void loadReservations() {
        ReservationService.getReservationsForSpace(currentSpaceId)
                .thenAccept(reservationList -> {
                    Platform.runLater(() -> {
                        reservations.clear();
                        reservations.addAll(reservationList);
                    });
                });
    }

    @FXML
    private void handleViewReservations() {
        currentDate = datePicker.getValue();
        drawCalendar();
        loadReservations();
    }

    @FXML
    private void handleCreateReservation() {
        if (validateForm()) {
            Reservation reservation = new Reservation();
            reservation.setSpaceId(currentSpaceId);
            reservation.setUserId(SessionManager.getInstance().getEmail());
            reservation.setTitle(titleField.getText());
            reservation.setDescription(descriptionArea.getText());
            reservation.setStartTime(LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(9, 0)));
            reservation.setEndTime(LocalDateTime.of(endDatePicker.getValue(), LocalTime.of(17, 0)));
            reservation.setStatus("PENDING");

            ReservationService.createReservation(reservation)
                    .thenAccept(success -> {
                        Platform.runLater(() -> {
                            if (success) {
                                clearForm();
                                loadReservations();
                                errorLabel.setText("Reservation created successfully!");
                            } else {
                                errorLabel.setText("Failed to create reservation");
                            }
                        });
                    });
        }
    }

    @FXML
    private void handleUpdateReservation() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            errorLabel.setText("Please select a reservation to update");
            return;
        }

        if (validateForm()) {
            selectedReservation.setTitle(titleField.getText());
            selectedReservation.setDescription(descriptionArea.getText());
            selectedReservation.setStartTime(LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(9, 0)));
            selectedReservation.setEndTime(LocalDateTime.of(endDatePicker.getValue(), LocalTime.of(17, 0)));

            ReservationService.updateReservation(selectedReservation)
                    .thenAccept(success -> {
                        Platform.runLater(() -> {
                            if (success) {
                                clearForm();
                                loadReservations();
                                errorLabel.setText("Reservation updated successfully!");
                            } else {
                                errorLabel.setText("Failed to update reservation");
                            }
                        });
                    });
        }
    }

    @FXML
    private void handleDeleteReservation() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            errorLabel.setText("Please select a reservation to delete");
            return;
        }

        ReservationService.deleteReservation(selectedReservation.getId())
                .thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            clearForm();
                            loadReservations();
                            errorLabel.setText("Reservation deleted successfully!");
                        } else {
                            errorLabel.setText("Failed to delete reservation");
                        }
                    });
                });
    }

    private void updateFormWithReservation(Reservation reservation) {
        titleField.setText(reservation.getTitle());
        descriptionArea.setText(reservation.getDescription());
        startDatePicker.setValue(reservation.getStartTime().toLocalDate());
        endDatePicker.setValue(reservation.getEndTime().toLocalDate());
    }

    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
    }

    private boolean validateForm() {
        if (titleField.getText().isEmpty()) {
            errorLabel.setText("Please enter a title");
            return false;
        }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            errorLabel.setText("Please select both start and end dates");
            return false;
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            errorLabel.setText("Start date cannot be after end date");
            return false;
        }
        return true;
    }
}