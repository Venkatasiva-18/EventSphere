package com.example.EventSphere.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.repository.EventRepository;

@Service
@Transactional
public class EventService {
    
    private final EventRepository eventRepository;
    
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    public Event createEvent(Event event, User organizer) {
        event.setOrganizer(organizer);
        event.setCreatedAt(LocalDateTime.now());
        event.setActive(true);
        
        return eventRepository.save(event);
    }
    
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public boolean isRegistrationClosed(Event event, LocalDateTime referenceTime) {
        LocalDateTime deadline = event.getRegistrationDeadline();
        if (deadline == null) {
            return false;
        }
        return !referenceTime.isBefore(deadline);
    }
    
    @Transactional(readOnly = true)
    public Event getEventWithDetails(Long eventId) {
        Event event = eventRepository.findByIdWithDetails(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Initialize lazy collections within transaction to avoid LazyInitializationException
        if (event.getRsvps() != null) {
            event.getRsvps().size(); // Force initialization
        }
        if (event.getVolunteers() != null) {
            event.getVolunteers().size(); // Force initialization
        }
        if (event.getOrganizer() != null) {
            event.getOrganizer().getUserId(); // Force initialization of organizer
        }
        
        return event;
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // The cascade will handle deletion of RSVPs and Volunteers
        eventRepository.delete(event);
    }
    
    public void deactivateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setActive(false);
        eventRepository.save(event);
    }

    public void activateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        // Admin can activate any event, including those that were previously deactivated
        // The requiresApproval field is for future event approval workflow
        event.setActive(true);
        eventRepository.save(event);
    }
    
    public Optional<Event> findById(Long eventId) {
        // Use findByIdWithOrganizer to eagerly fetch the organizer and avoid lazy loading issues
        return eventRepository.findByIdWithOrganizer(eventId);
    }
    
    public Optional<Event> findByIdWithDetails(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findByIdWithDetails(eventId);
        // Initialize lazy collections within transaction
        eventOpt.ifPresent(event -> {
            if (event.getRsvps() != null) {
                event.getRsvps().size(); // Force initialization
            }
            if (event.getVolunteers() != null) {
                event.getVolunteers().size(); // Force initialization
            }
            if (event.getOrganizer() != null) {
                event.getOrganizer().getUserId(); // Force initialization of organizer
            }
        });
        return eventOpt;
    }
    
    public List<Event> getAllActiveEvents() {
        return eventRepository.findAllActiveWithDetails();
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAllWithDetails();
    }

    public List<Event> getPendingEvents() {
        return eventRepository.findAllWithDetails().stream()
            .filter(event -> Boolean.TRUE.equals(event.getRequiresApproval()))
            .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
            .toList();
    }


    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEventsWithDetails(LocalDateTime.now());
    }
    
    public List<Event> getEventsByCategory(Event.Category category) {
        return eventRepository.findByCategoryWithDetails(category);
    }
    
    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }
    
    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchEventsWithDetails(keyword);
    }
    
    public List<Event> getEventsByLocation(String location) {
        return eventRepository.findByLocationContainingWithDetails(location);
    }
    
    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findEventsByDateRange(start, end);
    }
    

    public List<Event> getEventsCompletedBefore(LocalDateTime cutoff) {
        return eventRepository.findCompletedEventsBefore(cutoff);
    }
    
    public List<Event> deleteEventsCompletedBefore(LocalDateTime cutoff) {
        List<Event> eventsToDelete = getEventsCompletedBefore(cutoff);
        if (!eventsToDelete.isEmpty()) {
            eventRepository.deleteAll(eventsToDelete);
        }
        return List.copyOf(eventsToDelete);
    }
    
    public List<Event> getEventsByDateRangeAndCategory(LocalDateTime start, LocalDateTime end, Event.Category category) {
        List<Event> events = getEventsByDateRange(start, end);
        return events.stream()
            .filter(event -> event.getCategory() == category)
            .toList();
    }
    
    public boolean isEventFull(Event event) {
        return event.isFull();
    }
    
    public boolean isEventUpcoming(Event event) {
        return event.isUpcoming();
    }
    
    public boolean canUserManageEvent(Event event, User user) {
        return event.getOrganizer().getUserId().equals(user.getUserId()) || 
               user.getRole() == User.Role.ADMIN;
    }
}
