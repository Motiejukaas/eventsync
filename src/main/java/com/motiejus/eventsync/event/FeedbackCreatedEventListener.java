package com.motiejus.eventsync.event;


import com.motiejus.eventsync.feedback.FeedbackCreatedEvent;
import com.motiejus.eventsync.sentiment.SentimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FeedbackCreatedEventListener {
    private final SentimentService sentimentService;
    private final EventService eventService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFeedbackCreatedEvent(FeedbackCreatedEvent publishedEvent) {
        Event event = eventService.getEventWithFeedbacksById(publishedEvent.getEventId());

        int total = event.getPositiveFeedbackSentimentCount() +
                    event.getNegativeFeedbackSentimentCount() +
                    event.getNegativeFeedbackSentimentCount();

        //alternative
        //int triggerInterval = Math.max(1, (int) (5 * Math.log(total)));

        int triggerInterval = Math.max(1, (int)Math.sqrt(total));

        if ((total % triggerInterval) == 0) {
            eventService.updateFeedbackSummary(event, sentimentService.summariseSentiments(event));
        }
    }

}
