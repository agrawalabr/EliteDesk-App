package com.elitedesk.service;

import com.elitedesk.config.AppConfig;
import com.elitedesk.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String currentToken = null;
    private static User currentUser = null;

    public static boolean isAuthenticated() {
        return currentToken != null;
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentToken = null;
        currentUser = null;
    }

    public static CompletableFuture<LoginResponse> login(String email, String password) {
        try {
            String json = objectMapper.writeValueAsString(new LoginRequest(email, password));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            if (response.statusCode() == 200) {
                                LoginResponse loginResponse = objectMapper.readValue(response.body(),
                                        LoginResponse.class);
                                currentToken = loginResponse.getToken();
                                currentUser = loginResponse.getUser();
                                return loginResponse;
                            } else {
                                return new LoginResponse(null, null, "Invalid credentials");
                            }
                        } catch (Exception e) {
                            return new LoginResponse(null, null, "Error processing login response");
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new LoginResponse(null, null, "Error creating login request"));
        }
    }

    public static CompletableFuture<RegistrationResponse> register(String email, String password, String name) {
        try {
            String json = objectMapper.writeValueAsString(new RegistrationRequest(email, password, name));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            if (response.statusCode() == 201) {
                                return objectMapper.readValue(response.body(), RegistrationResponse.class);
                            } else {
                                return new RegistrationResponse("Registration failed: " + response.body());
                            }
                        } catch (Exception e) {
                            return new RegistrationResponse("Error processing registration response");
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new RegistrationResponse("Error creating registration request"));
        }
    }

    private static class LoginRequest {
        private final String email;
        private final String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    private static class RegistrationRequest {
        private final String email;
        private final String password;
        private final String name;

        public RegistrationRequest(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }
    }

    public static class LoginResponse {
        private final String token;
        private final User user;
        private final String error;

        public LoginResponse(String token, User user, String error) {
            this.token = token;
            this.user = user;
            this.error = error;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return token != null && error == null;
        }
    }

    public static class RegistrationResponse {
        private final String error;

        public RegistrationResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return error == null;
        }
    }
}