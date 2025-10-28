package com.motiejus.eventsync.feedback;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Operation(summary = "Get feedbacks by event ID")
    @GetMapping("/{eventId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbacksByEventId(@PathVariable UUID eventId) {
        List<FeedbackResponseDTO> feedbacks = feedbackService.getFeedbacks(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(feedbacks);
    }
}
