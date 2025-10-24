package com.motiejus.eventsync.event;

import lombok.Data;

import java.util.UUID;

@Data
public class EventSentimentBreakdownDTO {
    UUID eventId;
    int totalFeedbacks;
    int positive;
    int neutral;
    int negative;
    double positivePercent;
    double neutralPercent;
    double negativePercent;
}
