package com.elitedesk.service;

import com.elitedesk.config.AppConfig;
import com.elitedesk.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

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
                                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                                boolean success = (boolean) responseMap.get("success");
                                String message = (String) responseMap.get("message");
                                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

                                return new LoginResponse(success, message, data, null);
                            } else {
                                return new LoginResponse(false, null, null, "Invalid credentials");
                            }
                        } catch (Exception e) {
                            return new LoginResponse(false, null, null, "Error processing login response");
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture
                    .completedFuture(new LoginResponse(false, null, null, "Error creating login request"));
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
                                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                                boolean success = (boolean) responseMap.get("success");
                                String message = (String) responseMap.get("message");
                                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

                                return new RegistrationResponse(success, message, data, null);
                            } else {
                                return new RegistrationResponse(false, null, null, "Registration failed");
                            }
                        } catch (Exception e) {
                            return new RegistrationResponse(false, null, null,
                                    "Error processing registration response");
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    new RegistrationResponse(false, null, null, "Error creating registration request"));
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
        private boolean success;
        private String message;
        private Map<String, Object> data;
        private String error;

        public LoginResponse(boolean success, String message, Map<String, Object> data, String error) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public String getError() {
            return error;
        }
    }

    public static class RegistrationResponse {
        private boolean success;
        private String message;
        private Map<String, Object> data;
        private String error;

        public RegistrationResponse(boolean success, String message, Map<String, Object> data, String error) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public String getError() {
            return error;
        }
    }
}