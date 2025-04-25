package com.elitedesk;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class Space {
    private final LongProperty id;
    private final StringProperty name;
    private final StringProperty location;
    private final ObjectProperty<SpaceType> type;
    private final IntegerProperty capacity;
    private final ObjectProperty<BigDecimal> pricePerHour;

    public Space(String name, String location, SpaceType type, Integer capacity, BigDecimal pricePerHour) {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty(name);
        this.location = new SimpleStringProperty(location);
        this.type = new SimpleObjectProperty<>(type);
        this.capacity = new SimpleIntegerProperty(capacity);
        this.pricePerHour = new SimpleObjectProperty<>(pricePerHour);
    }

    // Getters for properties
    public LongProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public ObjectProperty<SpaceType> typeProperty() {
        return type;
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    public ObjectProperty<BigDecimal> pricePerHourProperty() {
        return pricePerHour;
    }

    // Getters for values
    public Long getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getLocation() {
        return location.get();
    }

    public SpaceType getType() {
        return type.get();
    }

    public Integer getCapacity() {
        return capacity.get();
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour.get();
    }

    // Setters
    public void setId(Long value) {
        id.set(value);
    }

    public void setName(String value) {
        name.set(value);
    }

    public void setLocation(String value) {
        location.set(value);
    }

    public void setType(SpaceType value) {
        type.set(value);
    }

    public void setCapacity(Integer value) {
        capacity.set(value);
    }

    public void setPricePerHour(BigDecimal value) {
        pricePerHour.set(value);
    }
}