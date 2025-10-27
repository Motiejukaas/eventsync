package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.common.enums.SentimentType;
import com.motiejus.eventsync.event.Event;
import com.motiejus.eventsync.event.EventService;
import com.motiejus.eventsync.sentiment.SentimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventService eventService;
    private final SentimentService sentimentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, UUID eventId) {
        Event currentEvent = eventService.getEventById(eventId);
        SentimentType sentiment = sentimentService.analyzeSentiment(feedbackRequestDTO.getMessage());

        Feedback feedback = mapToEntity(feedbackRequestDTO, sentiment, currentEvent);
        //currentEvent.getFeedbacks().add(feedback);
        feedback.setEvent(currentEvent);

        Feedback savedFeedback = feedbackRepository.save(feedback);
        //feedbackRepository.flush();

        updateCounters(currentEvent, sentiment);

        applicationEventPublisher.publishEvent(new FeedbackCreatedEvent(eventId));

        return mapToDto(savedFeedback);
    }

    public List<FeedbackResponseDTO> getFeedbacks(UUID eventId) {
        return feedbackRepository.getFeedbackByEvent(eventService.getEventById(eventId)).stream()
                .map(this::mapToDto)
                .toList();
    }

    private void updateCounters(Event event, SentimentType sentiment) {
        switch (sentiment) {
            case POSITIVE -> event.setPositiveFeedbackSentimentCount(event.getPositiveFeedbackSentimentCount() + 1);
            case NEUTRAL  -> event.setNeutralFeedbackSentimentCount(event.getNeutralFeedbackSentimentCount() + 1);
            case NEGATIVE -> event.setNegativeFeedbackSentimentCount(event.getNegativeFeedbackSentimentCount() + 1);
        }
    }

    //Mappers
    private Feedback mapToEntity(FeedbackRequestDTO feedbackRequestDTO, SentimentType sentiment, Event event) {
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
