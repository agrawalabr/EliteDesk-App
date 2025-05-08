package com.elitedesk.service;

import com.elitedesk.config.AppConfig;
import com.elitedesk.model.Reservation;
import com.elitedesk.model.TimeSlot;
import com.elitedesk.service.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;

public class ReservationService {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static CompletableFuture<List<Reservation>> getReservationsForSpace(long spaceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/spaces/" + spaceId + "/reservations"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            if (response.statusCode() == 200) {
                                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                                List<Map<String, Object>> reservationsData = (List<Map<String, Object>>) responseMap
                                        .get("data");

                                return reservationsData.stream()
                                        .map(data -> new Reservation(
                                                ((Number) data.get("id")).longValue(),
                                                spaceId,
                                                ((Number) data.get("userId")).toString(),
                                                LocalDateTime.parse((String) data.get("startTime"), formatter),
                                                LocalDateTime.parse((String) data.get("endTime"), formatter),
                                                (String) data.get("spaceName"),
                                                (String) data.get("spaceLocation"),
                                                (String) data.get("status")))
                                        .collect(Collectors.toList());
                            }
                            return List.of();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return List.of();
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }

    public static CompletableFuture<Boolean> createReservation(Reservation reservation) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "spaceId", reservation.getSpaceId(),
                    "userId", reservation.getUserId(),
                    "startTime", reservation.getStartTime().format(formatter),
                    "endTime", reservation.getEndTime().format(formatter));

            String json = objectMapper.writeValueAsString(requestBody);
            System.out.println("Creating reservation with request: " + json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/reservations/email"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("Reservation API Response Status: " + response.statusCode());
                        System.out.println("Reservation API Response Body: " + response.body());
                        if (response.statusCode() == 500) {
                            System.err.println("Server error response: " + response.body());
                        }
                        return response.statusCode() == 201;
                    });
        } catch (Exception e) {
            System.err.println("Error creating reservation: " + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    public static CompletableFuture<Boolean> updateReservation(Reservation reservation) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "startTime", reservation.getStartTime().format(formatter),
                    "endTime", reservation.getEndTime().format(formatter),
                    "title", reservation.getTitle(),
                    "description", reservation.getDescription());

            String json = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/reservations/" + reservation.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 200);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }

    public static CompletableFuture<Boolean> deleteReservation(long reservationId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/reservations/" + reservationId))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 200);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }

    public static CompletableFuture<List<Reservation>> getUserReservations() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            AppConfig.API_BASE_URL + "/reservations/user/email/"
                                    + SessionManager.getInstance().getEmail()))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            if (response.statusCode() == 200) {
                                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                                List<Map<String, Object>> reservationsData = (List<Map<String, Object>>) responseMap
                                        .get("data");

                                return reservationsData.stream()
                                        .map(data -> new Reservation(
                                                ((Number) data.get("id")).longValue(),
                                                ((Number) data.get("spaceId")).longValue(),
                                                ((Number) data.get("userId")).toString(),
                                                (String) data.get("userName"),
                                                (String) data.get("spaceName"),
                                                (String) data.get("spaceLocation"),
                                                (String) data.get("spaceType"),
                                                LocalDateTime.parse((String) data.get("startTime"), formatter),
                                                LocalDateTime.parse((String) data.get("endTime"), formatter),
                                                (String) data.get("status")))
                                        .collect(Collectors.toList());
                            }
                            return List.of();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return List.of();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(List.of());
        }
    }

    public static CompletableFuture<List<TimeSlot>> getAvailableTimeSlots(Long spaceId, LocalDate date) {
        try {
            String url = String.format("%s/reservations/availability?spaceId=%d&date=%s", AppConfig.API_BASE_URL,
                    spaceId, date);
            System.out.println("Fetching time slots from: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("Time slots API Response Status: " + response.statusCode());
                        System.out.println("Time slots API Response Body: " + response.body());

                        if (response.statusCode() != 200) {
                            throw new RuntimeException("API returned status code: " + response.statusCode());
                        }

                        return response.body();
                    })
                    .thenApply(response -> {
                        try {
                            Map<String, Object> responseMap = objectMapper.readValue(response,
                                    new TypeReference<Map<String, Object>>() {
                                    });

                            if (!responseMap.containsKey("data")) {
                                throw new RuntimeException("API response missing 'data' field: " + response);
                            }

                            List<Map<String, Object>> timeSlotData = (List<Map<String, Object>>) responseMap
                                    .get("data");
                            System.out.println("Found " + timeSlotData.size() + " time slots");

                            List<TimeSlot> timeSlots = new ArrayList<>();
                            for (Map<String, Object> slotData : timeSlotData) {
                                try {
                                    LocalTime startTime = LocalTime.parse((String) slotData.get("startTime"),
                                            timeFormatter);
                                    LocalTime endTime = LocalTime.parse((String) slotData.get("endTime"),
                                            timeFormatter);
                                    boolean available = (boolean) slotData.get("available");
                                    timeSlots.add(new TimeSlot(startTime, endTime, available));
                                } catch (Exception e) {
                                    System.err.println("Error parsing time slot: " + slotData);
                                    e.printStackTrace();
                                }
                            }

                            if (timeSlots.isEmpty()) {
                                System.err.println("No valid time slots found in response");
                            }

                            return timeSlots;
                        } catch (Exception e) {
                            System.err.println("Error parsing API response: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Failed to parse time slots", e);
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Error fetching time slots: " + throwable.getMessage());
                        throwable.printStackTrace();
                        throw new RuntimeException("Failed to fetch time slots", throwable);
                    });
        } catch (Exception e) {
            System.err.println("Error creating time slots request: " + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }
}