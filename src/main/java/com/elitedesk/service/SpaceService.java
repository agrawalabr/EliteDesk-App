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

public class SpaceService {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Map<String, Object>> getSpaces() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getSpacesEndpoint()))
                .GET()
                .build();

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getSpacesEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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