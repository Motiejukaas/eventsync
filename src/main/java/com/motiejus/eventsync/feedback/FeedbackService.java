package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.common.enums.SentimentType;
import com.motiejus.eventsync.event.Event;
import com.motiejus.eventsync.event.EventService;
import com.motiejus.eventsync.sentiment.SentimentService;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, UUID eventId) {
        Event currentEvent = eventService.getEventById(eventId);
        SentimentType sentiment = sentimentService.analyzeSentiment(feedbackRequestDTO.getMessage());

        Feedback feedback = mapToEntity(feedbackRequestDTO, sentiment, currentEvent);
        currentEvent.getFeedbacks().add(feedback);
        feedback.setEvent(currentEvent);

        Feedback savedFeedback = feedbackRepository.save(feedback);
        feedbackRepository.flush();

        //For efficiency. To not have multiple currentEvent calls.
        int positiveFeedbackCount = currentEvent.getPositiveFeedbackSentimentCount();
        int neutralFeedbackCount = currentEvent.getNeutralFeedbackSentimentCount();
        int negativeFeedbackCount = currentEvent.getNegativeFeedbackSentimentCount();

        switch (sentiment) {
            case POSITIVE -> currentEvent.setPositiveFeedbackSentimentCount(
                    positiveFeedbackCount + 1
            );
            case NEUTRAL -> currentEvent.setNeutralFeedbackSentimentCount(
                    neutralFeedbackCount + 1
            );
            case NEGATIVE -> currentEvent.setNegativeFeedbackSentimentCount(
                    negativeFeedbackCount + 1
            );
        }

        // +1 to account for the new feedback
        triggerSummary(positiveFeedbackCount + neutralFeedbackCount + negativeFeedbackCount + 1,
                currentEvent);

        return mapToDto(savedFeedback);
    }

    private void triggerSummary(int total, Event currentEvent) {
        //alternative
        //int triggerInterval = Math.max(1, (int) (5 * Math.log(total + 1)));

        int triggerInterval = Math.max(1, (int)Math.sqrt(total));

        if ((total % triggerInterval) == 0) {
            currentEvent.setFeedbackSentimentSummary(sentimentService.summariseSentiments(currentEvent));
        }
    }

    public List<FeedbackResponseDTO> getFeedbacks(UUID eventId) {
        return feedbackRepository.getFeedbackByEvent(eventService.getEventById(eventId)).stream()
                .map(this::mapToDto)
                .toList();
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
