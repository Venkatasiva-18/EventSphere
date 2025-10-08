package com.example.EventSphere.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.service.EventService;
import com.example.EventSphere.service.UserService;

@Controller
public class WebController {
    
    private final EventService eventService;
    private final UserService userService;
    
    public WebController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }
    
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        List<Event> upcomingEvents = eventService.getUpcomingEvents();
        model.addAttribute("events", upcomingEvents);
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                model.addAttribute("currentUser", user);
            }
            // If admin is viewing, don't add currentUser to model
        }
        
        return "index";
    }
    
    @GetMapping("/events")
    public String events(@RequestParam(required = false) String category,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String location,
                        Model model, Authentication authentication) {
        
        LocalDateTime currentTime = LocalDateTime.now();
        eventService.deleteEventsCompletedBefore(currentTime);
        List<Event> events;
        
        if (search != null && !search.trim().isEmpty()) {
            events = eventService.searchEvents(search);
        } else if (category != null && !category.trim().isEmpty()) {
            try {
                Event.Category eventCategory = Event.Category.valueOf(category.toUpperCase());
                events = eventService.getEventsByCategory(eventCategory);
            } catch (IllegalArgumentException e) {
                events = eventService.getAllActiveEvents();
            }
        } else if (location != null && !location.trim().isEmpty()) {
            events = eventService.getEventsByLocation(location);
        } else {
            events = eventService.getAllActiveEvents();
        }
        
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                currentUser = (User) principal;
                model.addAttribute("currentUser", currentUser);
            }
        }
        
        // Filter and sort events - organizer's events first
        final User finalCurrentUser = currentUser;
        List<Event> filteredEvents = events.stream()
            .filter(event -> event.getEndDateTime() == null || !event.getEndDateTime().isBefore(currentTime))
            .sorted((e1, e2) -> {
                // Sort organizer's events first
                if (finalCurrentUser != null) {
                    boolean e1IsOrganizer = e1.getOrganizer().getUserId().equals(finalCurrentUser.getUserId());
                    boolean e2IsOrganizer = e2.getOrganizer().getUserId().equals(finalCurrentUser.getUserId());
                    
                    if (e1IsOrganizer && !e2IsOrganizer) return -1;
                    if (!e1IsOrganizer && e2IsOrganizer) return 1;
                }
                // Then sort by date (most recent first)
                return e2.getDateTime().compareTo(e1.getDateTime());
            })
            .toList();
        
        model.addAttribute("events", filteredEvents);
        model.addAttribute("categories", Event.Category.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", search);
        model.addAttribute("locationQuery", location);
        
        return "events";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @GetMapping("/register-organizer")
    public String registerOrganizer(Model model) {
        model.addAttribute("user", new User());
        return "register-organizer";
    }
}
