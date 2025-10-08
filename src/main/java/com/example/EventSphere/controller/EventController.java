package com.example.EventSphere.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.RSVP;
import com.example.EventSphere.model.User;
import com.example.EventSphere.model.Volunteer;
import com.example.EventSphere.service.EventService;
import com.example.EventSphere.service.RSVPService;
import com.example.EventSphere.service.VolunteerService;

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
        
        boolean isOrganizerView = false;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                model.addAttribute("currentUser", user);
                
                // Check if user has RSVPed - use IDs to avoid lazy loading issues
                model.addAttribute("userRSVP", rsvpService.getRSVPByIds(eventId, user.getUserId()).orElse(null));
                model.addAttribute("userVolunteer", volunteerService.getVolunteerRegistrationByIds(eventId, user.getUserId()).orElse(null));
                
                isOrganizerView = eventService.canUserManageEvent(event, user);
            }
            // If admin is viewing, they can see the event but not as organizer
        }
        
        if (isOrganizerView) {
            model.addAttribute("participants", rsvpService.getEventRSVPsWithUsers(event));
            model.addAttribute("volunteers", volunteerService.getEventVolunteersWithUsers(event));
        } else {
            model.addAttribute("participants", Collections.emptyList());
            model.addAttribute("volunteers", Collections.emptyList());
        }
        
        model.addAttribute("isOrganizerView", isOrganizerView);
        return "event-details";
    }
    
    @GetMapping("/create")
    public String createEventForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            if (user.getRole() == User.Role.USER) {
                return "redirect:/";
            }
            model.addAttribute("event", new Event());
            model.addAttribute("categories", Event.Category.values());
            return "create-event";
        } else {
            // Admin trying to create event - redirect to admin dashboard
            return "redirect:/admin/dashboard";
        }
    }
    
    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            redirectAttributes.addFlashAttribute("error", "Only organizers can create events.");
            return "redirect:/admin/dashboard";
        }
        
        User user = (User) principal;
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
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            // Admin trying to edit - redirect to admin events page
            return "redirect:/admin/events";
        }
        
        User user = (User) principal;
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
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this event.");
            return "redirect:/admin/events";
        }
        
        User user = (User) principal;
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
    public String rsvpToEvent(@PathVariable Long eventId, 
                             @RequestParam String status,
                             @RequestParam(required = false) String teamName,
                             @RequestParam(required = false) Integer teamSize,
                             Authentication authentication, 
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Please login to RSVP.");
            return "redirect:/login";
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            redirectAttributes.addFlashAttribute("error", "Admins cannot RSVP to events.");
            return "redirect:/events/" + eventId;
        }
        
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = (User) principal;
        
        try {
            com.example.EventSphere.model.RSVP.Status rsvpStatus = com.example.EventSphere.model.RSVP.Status.valueOf(status.toUpperCase());
            
            // Validate team information for GROUP events
            if (event.getParticipationType() == com.example.EventSphere.model.ParticipationType.GROUP 
                && rsvpStatus == com.example.EventSphere.model.RSVP.Status.GOING) {
                if (teamName == null || teamName.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Team name is required for group events.");
                    return "redirect:/events/" + eventId;
                }
                if (teamSize == null || teamSize < 1) {
                    redirectAttributes.addFlashAttribute("error", "Valid team size is required.");
                    return "redirect:/events/" + eventId;
                }
                if (event.getGroupSize() != null && teamSize > event.getGroupSize()) {
                    redirectAttributes.addFlashAttribute("error", "Team size cannot exceed " + event.getGroupSize() + " members.");
                    return "redirect:/events/" + eventId;
                }
            }
            
            rsvpService.createRSVP(event, user, rsvpStatus, teamName, teamSize);
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
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            redirectAttributes.addFlashAttribute("error", "Admins cannot volunteer for events.");
            return "redirect:/events/" + eventId;
        }
        
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = (User) principal;
        
        try {
            volunteerService.registerVolunteer(event, user, roleDescription);
            redirectAttributes.addFlashAttribute("success", "Volunteer registration submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to register as volunteer: " + e.getMessage());
        }
        
        return "redirect:/events/" + eventId;
    }
    
    @GetMapping("/{eventId}/participants/export")
    public ResponseEntity<byte[]> exportParticipants(@PathVariable Long eventId, Authentication authentication) {
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!eventService.canUserManageEvent(event, getAuthenticatedUser(authentication))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<RSVP> participants = rsvpService.getEventRSVPsWithUsers(event);
        String csv = buildParticipantsCsv(event, participants);
        return buildCsvResponse(csv, event.getTitle(), "participants");
    }
    
    @GetMapping("/{eventId}/volunteers/export")
    public ResponseEntity<byte[]> exportVolunteers(@PathVariable Long eventId, Authentication authentication) {
        Event event = eventService.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!eventService.canUserManageEvent(event, getAuthenticatedUser(authentication))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Volunteer> volunteers = volunteerService.getEventVolunteersWithUsers(event);
        String csv = buildVolunteersCsv(event, volunteers);
        return buildCsvResponse(csv, event.getTitle(), "volunteers");
    }
    
    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized access");
        }
        return (User) authentication.getPrincipal();
    }
    
    private String buildParticipantsCsv(Event event, List<RSVP> participants) {
        String header = "Participant Name,Email,Status,RSVP Date,Team Name,Team Size";
        String rows = participants.stream()
            .map(rsvp -> String.join(",",
                safeCsv(rsvp.getUser().getName()),
                safeCsv(rsvp.getUser().getEmail()),
                rsvp.getStatus().name(),
                rsvp.getRsvpDate() != null ? rsvp.getRsvpDate().toString() : "",
                safeCsv(rsvp.getTeamName()),
                rsvp.getTeamSize() != null ? rsvp.getTeamSize().toString() : ""))
            .collect(Collectors.joining("\n"));
        return header + "\n" + rows;
    }
    
    private String buildVolunteersCsv(Event event, List<Volunteer> volunteers) {
        String header = "Volunteer Name,Email,Status,Role Description,Registration Date";
        String rows = volunteers.stream()
            .map(volunteer -> String.join(",",
                safeCsv(volunteer.getUser().getName()),
                safeCsv(volunteer.getUser().getEmail()),
                volunteer.getStatus().name(),
                safeCsv(volunteer.getRoleDescription()),
                volunteer.getRegistrationDate() != null ? volunteer.getRegistrationDate().toString() : ""))
            .collect(Collectors.joining("\n"));
        return header + "\n" + rows;
    }
    
    private ResponseEntity<byte[]> buildCsvResponse(String csvContent, String eventTitle, String type) {
        String sanitizedTitle = eventTitle.replaceAll("[^a-zA-Z0-9]+", "-").toLowerCase();
        String filename = String.format("%s-%s.csv", sanitizedTitle, type);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
    
    private String safeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
