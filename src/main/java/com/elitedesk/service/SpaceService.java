package com.elitedesk.service;

import com.elitedesk.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import com.elitedesk.Space;
import com.elitedesk.service.SessionManager;

public class SpaceService {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Map<String, Object>> getSpaces() throws Exception {
        // Get the authentication token if user is logged in
        String token = SessionManager.getInstance().getToken();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getSpacesEndpoint()))
                .GET();

        // Add authorization header if token exists
        if (token != null && !token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
            Object success = responseMap.get("success");
            if (success != null && Boolean.TRUE.equals(success)) {
                return (List<Map<String, Object>>) responseMap.get("data");
            } else {
                throw new RuntimeException("API returned success: false - " + responseMap.get("message"));
            }
        } else {
            throw new RuntimeException("Failed to fetch spaces: " + response.statusCode() + " - " + response.body());
        }
    }

    public static Map<String, Object> createSpace(Space space) throws Exception {
        // Create request body
        Map<String, Object> requestBody = Map.of(
                "name", space.getName(),
                "location", space.getLocation(),
                "type", space.getType().toString(),
                "capacity", space.getCapacity(),
                "pricePerHour", space.getPricePerHour());

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // Get the authentication token from the SessionManager
        String token = SessionManager.getInstance().getToken();

        // Debug logging
        System.out.println("=== DEBUG: Create Space Request ===");
        System.out.println("Token present: " + (token != null && !token.isEmpty()));
        if (token != null) {
            System.out.println("Token length: " + token.length());
            System.out.println("Token prefix: " + (token.length() > 10 ? token.substring(0, 10) + "..." : token));
        }
        System.out.println("User email: " + SessionManager.getInstance().getEmail());
        System.out.println("User role: " + SessionManager.getInstance().getRole());
        System.out.println("Request URL: " + AppConfig.getSpacesEndpoint());
        System.out.println("Request body: " + jsonBody);
        System.out.println("================================");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication required. Please log in.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getSpacesEndpoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Debug: print request headers
        System.out.println("Request headers:");
        request.headers().map().forEach((key, values) -> {
            System.out.println("  " + key + ": " + String.join(", ", values));
        });

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Debug response
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() == 201) {
            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);

            // Print the response for debugging
            System.out.println("API Response: " + response.body());

            // Check if success field exists and is true
            Object success = responseMap.get("success");
            if (success != null && Boolean.TRUE.equals(success)) {
                return (Map<String, Object>) responseMap.get("data");
            } else {
                // If there's no success field but we have data, return it anyway
                Object data = responseMap.get("data");
                if (data != null && data instanceof Map) {
                    return (Map<String, Object>) data;
                }
                // Otherwise return the whole response if there's no data field
                if (responseMap.containsKey("id")) {
                    return responseMap;
                }
                throw new RuntimeException("API response missing success or data field: " + response.body());
            }
        } else {
            throw new RuntimeException("Failed to create space: " + response.statusCode() + " - " + response.body());
        }
    }
}