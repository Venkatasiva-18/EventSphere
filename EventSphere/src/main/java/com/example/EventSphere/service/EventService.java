package com.example.EventSphere.service;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
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
    
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
    
    public void deactivateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setActive(false);
        eventRepository.save(event);
    }
    
    public Optional<Event> findById(Long eventId) {
        return eventRepository.findById(eventId);
    }
    
    public List<Event> getAllActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }
    
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }
    
    public List<Event> getEventsByCategory(Event.Category category) {
        return eventRepository.findByCategory(category);
    }
    
    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }
    
    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword);
    }
    
    public List<Event> getEventsByLocation(String location) {
        return eventRepository.findByLocationContaining(location);
    }
    
    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findEventsByDateRange(start, end);
    }
    
    public List<Event> getEventsRequiringApproval() {
        return eventRepository.findEventsRequiringApproval();
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
