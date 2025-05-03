package by.victory.randomgenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException, JSONException {
        Random random = new Random();

        JSONObject requestBody = new JSONObject();

        HttpClient client = HttpClient.newHttpClient();

        for (; ; ) {
            requestBody.put("amperage", random.nextInt(100 - 1) + 1);
            requestBody.put("voltage", random.nextInt(100 - 1) + 1);

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/measurement"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Thread.sleep(400);
        }
    }
}
