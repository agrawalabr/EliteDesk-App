package com.elitedesk.service;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private String email;
    private String name;
    private String role;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSession(String token, String email, String name, String role) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void clearSession() {
        token = null;
        email = null;
        name = null;
        role = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }
}