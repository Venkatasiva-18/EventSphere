package com.example.EventSphere.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.service.EventService;

@Component
public class EventCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventCleanupScheduler.class);

    private final EventService eventService;

    public EventCleanupScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runCleanupOnStartup() {
        logger.info("Running initial event cleanup on application startup");
        purgeCompletedEvents();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledCleanup() {
        purgeCompletedEvents();
    }

    @Transactional
    void purgeCompletedEvents() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Event> eventsToDelete = eventService.deleteEventsCompletedBefore(currentTime);

        if (eventsToDelete.isEmpty()) {
            logger.debug("No completed events found before {} for deletion.", currentTime);
            return;
        }

        logger.info("Deleted {} events completed before {}", eventsToDelete.size(), currentTime);
        eventsToDelete.forEach(event ->
            logger.debug("Deleted event with id {} titled '{}'", event.getEventId(), event.getTitle())
        );
    }
}