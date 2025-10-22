package com.motiejus.eventsync.sentiment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class SentimentService {

    private static final String MODEL_URL =
            "https://router.huggingface.co/hf-inference/models/cardiffnlp/twitter-roberta-base-sentiment-latest";

    private final String hfToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SentimentService(@Value("${huggingface.token}") String hfToken, ObjectMapper objectMapper) {
        this.hfToken = hfToken;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public String analyzeSentiment(String text) {
        try {
            Map<String, Object> payload = Map.of("inputs", text);
            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODEL_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Authorization", "Bearer " + hfToken)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("Hugging Face API error: " + response.statusCode());

            JsonNode root = objectMapper.readTree(response.body());

            return root.get(0).get(0).get("label").asText();
        } catch (Exception e) {
            throw new RuntimeException("Sentiment analysis failed", e);
        }
    }
}

