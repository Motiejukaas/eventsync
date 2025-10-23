package com.motiejus.eventsync.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"event\"")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "positive_feedback_sentiment_count", nullable = false)
    private int positiveFeedbackSentimentCount = 0;

    @Column(name = "neutral_feedback_sentiment_count", nullable = false)
    private int neutralFeedbackSentimentCount = 0;

    @Column(name = "negative_feedback_sentiment_count", nullable = false)
    private int negativeFeedbackSentimentCount = 0;

    @Column(name = "feedback_sentiment_summary")
    private String feedbackSentimentSummary;
}
