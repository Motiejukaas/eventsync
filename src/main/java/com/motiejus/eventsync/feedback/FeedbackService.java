package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.event.Event;
import com.motiejus.eventsync.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventService eventService;


    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, UUID eventId) {
        Feedback feedback = mapToEntity(feedbackRequestDTO, eventService.getEventById(eventId));
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return mapToDto(savedFeedback);
    }

    public List<FeedbackResponseDTO> getFeedbacks(UUID eventId) {
        return feedbackRepository.getFeedbackByEvent(eventService.getEventById(eventId)).stream()
                .map(this::mapToDto)
                .toList();
    }

    //Mappers
    private Feedback mapToEntity(FeedbackRequestDTO feedbackRequestDTO, Event event) {
        Feedback feedback = new Feedback();
        feedback.setMessage(feedbackRequestDTO.getMessage());
        feedback.setEvent(event);

        return feedback;
    }

    private FeedbackResponseDTO mapToDto(Feedback feedback) {
        FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO();
        feedbackResponseDTO.setId(feedback.getUuid());
        feedbackResponseDTO.setMessage(feedback.getMessage());
        feedbackResponseDTO.setCreatedAt(feedback.getCreatedAt());
        feedbackResponseDTO.setEventId(feedback.getEvent().getId());

        return feedbackResponseDTO;
    }
}
