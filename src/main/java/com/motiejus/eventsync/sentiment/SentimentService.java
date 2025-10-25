package com.motiejus.eventsync.sentiment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motiejus.eventsync.common.enums.SentimentType;
import com.motiejus.eventsync.event.Event;
import com.motiejus.eventsync.feedback.Feedback;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SentimentService {

    private static final String MODEL_URL_SENTIMENT =
            "https://router.huggingface.co/hf-inference/models/cardiffnlp/twitter-roberta-base-sentiment-latest";

    private static final String MODEL_URL_LLM = "https://router.huggingface.co/v1/chat/completions";

    private final String hfToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String systemPrompt = "System:\n" +
            "Summarize event reviews into a neutral, concise paragraph highlighting the main positives and negatives. Do not invent facts. No lists, tables, headings, quotes, emojis, or metadata.\n" +
            "Write a single summary of the key points in 2–4 sentences (if there are very few reviews, 1–2 sentences). Output must be one plain paragraph only.\n" +
            "\n" +
            "User:\n" +
            "You are given these reviews: ";

    public SentimentService(@Value("${huggingface.token}") String hfToken, ObjectMapper objectMapper) {
        this.hfToken = hfToken;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public SentimentType analyzeSentiment(String text) {
        try {
            Map<String, Object> payload = Map.of("inputs", text);
            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODEL_URL_SENTIMENT))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Authorization", "Bearer " + hfToken)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("Hugging Face API error: " + response.statusCode());

            JsonNode root = objectMapper.readTree(response.body());

            return SentimentType.valueOf(root.get(0).get(0).get("label").asText().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Sentiment analysis failed", e);
        }
    }

    public String summariseSentiments(Event event) {
        try {
            // join all feedback messages into one text block
            String allFeedbacks = event.getFeedbacks().stream()
                    .map(Feedback::getMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            Map<String, Object> payload = Map.of(
                    "model", "openai/gpt-oss-20b:groq",
                    "stream", false,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", systemPrompt + allFeedbacks))
            );


            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODEL_URL_LLM))
                    .header("Authorization", "Bearer " + hfToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("Hugging Face API error: " + response.statusCode());

            JsonNode root = objectMapper.readTree(response.body());
            // extract text: response.output[0].content[0].text
            String summary = root.path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return summary;

        } catch (Exception e) {
            throw new RuntimeException("Failed to summarize feedbacks", e);
        }
    }

    //openai
    public String summariseSentimentsOpenAi(Event event) {
        try {
            String allFeedbacks = event.getFeedbacks().stream()
                    .map(Feedback::getMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            if (allFeedbacks.isBlank()) {
                return "No feedbacks available to summarize.";
            }

            String prompt = systemPrompt + "\n" + allFeedbacks;

            OpenAIClient client = OpenAIOkHttpClient.fromEnv();

            ResponseCreateParams params = ResponseCreateParams.builder()
                    .input(prompt)
                    .model(ChatModel.GPT_5_NANO)
                    .build();

            Response response = client.responses().create(params);

            return response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(msg -> msg.content().stream())
                    .flatMap(c -> c.outputText().stream())
                    .map(ResponseOutputText::text)
                    .collect(Collectors.joining("\n")).trim();
        } catch (Exception e) {
            throw new RuntimeException("OpenAI summarization failed", e);
        }
    }

}

