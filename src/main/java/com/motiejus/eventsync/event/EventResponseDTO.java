package com.motiejus.eventsync.event;

import lombok.Data;

import java.util.UUID;

@Data
public class EventResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private int positiveFeedbackSentimentCount;
    private int neutralFeedbackSentimentCount;
    private int negativeFeedbackSentimentCount;
    private String feedbackSentimentSummary;
}
