package com.elitedesk;

import com.elitedesk.model.Reservation;
import com.elitedesk.model.TimeSlot;
import com.elitedesk.service.ReservationService;
import com.elitedesk.service.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpaceBookingController implements Initializable {
    @FXML
    private Label spaceNameLabel;
    @FXML
    private Label spaceDetailsLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private GridPane timeSlotGrid;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<LocalTime> startTimeCombo;
    @FXML
    private ComboBox<LocalTime> endTimeCombo;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label errorLabel;
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, String> timeColumn;
    @FXML
    private TableColumn<Reservation, String> titleColumn;
    @FXML
    private TableColumn<Reservation, String> statusColumn;

    private Space currentSpace;
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private static final LocalTime START_OF_DAY = LocalTime.of(9, 0);
    private static final LocalTime END_OF_DAY = LocalTime.of(17, 0);
    private static final int TIME_SLOT_DURATION = 30; // minutes
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatePicker();
        setupTimeCombos();
        setupReservationsTable();
    }

    public void setSpace(Space space) {
        this.currentSpace = space;
        spaceNameLabel.setText(space.getName());
        spaceDetailsLabel.setText(String.format("Location: %s | Type: %s | Capacity: %d | Price per hour: $%.2f",
                space.getLocation(), space.getType(), space.getCapacity(), space.getPricePerHour()));
        loadReservations();
    }

    private void setupDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.setOnAction(e -> loadReservations());
    }

    private void setupTimeCombos() {
        List<LocalTime> timeSlots = generateTimeSlots();
        startTimeCombo.setItems(FXCollections.observableArrayList(timeSlots));
        endTimeCombo.setItems(FXCollections.observableArrayList(timeSlots));

        StringConverter<LocalTime> timeConverter = new StringConverter<>() {
            @Override
            public String toString(LocalTime time) {
                return time != null ? time.format(timeFormatter) : "";
            }

            @Override
            public LocalTime fromString(String string) {
                return LocalTime.parse(string, timeFormatter);
            }
        };

        startTimeCombo.setConverter(timeConverter);
        endTimeCombo.setConverter(timeConverter);

        startTimeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                endTimeCombo.setItems(FXCollections.observableArrayList(
                        timeSlots.stream()
                                .filter(time -> time.isAfter(newVal))
                                .collect(Collectors.toList())));
            }
        });
    }

    private void setupReservationsTable() {
        timeColumn.setCellValueFactory(data -> {
            String timeRange = data.getValue().getStartTime().format(timeFormatter) +
                    " - " + data.getValue().getEndTime().format(timeFormatter);
            return javafx.beans.binding.Bindings.createStringBinding(() -> timeRange);
        });

        // Add custom cell factory for time column with styling
        timeColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(
                            "-fx-font-size: 14px; -fx-padding: 8px; -fx-background-color: #E3F2FD; -fx-border-radius: 3px; -fx-alignment: center;");
                }
            }
        });

        titleColumn.setCellValueFactory(
                data -> javafx.beans.binding.Bindings.createStringBinding(() -> data.getValue().getTitle()));

        // Add custom cell factory for title column with styling
        titleColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-font-weight: bold;");
                }
            }
        });

        statusColumn.setCellValueFactory(
                data -> javafx.beans.binding.Bindings.createStringBinding(() -> data.getValue().getStatus()));

        // Add custom cell factory for status column with different colors based on
        // status
        statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    // Different colors based on status
                    if ("CONFIRMED".equals(item)) {
                        setStyle(
                                "-fx-font-size: 14px; -fx-padding: 8px; -fx-text-fill: white; -fx-background-color: #388E3C; -fx-font-weight: bold; -fx-background-radius: 3px;");
                    } else if ("PENDING".equals(item)) {
                        setStyle(
                                "-fx-font-size: 14px; -fx-padding: 8px; -fx-text-fill: white; -fx-background-color: #FFA000; -fx-font-weight: bold; -fx-background-radius: 3px;");
                    } else if ("CANCELLED".equals(item)) {
                        setStyle(
                                "-fx-font-size: 14px; -fx-padding: 8px; -fx-text-fill: white; -fx-background-color: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 3px;");
                    } else {
                        setStyle(
                                "-fx-font-size: 14px; -fx-padding: 8px; -fx-text-fill: white; -fx-background-color: #1976D2; -fx-font-weight: bold; -fx-background-radius: 3px;");
                    }
                }
            }
        });

        reservationsTable.setItems(reservations);
    }

    private void setupTimeSlotGrid() {
        timeSlotGrid.getChildren().clear();
        timeSlotGrid.getColumnConstraints().clear();
        timeSlotGrid.getRowConstraints().clear();

        List<LocalTime> timeSlots = generateTimeSlots();

        // Add header row with styling
        Label timeHeader = new Label("Time");
        timeHeader.getStyleClass().add("time-slot-header");

        Label statusHeader = new Label("Status");
        statusHeader.getStyleClass().add("time-slot-header");

        timeSlotGrid.add(timeHeader, 0, 0);
        timeSlotGrid.add(statusHeader, 1, 0);

        // Add a separator
        HBox separator = new HBox();
        separator.getStyleClass().add("time-slots-header-separator");
        separator.setPrefHeight(2);
        timeSlotGrid.add(separator, 0, 1, 2, 1);

        // Add column constraints for better layout
        ColumnConstraints timeColumn = new ColumnConstraints();
        timeColumn.setMinWidth(180);
        timeColumn.setPrefWidth(180);

        ColumnConstraints statusColumn = new ColumnConstraints();
        statusColumn.setMinWidth(250);
        statusColumn.setPrefWidth(250);
        statusColumn.setHgrow(javafx.scene.layout.Priority.ALWAYS);

        timeSlotGrid.getColumnConstraints().addAll(timeColumn, statusColumn);

        // Add time slots
        int rowIndex = 2; // Start at row 2 after header and separator
        for (int i = 0; i < timeSlots.size() - 1; i++) {
            LocalTime startTime = timeSlots.get(i);
            LocalTime endTime = timeSlots.get(i + 1);

            HBox timeBox = new HBox();
            timeBox.getStyleClass().add("time-box");
            timeBox.setAlignment(Pos.CENTER_LEFT);

            Label timeLabel = new Label(String.format("%s - %s",
                    startTime.format(timeFormatter),
                    endTime.format(timeFormatter)));
            timeLabel.getStyleClass().add("time-label");

            timeBox.getChildren().add(timeLabel);

            StackPane timeSlot = createTimeSlot(startTime, endTime);

            timeSlotGrid.add(timeBox, 0, rowIndex);
            timeSlotGrid.add(timeSlot, 1, rowIndex);

            // Add row constraints for consistent height
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(70);
            if (rowIndex >= timeSlotGrid.getRowConstraints().size() + 2) {
                timeSlotGrid.getRowConstraints().add(rowConstraints);
            }

            rowIndex++;
        }
    }

    private StackPane createTimeSlot(LocalTime startTime, LocalTime endTime) {
        StackPane timeSlot = new StackPane();
        timeSlot.setPadding(new Insets(10));
        timeSlot.getStyleClass().add("time-slot");

        // Check if the time slot is available
        LocalDateTime slotStartDateTime = LocalDateTime.of(datePicker.getValue(), startTime);
        LocalDateTime slotEndDateTime = LocalDateTime.of(datePicker.getValue(), endTime);

        boolean isAvailable = reservations.stream()
                .noneMatch(r -> {
                    // Check if reservation overlaps with this time slot
                    LocalDateTime reservationStart = r.getStartTime();
                    LocalDateTime reservationEnd = r.getEndTime();

                    return !(slotEndDateTime.isBefore(reservationStart) || slotStartDateTime.isAfter(reservationEnd));
                });

        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setSpacing(10);

        Rectangle statusIndicator = new Rectangle(18, 18);
        statusIndicator.getStyleClass().add("status-indicator");

        Label statusText = new Label(isAvailable ? "Available" : "Reserved");
        statusText.getStyleClass().add("status-text");

        if (isAvailable) {
            timeSlot.getStyleClass().add("time-slot-available");
            statusText.getStyleClass().add("status-available");
            statusIndicator.setFill(Color.web("#4CAF50"));
            timeSlot.setCursor(Cursor.HAND);
            timeSlot.setOnMouseClicked(e -> handleTimeSlotClick(startTime, endTime));
        } else {
            timeSlot.getStyleClass().add("time-slot-reserved");
            statusText.getStyleClass().add("status-reserved");
            statusIndicator.setFill(Color.web("#F44336"));
        }

        statusBox.getChildren().addAll(statusIndicator, statusText);
        timeSlot.getChildren().add(statusBox);

        return timeSlot;
    }

    private void handleTimeSlotClick(LocalTime startTime, LocalTime endTime) {
        startTimeCombo.setValue(startTime);
        endTimeCombo.setValue(endTime);
    }

    private void updateTimeSlotGrid(List<TimeSlot> timeSlots) {
        timeSlotGrid.getChildren().clear();

        // Add header row
        Text timeHeader = new Text("Time");
        timeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
        Text statusHeader = new Text("Status");
        statusHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");

        timeSlotGrid.add(timeHeader, 0, 0);
        timeSlotGrid.add(statusHeader, 1, 0);

        // Add column constraints
        ColumnConstraints timeColumn = new ColumnConstraints();
        timeColumn.setMinWidth(100);
        ColumnConstraints statusColumn = new ColumnConstraints();
        statusColumn.setMinWidth(200);
        timeSlotGrid.getColumnConstraints().addAll(timeColumn, statusColumn);

        // Add time slots
        int row = 1;
        for (TimeSlot slot : timeSlots) {
            Text timeText = new Text(String.format("%s - %s",
                    slot.getStartTime().format(timeFormatter),
                    slot.getEndTime().format(timeFormatter)));
            timeText.setStyle("-fx-font-size: 14px");

            StackPane timeSlot = createTimeSlot(slot);

            timeSlotGrid.add(timeText, 0, row);
            timeSlotGrid.add(timeSlot, 1, row);

            // Add row constraints
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(40);
            timeSlotGrid.getRowConstraints().add(rowConstraints);

            row++;
        }

        // Add legend
        HBox legend = createLegend();
        timeSlotGrid.add(legend, 0, row, 2, 1);
    }

    private StackPane createTimeSlot(TimeSlot slot) {
        StackPane timeSlot = new StackPane();
        timeSlot.setPadding(new Insets(10));
        timeSlot.getStyleClass().add("time-slot");

        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setSpacing(10);

        Rectangle statusIndicator = new Rectangle(18, 18);
        statusIndicator.getStyleClass().add("status-indicator");

        Label statusText = new Label(slot.isAvailable() ? "Available" : "Reserved");
        statusText.getStyleClass().add("status-text");

        if (slot.isAvailable()) {
            timeSlot.getStyleClass().add("time-slot-available");
            statusText.getStyleClass().add("status-available");
            statusIndicator.setFill(Color.web("#4CAF50"));
            timeSlot.setCursor(Cursor.HAND);
            timeSlot.setOnMouseClicked(e -> handleTimeSlotClick(slot.getStartTime(), slot.getEndTime()));
        } else {
            timeSlot.getStyleClass().add("time-slot-reserved");
            statusText.getStyleClass().add("status-reserved");
            statusIndicator.setFill(Color.web("#F44336"));
        }

        statusBox.getChildren().addAll(statusIndicator, statusText);
        timeSlot.getChildren().add(statusBox);

        return timeSlot;
    }

    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setPadding(new Insets(15, 0, 5, 0));
        legend.setAlignment(Pos.CENTER);

        // Available legend item
        HBox availableItem = new HBox(10);
        availableItem.setAlignment(Pos.CENTER);
        Rectangle availableRect = new Rectangle(20, 20);
        availableRect.setFill(Color.web("#E3F2FD"));
        availableRect.setArcWidth(5);
        availableRect.setArcHeight(5);
        availableRect.setStroke(Color.LIGHTGRAY);
        Label availableLabel = new Label("Available");
        availableLabel.setStyle("-fx-font-size: 14px;");
        availableItem.getChildren().addAll(availableRect, availableLabel);

        // Booked legend item
        HBox bookedItem = new HBox(10);
        bookedItem.setAlignment(Pos.CENTER);
        Rectangle bookedRect = new Rectangle(20, 20);
        bookedRect.setFill(Color.web("#FFCDD2"));
        bookedRect.setArcWidth(5);
        bookedRect.setArcHeight(5);
        bookedRect.setStroke(Color.LIGHTGRAY);
        Label bookedLabel = new Label("Booked");
        bookedLabel.setStyle("-fx-font-size: 14px;");
        bookedItem.getChildren().addAll(bookedRect, bookedLabel);

        // Unavailable legend item
        HBox unavailableItem = new HBox(10);
        unavailableItem.setAlignment(Pos.CENTER);
        Rectangle unavailableRect = new Rectangle(20, 20);
        unavailableRect.setFill(Color.web("#F5F5F5"));
        unavailableRect.setArcWidth(5);
        unavailableRect.setArcHeight(5);
        unavailableRect.setStroke(Color.LIGHTGRAY);
        Label unavailableLabel = new Label("Unavailable");
        unavailableLabel.setStyle("-fx-font-size: 14px;");
        unavailableItem.getChildren().addAll(unavailableRect, unavailableLabel);

        legend.getChildren().addAll(availableItem, bookedItem, unavailableItem);
        return legend;
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime currentTime = START_OF_DAY;

        while (!currentTime.isAfter(END_OF_DAY)) {
            timeSlots.add(currentTime);
            currentTime = currentTime.plusMinutes(TIME_SLOT_DURATION);
        }

        return timeSlots;
    }

    private void loadReservations() {
        if (currentSpace == null || datePicker.getValue() == null)
            return;

        System.out.println(
                "Loading time slots for space: " + currentSpace.getId() + " on date: " + datePicker.getValue());

        // Show loading indicator
        timeSlotGrid.getChildren().clear();
        ProgressIndicator loading = new ProgressIndicator();
        loading.setMaxSize(50, 50);
        timeSlotGrid.add(loading, 0, 0, 2, 1);

        // Load availability for the time slot grid
        ReservationService.getAvailableTimeSlots(currentSpace.getId(), datePicker.getValue())
                .thenAccept(timeSlots -> {
                    Platform.runLater(() -> {
                        if (timeSlots.isEmpty()) {
                            showNoTimeSlotsMessage("No time slots found for the selected date.");
                        } else {
                            boolean hasAvailableSlots = timeSlots.stream().anyMatch(TimeSlot::isAvailable);
                            if (!hasAvailableSlots) {
                                showNoTimeSlotsMessage("All time slots are booked for the selected date.");
                            } else {
                                updateTimeSlotGrid(timeSlots);
                            }
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        timeSlotGrid.getChildren().clear();
                        VBox errorBox = new VBox(10);
                        errorBox.setAlignment(Pos.CENTER);

                        Label errorLabel = new Label("Failed to load time slots");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

                        String errorDetails = throwable.getMessage();
                        if (errorDetails != null && !errorDetails.isBlank()) {
                            Label detailsLabel = new Label(errorDetails);
                            detailsLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
                            detailsLabel.setWrapText(true);
                            errorBox.getChildren().addAll(errorLabel, detailsLabel);
                        } else {
                            errorBox.getChildren().add(errorLabel);
                        }

                        Button retryButton = new Button("Retry");
                        retryButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                        retryButton.setOnAction(e -> loadReservations());
                        errorBox.getChildren().add(retryButton);

                        timeSlotGrid.add(errorBox, 0, 0, 2, 1);
                    });
                    return null;
                });
    }

    private void showNoTimeSlotsMessage(String message) {
        timeSlotGrid.getChildren().clear();
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        DatePicker newDatePicker = new DatePicker();
        newDatePicker.setValue(datePicker.getValue().plusDays(1));
        newDatePicker.setStyle("-fx-font-size: 14px;");

        Button checkButton = new Button("Check Next Available Date");
        checkButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        checkButton.setOnAction(e -> {
            datePicker.setValue(newDatePicker.getValue());
            loadReservations();
        });

        messageBox.getChildren().addAll(messageLabel, newDatePicker, checkButton);
        timeSlotGrid.add(messageBox, 0, 0, 2, 1);
    }

    @FXML
    private void handleBookSpace() {
        if (!validateBookingForm())
            return;

        LocalDate selectedDate = datePicker.getValue();
        LocalTime startTime = startTimeCombo.getValue();
        LocalTime endTime = endTimeCombo.getValue();

        System.out.println("Creating reservation for space: " + currentSpace.getId());
        System.out.println("Selected date: " + selectedDate);
        System.out.println("Time slot: " + startTime + " - " + endTime);

        // Show processing feedback
        errorLabel.setText("Creating reservation...");
        errorLabel.setTextFill(Color.BLUE);

        Reservation newReservation = new Reservation();
        newReservation.setSpaceId(currentSpace.getId());
        newReservation.setUserId(SessionManager.getInstance().getEmail());
        newReservation.setStartTime(LocalDateTime.of(selectedDate, startTime));
        newReservation.setEndTime(LocalDateTime.of(selectedDate, endTime));

        ReservationService.createReservation(newReservation)
                .thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            // Show success message
                            errorLabel.setText("Reservation created successfully!");
                            errorLabel.setTextFill(Color.GREEN);

                            // Show success alert
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Booking Confirmation");
                            alert.setHeaderText("Reservation Successful");
                            alert.setContentText(String.format(
                                    "Your booking for %s has been confirmed.\nDate: %s\nTime: %s - %s",
                                    currentSpace.getName(),
                                    selectedDate.format(DateTimeFormatter.ofPattern("EE, MMM dd, yyyy")),
                                    startTime.format(timeFormatter),
                                    endTime.format(timeFormatter)));
                            alert.showAndWait();

                            clearBookingForm();
                            loadReservations();
                        } else {
                            errorLabel.setText("Failed to create reservation. Please try again.");
                            errorLabel.setTextFill(Color.RED);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        errorLabel.setText("Error: " + throwable.getMessage());
                        errorLabel.setTextFill(Color.RED);
                    });
                    return null;
                });
    }

    @FXML
    private void handleBack() {
        // Get the current stage from any @FXML injected control
        Stage stage = (Stage) spaceNameLabel.getScene().getWindow();
        stage.close();
    }

    private boolean validateBookingForm() {
        if (startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
            errorLabel.setText("Please select both start and end times");
            return false;
        }
        if (startTimeCombo.getValue().isAfter(endTimeCombo.getValue())) {
            errorLabel.setText("Start time cannot be after end time");
            return false;
        }
        if (currentSpace == null) {
            errorLabel.setText("No space selected");
            return false;
        }
        return true;
    }

    private void clearBookingForm() {
        titleField.clear();
        descriptionArea.clear();
        startTimeCombo.setValue(null);
        endTimeCombo.setValue(null);
    }
}