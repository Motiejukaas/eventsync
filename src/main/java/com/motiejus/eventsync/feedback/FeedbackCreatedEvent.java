package com.motiejus.eventsync.feedback;


import lombok.Getter;

import java.util.UUID;

@Getter
public class FeedbackCreatedEvent {
    private final UUID eventId;

    public FeedbackCreatedEvent(UUID eventId) {
        //super(source);
        this.eventId = eventId;
    }
}
