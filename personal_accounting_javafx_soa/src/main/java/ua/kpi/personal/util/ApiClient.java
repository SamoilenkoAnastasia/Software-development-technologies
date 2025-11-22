package ua.kpi.personal.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/v1";

    public static final ObjectMapper MAPPER = initializeObjectMapper();

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static String authToken;

    private ApiClient() {}

    
    private static ObjectMapper initializeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static void clearAuthToken() {
        authToken = null;
    }

   
    private static HttpRequest.Builder createRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json");

        if (authToken != null && !authToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + authToken);
        }
        return builder;
    }

    
    public static String get(String path, Map<String, String> params) throws IOException, InterruptedException {
        String queryString = "";
        if (params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (sb.length() > 1) sb.append("&");
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                  .append("=")
                  .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            queryString = sb.toString();
        }
        HttpRequest request = createRequestBuilder(path + queryString).GET().build();
        return sendRequest(request);
    }

    
    public static String get(String path) throws IOException, InterruptedException {
        return get(path, null);
    }

    public static String post(String path, Object body) throws IOException, InterruptedException {
        String requestBody = MAPPER.writeValueAsString(body);
        HttpRequest request = createRequestBuilder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return sendRequest(request);
    }

    public static String put(String path, Object body) throws IOException, InterruptedException {
        String requestBody = MAPPER.writeValueAsString(body);
        HttpRequest request = createRequestBuilder(path)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return sendRequest(request);
    }

    public static void delete(String path) throws IOException, InterruptedException {
        HttpRequest request = createRequestBuilder(path).DELETE().build();
        sendRequest(request);
    }

    private static String sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        } else {
            String errorBody = response.body().isEmpty() ? "No response body" : response.body();
            System.err.println("API Call failed: Status " + statusCode + ", Body: " + errorBody);
            throw new RuntimeException("API Call failed: Status " + statusCode);
        }
    }
}
