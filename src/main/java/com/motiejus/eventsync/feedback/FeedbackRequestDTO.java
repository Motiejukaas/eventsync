package com.motiejus.eventsync.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class FeedbackRequestDTO {
    @NotBlank(message = "Feedback message must not be empty.")
    @Size(max = 5000, message = "Feedback message must not exceed 5000 characters.")
    private String message;
}
