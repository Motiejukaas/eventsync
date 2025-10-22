package com.motiejus.eventsync.event;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        Event event = mapToEntity(eventRequestDTO);
        Event savedEvent = eventRepository.save(event);
        return mapToDTO(savedEvent);
    }

    public List<EventResponseDTO> getEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Event mapToEntity(EventRequestDTO eventRequestDTO) {
        Event event = new Event();
        event.setTitle(eventRequestDTO.getTitle());
        event.setDescription(eventRequestDTO.getDescription());
        return event;
    }

    private EventResponseDTO mapToDTO(Event event) {
        EventResponseDTO eventResponseDTO = new EventResponseDTO();
        eventResponseDTO.setId(event.getId());
        eventResponseDTO.setTitle(event.getTitle());
        eventResponseDTO.setDescription(event.getDescription());
        return eventResponseDTO;
    }
}
