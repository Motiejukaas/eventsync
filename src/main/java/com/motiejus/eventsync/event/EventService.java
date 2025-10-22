package com.motiejus.eventsync.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<EventResponseDTO> getEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Event getEventById(UUID eventId) {
        return eventRepository.findById(eventId).orElse(null);
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
        return eventResponseDTO;
    }
}
