package com.motiejus.eventsync.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventRequestDTO {
    @NotBlank
    @Size(min = 1, max = 1000, message = "Event title must not be empty or exceed 1000 characters.")
    private String title;

    @NotBlank
    @Size(min = 1, message = "Event description must not be empty.")
    private String description;
}
