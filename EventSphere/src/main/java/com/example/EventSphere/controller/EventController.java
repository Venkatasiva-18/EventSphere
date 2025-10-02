package com.example.EventSphere.controller;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.service.EventService;
import com.example.EventSphere.service.RSVPService;
import com.example.EventSphere.service.VolunteerService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    private final RSVPService rsvpService;
    private final VolunteerService volunteerService;
    
    public EventController(EventService eventService, RSVPService rsvpService, VolunteerService volunteerService) {
        this.eventService = eventService;
        this.rsvpService = rsvpService;
        this.volunteerService = volunteerService;
    }
    
    @GetMapping("/{eventId}")
    public String eventDetails(@PathVariable Long eventId, Model model, Authentication authentication) {
        Event event = eventService.findByIdWithDetails(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        model.addAttribute("event", event);
        model.addAttribute("categories", Event.Category.values());
        
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", user);
            
            // Check if user has RSVPed - use IDs to avoid lazy loading issues
            model.addAttribute("userRSVP", rsvpService.getRSVPByIds(eventId, user.getUserId()).orElse(null));
            model.addAttribute("userVolunteer", volunteerService.getVolunteerRegistrationByIds(eventId, user.getUserId()).orElse(null));
        }
        
        return "event-details";
    }
    
    @GetMapping("/create")
    public String createEventForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = (User) authentication.getPrincipal();
        if (user.getRole() == User.Role.USER) {
            return "redirect:/";
        }
        
        model.addAttribute("event", new Event());
        model.addAttribute("categories", Event.Category.values());
        return "create-event";
    }
    
    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = (User) authentication.getPrincipal();
        if (user.getRole() == User.Role.USER) {
            redirectAttributes.addFlashAttribute("error", "Only organizers can create events.");
            return "redirect:/";
        }
        
        try {
            Event savedEvent = eventService.createEvent(event, user);
            redirectAttributes.addFlashAttribute("success", "Event created successfully!");
            return "redirect:/events/" + savedEvent.getEventId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create event: " + e.getMessage());
            return "redirect:/events/create";
        }
    }
    
    @GetMapping("/{eventId}/edit")
    public String editEventForm(@PathVariable Long eventId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = (User) authentication.getPrincipal();
        if (!eventService.canUserManageEvent(event, user)) {
            return "redirect:/events/" + eventId;
        }
        
        model.addAttribute("event", event);
        model.addAttribute("categories", Event.Category.values());
        return "edit-event";
    }
    
    @PostMapping("/{eventId}/edit")
    public String updateEvent(@PathVariable Long eventId, @ModelAttribute Event event, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Event existingEvent = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = (User) authentication.getPrincipal();
        if (!eventService.canUserManageEvent(existingEvent, user)) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this event.");
            return "redirect:/events/" + eventId;
        }
        
        try {
            existingEvent.setTitle(event.getTitle());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setCategory(event.getCategory());
            existingEvent.setLocation(event.getLocation());
            existingEvent.setDateTime(event.getDateTime());
            existingEvent.setEndDateTime(event.getEndDateTime());
            existingEvent.setMaxParticipants(event.getMaxParticipants());
            existingEvent.setRequiresApproval(event.getRequiresApproval());
            
            eventService.updateEvent(existingEvent);
            redirectAttributes.addFlashAttribute("success", "Event updated successfully!");
            return "redirect:/events/" + eventId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update event: " + e.getMessage());
            return "redirect:/events/" + eventId + "/edit";
        }
    }
    
    @PostMapping("/{eventId}/rsvp")
    public String rsvpToEvent(@PathVariable Long eventId, @RequestParam String status, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Please login to RSVP.");
            return "redirect:/login";
        }
        
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = (User) authentication.getPrincipal();
        
        try {
            com.example.EventSphere.model.RSVP.Status rsvpStatus = com.example.EventSphere.model.RSVP.Status.valueOf(status.toUpperCase());
            rsvpService.createRSVP(event, user, rsvpStatus);
            redirectAttributes.addFlashAttribute("success", "RSVP updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update RSVP: " + e.getMessage());
        }
        
        return "redirect:/events/" + eventId;
    }
    
    @PostMapping("/{eventId}/volunteer")
    public String volunteerForEvent(@PathVariable Long eventId, @RequestParam String roleDescription, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Please login to volunteer.");
            return "redirect:/login";
        }
        
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = (User) authentication.getPrincipal();
        
        try {
            volunteerService.registerVolunteer(event, user, roleDescription);
            redirectAttributes.addFlashAttribute("success", "Volunteer registration submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to register as volunteer: " + e.getMessage());
        }
        
        return "redirect:/events/" + eventId;
    }
}
