package com.motiejus.eventsync.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motiejus.eventsync.feedback.FeedbackRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createEvent_AndRetrieve_ShouldWork() throws Exception {
        EventRequestDTO request = new EventRequestDTO();
        request.setTitle("Music Festival");
        request.setDescription("Open-air event in summer");

        String responseJson = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EventResponseDTO created = objectMapper.readValue(responseJson, EventResponseDTO.class);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Music Festival");

        // Now fetch it via GET
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Music Festival"));
    }

    @Test
    void addFeedback_ShouldUpdateSentimentCounters() throws Exception {
        // 1. Create event
        EventRequestDTO eventRequest = new EventRequestDTO();
        eventRequest.setTitle("Tech Conference");
        eventRequest.setDescription("Conference on innovation");

        String eventJson = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EventResponseDTO event = objectMapper.readValue(eventJson, EventResponseDTO.class);
        UUID eventId = event.getId();

        // 2. Submit feedback
        FeedbackRequestDTO feedbackRequest = new FeedbackRequestDTO();
        feedbackRequest.setMessage("Amazing speakers and good organization");

        mockMvc.perform(post("/events/{eventId}/feedback", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isOk());

        // 3. Retrieve sentiment breakdown
        mockMvc.perform(get("/events/{eventId}/summary", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFeedbacks").value(1));
    }

    @Test
    void getSentimentBreakdown_ShouldReturnZeroForNewEvent() throws Exception {
        EventRequestDTO eventRequest = new EventRequestDTO();
        eventRequest.setTitle("Art Exhibition");
        eventRequest.setDescription("Modern art from local artists");

        String eventJson = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EventResponseDTO event = objectMapper.readValue(eventJson, EventResponseDTO.class);

        mockMvc.perform(get("/events/{eventId}/summary", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFeedbacks").value(0))
                .andExpect(jsonPath("$.positivePercent").value(0.0));
    }
}
