package com.example.EventSphere.service;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.model.Volunteer;
import com.example.EventSphere.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {
    
    private final VolunteerRepository volunteerRepository;
    
    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }
    
    public Volunteer registerVolunteer(Event event, User user, String roleDescription) {
        // Check if user is already registered as volunteer for this event
        Optional<Volunteer> existingVolunteer = volunteerRepository.findByEventAndUser(event, user);
        
        if (existingVolunteer.isPresent()) {
            throw new RuntimeException("User is already registered as volunteer for this event");
        }
        
        Volunteer volunteer = new Volunteer(event, user, roleDescription);
        return volunteerRepository.save(volunteer);
    }
    
    public Volunteer updateVolunteerStatus(Event event, User user, Volunteer.Status status) {
        Volunteer volunteer = volunteerRepository.findByEventAndUser(event, user)
            .orElseThrow(() -> new RuntimeException("Volunteer registration not found"));
        
        volunteer.setStatus(status);
        return volunteerRepository.save(volunteer);
    }
    
    public Volunteer updateVolunteerRole(Event event, User user, String roleDescription) {
        Volunteer volunteer = volunteerRepository.findByEventAndUser(event, user)
            .orElseThrow(() -> new RuntimeException("Volunteer registration not found"));
        
        volunteer.setRoleDescription(roleDescription);
        return volunteerRepository.save(volunteer);
    }
    
    public void deleteVolunteerRegistration(Event event, User user) {
        Volunteer volunteer = volunteerRepository.findByEventAndUser(event, user)
            .orElseThrow(() -> new RuntimeException("Volunteer registration not found"));
        
        volunteerRepository.delete(volunteer);
    }
    
    public Optional<Volunteer> getVolunteerRegistration(Event event, User user) {
        return volunteerRepository.findByEventAndUser(event, user);
    }
    
    public List<Volunteer> getUserVolunteerRegistrations(User user) {
        return volunteerRepository.findByUser(user);
    }
    
    public List<Volunteer> getEventVolunteers(Event event) {
        return volunteerRepository.findByEvent(event);
    }
    
    public List<Volunteer> getEventVolunteersByStatus(Event event, Volunteer.Status status) {
        return volunteerRepository.findByEventAndStatus(event, status);
    }
    
    public List<Volunteer> getUserApprovedVolunteerRoles(User user) {
        return volunteerRepository.findUserApprovedVolunteerRoles(user);
    }
    
    public long getApprovedVolunteersCount(Event event) {
        return volunteerRepository.countApprovedVolunteers(event);
    }
    
    public long getPendingVolunteersCount(Event event) {
        return volunteerRepository.countPendingVolunteers(event);
    }
    
    public boolean hasUserVolunteered(Event event, User user) {
        return volunteerRepository.existsByEventAndUser(event, user);
    }
    
    public boolean isUserApprovedVolunteer(Event event, User user) {
        Optional<Volunteer> volunteer = volunteerRepository.findByEventAndUser(event, user);
        return volunteer.isPresent() && volunteer.get().getStatus() == Volunteer.Status.APPROVED;
    }
    
    public boolean isUserPendingVolunteer(Event event, User user) {
        Optional<Volunteer> volunteer = volunteerRepository.findByEventAndUser(event, user);
        return volunteer.isPresent() && volunteer.get().getStatus() == Volunteer.Status.PENDING;
    }
}
