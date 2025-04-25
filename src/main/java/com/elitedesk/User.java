package com.elitedesk;

import javafx.beans.property.*;

public class User {
    private final LongProperty id;
    private final StringProperty username;
    private final StringProperty email;
    private final StringProperty password;

    public User(String username, String email, String password) {
        this.id = new SimpleLongProperty();
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
    }

    // Getters for properties
    public LongProperty idProperty() {
        return id;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    // Getters for values
    public Long getId() {
        return id.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getPassword() {
        return password.get();
    }

    // Setters
    public void setId(Long value) {
        id.set(value);
    }

    public void setUsername(String value) {
        username.set(value);
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public void setPassword(String value) {
        password.set(value);
    }
}