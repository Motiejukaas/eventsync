package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.common.enums.SentimentType;
import com.motiejus.eventsync.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"feedback\"")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "message", nullable = false, length = 5000)
    private String message;

    @Column(name = "sentiment")
    private SentimentType sentiment;

    @Column(name = "created_at",  updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
