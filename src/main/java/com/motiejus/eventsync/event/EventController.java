package com.motiejus.eventsync.event;

import com.motiejus.eventsync.feedback.FeedbackRequestDTO;
import com.motiejus.eventsync.feedback.FeedbackResponseDTO;
import com.motiejus.eventsync.feedback.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final FeedbackService feedbackService;

    @Operation(summary = "Create event")
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody @Valid EventRequestDTO eventRequestDTO) {
        EventResponseDTO eventResponseDTO = eventService.createEvent(eventRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponseDTO);
    }

    @Operation(summary = "Delete event")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all events")
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<EventResponseDTO> events = eventService.getEvents();

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @Operation(summary = "Submit feedback")
    @PostMapping("/{eventId}/feedback")
    public ResponseEntity<FeedbackResponseDTO> submitFeedback(@RequestBody @Valid FeedbackRequestDTO feedbackRequestDTO, @PathVariable UUID eventId) {
        FeedbackResponseDTO feedbackResponseDTO = feedbackService.createFeedback(feedbackRequestDTO, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(feedbackResponseDTO);
    }

    @Operation(summary = "Get sentiment breakdown (counts and percentages)")
    @GetMapping("/{eventId}/summary")
    public ResponseEntity<EventSentimentBreakdownDTO> getSentimentBreakdown(@PathVariable UUID eventId) {
        EventSentimentBreakdownDTO breakdown = eventService.getSentimentBreakdown(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(breakdown);
    }

    @Operation(summary = "Get AI-generated feedback summary")
    @GetMapping("/{eventId}/summary/ai")
    public ResponseEntity<String> getAISummary(@PathVariable UUID eventId) {
        String summary = eventService.getAISummary(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(summary);
    }
}
