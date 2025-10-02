package com.example.EventSphere.controller;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.service.EventService;
import com.example.EventSphere.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
            User user = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", user);
        }
        
        return "index";
    }
    
    @GetMapping("/events")
    public String events(@RequestParam(required = false) String category,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String location,
                        Model model, Authentication authentication) {
        
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
        
        model.addAttribute("events", events);
        model.addAttribute("categories", Event.Category.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", search);
        model.addAttribute("locationQuery", location);
        
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", user);
        }
        
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
