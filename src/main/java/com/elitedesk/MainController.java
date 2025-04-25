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

        try {
            // Fetch spaces from API
            List<Map<String, Object>> spacesData = SpaceService.getSpaces();
            for (Map<String, Object> spaceData : spacesData) {
                Space space = new Space(
                        (String) spaceData.get("name"),
                        (String) spaceData.get("location"),
                        SpaceType.valueOf(((String) spaceData.get("type")).toUpperCase()),
                        ((Number) spaceData.get("capacity")).intValue(),
                        new BigDecimal(spaceData.get("pricePerHour").toString()));
                spaces.add(space);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Add some sample data if API fails
            spaces.addAll(
                    new Space("Conference Room A", "1st Floor", SpaceType.CONFERENCE_ROOM, 20, new BigDecimal("50.00")),
                    new Space("Open Space 1", "2nd Floor", SpaceType.WORK_AREA, 50, new BigDecimal("25.00")),
                    new Space("Meeting Room B", "1st Floor", SpaceType.MEETING_ROOM, 8, new BigDecimal("35.00")),
                    new Space("Quiet Zone", "3rd Floor", SpaceType.FOCUS_AREA, 15, new BigDecimal("30.00")),
                    new Space("Training Room", "2nd Floor", SpaceType.TRAINING_ROOM, 30, new BigDecimal("45.00")));
        }

        // Set items to the table
        spacesTable.setItems(spaces);
    }

    // Method to add a new space
    public void addSpace(Space space) {
        spaces.add(space);
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
}