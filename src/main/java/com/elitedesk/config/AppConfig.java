package com.elitedesk.config;

public class AppConfig {
    public static final String API_BASE_URL = "http://localhost:8080/api";

    private static String apiUrl = System.getenv("ELITEDESK_API_URL");

    static {
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = API_BASE_URL;
        }
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static String getSpacesEndpoint() {
        return apiUrl + "/spaces";
    }

    public static String getLoginEndpoint() {
        return apiUrl + "/auth/login";
    }

    public static String getRegisterEndpoint() {
        return apiUrl + "/auth/register";
    }
}