package com.elitedesk;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservedSpacesController implements Initializable {
    @FXML
    private TableView<ReservedSpace> reservedSpacesTable;
    @FXML
    private TableColumn<ReservedSpace, Number> idColumn;
    @FXML
    private TableColumn<ReservedSpace, String> nameColumn;
    @FXML
    private TableColumn<ReservedSpace, SpaceType> typeColumn;
    @FXML
    private TableColumn<ReservedSpace, String> locationColumn;
    @FXML
    private TableColumn<ReservedSpace, Number> capacityColumn;
    @FXML
    private TableColumn<ReservedSpace, BigDecimal> priceColumn;
    @FXML
    private TableColumn<ReservedSpace, LocalDateTime> reservationDateColumn;
    @FXML
    private TableColumn<ReservedSpace, Number> durationColumn;

    private ObservableList<ReservedSpace> reservedSpaces = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacityProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerHourProperty());
        reservationDateColumn.setCellValueFactory(cellData -> cellData.getValue().reservationDateProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());

        // Add sample data
        reservedSpaces.addAll(
                new ReservedSpace("Conference Room A", "1st Floor", SpaceType.CONFERENCE_ROOM, 20,
                        new BigDecimal("50.00"), LocalDateTime.now(), 2),
                new ReservedSpace("Meeting Room B", "1st Floor", SpaceType.MEETING_ROOM, 8,
                        new BigDecimal("35.00"), LocalDateTime.now().plusHours(1), 1));

        // Set items to the table
        reservedSpacesTable.setItems(reservedSpaces);
    }

    // Method to add a new reserved space
    public void addReservedSpace(ReservedSpace space) {
        reservedSpaces.add(space);
    }

    // Method to remove a reserved space
    public void removeReservedSpace(ReservedSpace space) {
        reservedSpaces.remove(space);
    }
}