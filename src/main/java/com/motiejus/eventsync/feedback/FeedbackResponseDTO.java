package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.common.enums.SentimentType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeedbackResponseDTO {
    private UUID id;
    private String message;
    private SentimentType sentiment;
    private LocalDateTime createdAt;
    private UUID eventId;

}
