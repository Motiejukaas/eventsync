package com.motiejus.eventsync.event;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Create event")
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody @Valid EventRequestDTO eventRequestDTO) {
        EventResponseDTO eventResponseDTO = eventService.createEvent(eventRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponseDTO);
    }

    @Operation(summary = "List all events")
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<EventResponseDTO> events = eventService.getEvents();

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }
}
