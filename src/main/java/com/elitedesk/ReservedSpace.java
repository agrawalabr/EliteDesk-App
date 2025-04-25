package com.elitedesk;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservedSpace extends Space {
    private final ObjectProperty<LocalDateTime> reservationDate;
    private final IntegerProperty duration;

    public ReservedSpace(String name, String location, SpaceType type, Integer capacity,
            BigDecimal pricePerHour, LocalDateTime reservationDate, Integer duration) {
        super(name, location, type, capacity, pricePerHour);
        this.reservationDate = new SimpleObjectProperty<>(reservationDate);
        this.duration = new SimpleIntegerProperty(duration);
    }

    // Getters for properties
    public ObjectProperty<LocalDateTime> reservationDateProperty() {
        return reservationDate;
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    // Getters for values
    public LocalDateTime getReservationDate() {
        return reservationDate.get();
    }

    public Integer getDuration() {
        return duration.get();
    }

    // Setters
    public void setReservationDate(LocalDateTime value) {
        reservationDate.set(value);
    }

    public void setDuration(Integer value) {
        duration.set(value);
    }
}