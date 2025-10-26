package com.motiejus.eventsync.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void createEvent_ShouldReturnSavedEvent() {
        String title = "Ceramics class";
        String description = "Workshop for adults";

        EventRequestDTO request = new EventRequestDTO();
        request.setTitle(title);
        request.setDescription(description);

        Event saved = new Event();
        saved.setId(UUID.randomUUID());
        saved.setTitle(title);
        saved.setDescription(description);

        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        EventResponseDTO result = eventService.createEvent(request);

        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getEvents_ShouldReturnMappedDtos() {
        String title1 = "A";
        String title2 = "B";

        Event e1 = new Event();
        e1.setId(UUID.randomUUID());
        e1.setTitle(title1);
        e1.setDescription("descA");

        Event e2 = new Event();
        e2.setId(UUID.randomUUID());
        e2.setTitle(title2);
        e2.setDescription("descB");

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EventResponseDTO> result = eventService.getEvents();

        assertEquals(2, result.size());
        assertEquals(title1, result.get(0).getTitle());
        assertEquals(title2, result.get(1).getTitle());
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenExists() {
        String title = "Event";

        UUID id = UUID.randomUUID();
        Event e = new Event();
        e.setId(id);
        e.setTitle(title);

        when(eventRepository.findById(id)).thenReturn(Optional.of(e));

        Event result = eventService.getEventById(id);

        assertEquals(id, result.getId());
        assertEquals(title, result.getTitle());
    }

    @Test
    void getEventById_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> eventService.getEventById(id));
    }

    @Test
    void getSentimentBreakdown_ShouldCalculatePercentagesCorrectly() {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setPositiveFeedbackSentimentCount(3);
        event.setNeutralFeedbackSentimentCount(1);
        event.setNegativeFeedbackSentimentCount(1);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        EventSentimentBreakdownDTO dto = eventService.getSentimentBreakdown(event.getId());

        assertEquals(5, dto.getTotalFeedbacks());
        assertEquals(60.0, dto.getPositivePercent(), 0.01);
        assertEquals(20.0, dto.getNeutralPercent(), 0.01);
        assertEquals(20.0, dto.getNegativePercent(), 0.01);
    }

    @Test
    void getSentimentBreakdown_ShouldHandleZeroTotal() {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        EventSentimentBreakdownDTO dto = eventService.getSentimentBreakdown(event.getId());

        assertEquals(0, dto.getTotalFeedbacks());
        assertEquals(0.0, dto.getPositivePercent());
        assertEquals(0.0, dto.getNeutralPercent());
        assertEquals(0.0, dto.getNegativePercent());
    }

    @Test
    void getAISummary_ShouldReturnSummaryFromEvent() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setFeedbackSentimentSummary("AI summary text");

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        String summary = eventService.getAISummary(id);

        assertEquals("AI summary text", summary);
    }
}
