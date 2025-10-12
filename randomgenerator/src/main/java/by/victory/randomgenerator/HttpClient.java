package by.victory.randomgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private final String url;
    private final java.net.http.HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClient() {
        this.url = PropertyReader.getProperty("url");
        this.httpClient = java.net.http.HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void send(Measurement measurement) {
        try {
            String jsonMeasurement = objectMapper.writeValueAsString(measurement);
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonMeasurement))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                logger.warn("HTTP error: {} - {}", response.statusCode(), response.body());
            }
        } catch (IOException e) {
            logger.error("Network error sending metrics", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("HTTP request interrupted");
        }
    }
}
