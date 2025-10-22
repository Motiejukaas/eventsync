package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.event.Event;
import com.motiejus.eventsync.event.EventService;
import com.motiejus.eventsync.sentiment.SentimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventService eventService;
    private final SentimentService sentimentService;

    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, UUID eventId) {
        Feedback feedback = mapToEntity(
                feedbackRequestDTO,
                sentimentService.analyzeSentiment(feedbackRequestDTO.getMessage()),
                eventService.getEventById(eventId)
        );
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return mapToDto(savedFeedback);
    }

    public List<FeedbackResponseDTO> getFeedbacks(UUID eventId) {
        return feedbackRepository.getFeedbackByEvent(eventService.getEventById(eventId)).stream()
                .map(this::mapToDto)
                .toList();
    }

    //Mappers
    private Feedback mapToEntity(FeedbackRequestDTO feedbackRequestDTO, String sentiment, Event event) {
        Feedback feedback = new Feedback();
        feedback.setMessage(feedbackRequestDTO.getMessage());
        feedback.setSentiment(sentiment);
        feedback.setEvent(event);
        return feedback;
    }

    private FeedbackResponseDTO mapToDto(Feedback feedback) {
        FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO();
        feedbackResponseDTO.setId(feedback.getUuid());
        feedbackResponseDTO.setMessage(feedback.getMessage());
        feedbackResponseDTO.setSentiment(feedback.getSentiment());
        feedbackResponseDTO.setCreatedAt(feedback.getCreatedAt());
        feedbackResponseDTO.setEventId(feedback.getEvent().getId());
        return feedbackResponseDTO;
    }
}
