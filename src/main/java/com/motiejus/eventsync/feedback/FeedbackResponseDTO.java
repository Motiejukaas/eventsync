package com.motiejus.eventsync.feedback;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeedbackResponseDTO {
    private UUID id;
    private String message;
    private LocalDateTime createdAt;
    private UUID eventId;

}
