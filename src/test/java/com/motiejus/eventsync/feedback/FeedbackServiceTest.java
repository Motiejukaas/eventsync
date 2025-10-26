package com.motiejus.eventsync.feedback;

import com.motiejus.eventsync.common.enums.SentimentType;
import com.motiejus.eventsync.event.Event;
import com.motiejus.eventsync.event.EventService;
import com.motiejus.eventsync.sentiment.SentimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventService eventService;

    @Mock
    private SentimentService sentimentService;

    @InjectMocks
    private FeedbackService feedbackService;

    private Event event;
    private UUID eventId;

    @BeforeEach
    void setup() {
        eventId = UUID.randomUUID();
        event = new Event();
        event.setId(eventId);
        event.setFeedbacks(new java.util.ArrayList<>());
    }

    @Test
    void createFeedback_ShouldSaveFeedbackAndUpdateCounts() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setMessage("Great event!");

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(sentimentService.analyzeSentiment(request.getMessage())).thenReturn(SentimentType.POSITIVE);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(inv -> inv.getArgument(0));

        FeedbackResponseDTO response = feedbackService.createFeedback(request, eventId);

        assertNotNull(response);
        assertEquals(request.getMessage(), response.getMessage());
        assertEquals(SentimentType.POSITIVE, response.getSentiment());
        assertEquals(eventId, response.getEventId());

        verify(eventService).getEventById(eventId);
        verify(sentimentService).analyzeSentiment(request.getMessage());
        verify(feedbackRepository).save(any(Feedback.class));
        verify(feedbackRepository).flush();
    }

    @Test
    void createFeedback_ShouldTriggerSummary_WhenIntervalMatches() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setMessage("Amazing!");
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(sentimentService.analyzeSentiment(anyString())).thenReturn(SentimentType.POSITIVE);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sentimentService.summariseSentiments(event)).thenReturn("Summary generated");

        FeedbackResponseDTO response = feedbackService.createFeedback(request, eventId);

        assertNotNull(response);
        assertEquals(SentimentType.POSITIVE, response.getSentiment());
        assertEquals("Summary generated", event.getFeedbackSentimentSummary());
        verify(sentimentService).summariseSentiments(event);
    }

    @Test
    void getFeedbacks_ShouldMapEntitiesToDtos() {
        Feedback f1 = new Feedback();
        f1.setUuid(UUID.randomUUID());
        f1.setMessage("Good");
        f1.setSentiment(SentimentType.POSITIVE);
        f1.setCreatedAt(LocalDateTime.now());
        f1.setEvent(event);

        Feedback f2 = new Feedback();
        f2.setUuid(UUID.randomUUID());
        f2.setMessage("Bad");
        f2.setSentiment(SentimentType.NEGATIVE);
        f2.setCreatedAt(LocalDateTime.now());
        f2.setEvent(event);

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(feedbackRepository.getFeedbackByEvent(event)).thenReturn(List.of(f1, f2));

        List<FeedbackResponseDTO> responses = feedbackService.getFeedbacks(eventId);

        assertEquals(2, responses.size());
        assertEquals("Good", responses.get(0).getMessage());
        assertEquals(SentimentType.POSITIVE, responses.get(0).getSentiment());
        assertEquals(SentimentType.NEGATIVE, responses.get(1).getSentiment());
        verify(feedbackRepository).getFeedbackByEvent(event);
    }
}
