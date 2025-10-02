package com.example.EventSphere.service;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.RSVP;
import com.example.EventSphere.model.User;
import com.example.EventSphere.repository.RSVPRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RSVPService {
    
    private final RSVPRepository rsvpRepository;
    
    public RSVPService(RSVPRepository rsvpRepository) {
        this.rsvpRepository = rsvpRepository;
    }
    
    public RSVP createRSVP(Event event, User user, RSVP.Status status) {
        // Check if user already has an RSVP for this event
        Optional<RSVP> existingRSVP = rsvpRepository.findByEventAndUser(event, user);
        
        if (existingRSVP.isPresent()) {
            RSVP rsvp = existingRSVP.get();
            rsvp.setStatus(status);
            return rsvpRepository.save(rsvp);
        } else {
            RSVP rsvp = new RSVP(event, user, status);
            return rsvpRepository.save(rsvp);
        }
    }
    
    public RSVP updateRSVPStatus(Event event, User user, RSVP.Status newStatus) {
        RSVP rsvp = rsvpRepository.findByEventAndUser(event, user)
            .orElseThrow(() -> new RuntimeException("RSVP not found"));
        
        rsvp.setStatus(newStatus);
        return rsvpRepository.save(rsvp);
    }
    
    public void deleteRSVP(Event event, User user) {
        RSVP rsvp = rsvpRepository.findByEventAndUser(event, user)
            .orElseThrow(() -> new RuntimeException("RSVP not found"));
        
        rsvpRepository.delete(rsvp);
    }
    
    public Optional<RSVP> getRSVP(Event event, User user) {
        return rsvpRepository.findByEventAndUser(event, user);
    }
    
    public Optional<RSVP> getRSVPByIds(Long eventId, Long userId) {
        return rsvpRepository.findByEventIdAndUserId(eventId, userId);
    }
    
    public List<RSVP> getUserRSVPs(User user) {
        return rsvpRepository.findByUser(user);
    }
    
    public List<RSVP> getEventRSVPs(Event event) {
        return rsvpRepository.findByEvent(event);
    }
    
    public List<RSVP> getEventRSVPsByStatus(Event event, RSVP.Status status) {
        return rsvpRepository.findByEventAndStatus(event, status);
    }
    
    public List<RSVP> getUserGoingEvents(User user) {
        return rsvpRepository.findUserGoingEvents(user);
    }
    
    public long getGoingParticipantsCount(Event event) {
        return rsvpRepository.countGoingParticipants(event);
    }
    
    public long getInterestedParticipantsCount(Event event) {
        return rsvpRepository.countInterestedParticipants(event);
    }
    
    public boolean hasUserRSVPed(Event event, User user) {
        return rsvpRepository.existsByEventAndUser(event, user);
    }
    
    public boolean isUserGoing(Event event, User user) {
        Optional<RSVP> rsvp = rsvpRepository.findByEventAndUser(event, user);
        return rsvp.isPresent() && rsvp.get().getStatus() == RSVP.Status.GOING;
    }
    
    public boolean isUserInterested(Event event, User user) {
        Optional<RSVP> rsvp = rsvpRepository.findByEventAndUser(event, user);
        return rsvp.isPresent() && rsvp.get().getStatus() == RSVP.Status.INTERESTED;
    }
}
