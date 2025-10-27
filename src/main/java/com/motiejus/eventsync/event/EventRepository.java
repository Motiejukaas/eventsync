package com.motiejus.eventsync.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.feedbacks WHERE e.id = :id")
    Optional<Event> getEventWithFeedbacksById(@Param("id") UUID id);
}
