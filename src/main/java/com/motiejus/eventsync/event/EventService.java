package com.motiejus.eventsync.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        Event event = mapToEntity(eventRequestDTO);
        Event savedEvent = eventRepository.save(event);
        return mapToDto(savedEvent);
    }


    public void deleteEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        eventRepository.delete(event);
    }

    public List<EventResponseDTO> getEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Event getEventById(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
    }

    public Event getEventWithFeedbacksById(UUID eventId) {
        return eventRepository.getEventWithFeedbacksById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
    }

    @Transactional
    public void updateFeedbackSummary(Event event, String summary) {
        event.setFeedbackSentimentSummary(summary);
        eventRepository.save(event);
    }

    public EventSentimentBreakdownDTO getSentimentBreakdown(UUID eventId) {
        Event event = getEventById(eventId);
        int total = event.getPositiveFeedbackSentimentCount()
                + event.getNeutralFeedbackSentimentCount()
                + event.getNegativeFeedbackSentimentCount();

        double positivePercent = total == 0 ? 0 : (event.getPositiveFeedbackSentimentCount() * 100.0 / total);
        double neutralPercent  = total == 0 ? 0 : (event.getNeutralFeedbackSentimentCount() * 100.0 / total);
        double negativePercent = total == 0 ? 0 : (event.getNegativeFeedbackSentimentCount() * 100.0 / total);

        return mapToDto(event, total, positivePercent, neutralPercent, negativePercent);
    }

    public String getAISummary(UUID eventId) {
        Event event = getEventById(eventId);
        return event.getFeedbackSentimentSummary();
    }

    // Mappers
    private Event mapToEntity(EventRequestDTO eventRequestDTO) {
        Event event = new Event();
        event.setTitle(eventRequestDTO.getTitle());
        event.setDescription(eventRequestDTO.getDescription());
        return event;
    }

    private EventResponseDTO mapToDto(Event event) {
        EventResponseDTO eventResponseDTO = new EventResponseDTO();
        eventResponseDTO.setId(event.getId());
        eventResponseDTO.setTitle(event.getTitle());
        eventResponseDTO.setDescription(event.getDescription());
        eventResponseDTO.setPositiveFeedbackSentimentCount(event.getPositiveFeedbackSentimentCount());
        eventResponseDTO.setNeutralFeedbackSentimentCount(event.getNeutralFeedbackSentimentCount());
        eventResponseDTO.setNegativeFeedbackSentimentCount(event.getNegativeFeedbackSentimentCount());
        eventResponseDTO.setFeedbackSentimentSummary(event.getFeedbackSentimentSummary());
        return eventResponseDTO;
    }

    private EventSentimentBreakdownDTO mapToDto(Event event, int total, double positivePercent, double neutralPercent, double negativePercent) {
        EventSentimentBreakdownDTO eventSentimentBreakdownDTO = new EventSentimentBreakdownDTO();
        eventSentimentBreakdownDTO.setEventId(event.getId());
        eventSentimentBreakdownDTO.setTotalFeedbacks(total);
        eventSentimentBreakdownDTO.setPositive(event.getPositiveFeedbackSentimentCount());
        eventSentimentBreakdownDTO.setNeutral(event.getNeutralFeedbackSentimentCount());
        eventSentimentBreakdownDTO.setNegative(event.getNegativeFeedbackSentimentCount());
        eventSentimentBreakdownDTO.setPositivePercent(positivePercent);
        eventSentimentBreakdownDTO.setNeutralPercent(neutralPercent);
        eventSentimentBreakdownDTO.setNegativePercent(negativePercent);
        return eventSentimentBreakdownDTO;
    }
}
