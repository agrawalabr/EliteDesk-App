package com.elitedesk.service;

import com.elitedesk.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

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
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        } else {
            throw new RuntimeException("Failed to fetch spaces: " + response.statusCode() + " - " + response.body());
        }
    }
}