package com.example.EventSphere.controller;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.service.EventService;
import com.example.EventSphere.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final EventService eventService;
    
    public AdminController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = (User) authentication.getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/";
        }
        
        List<User> allUsers = userService.getAllUsers();
        List<Event> allEvents = eventService.getAllActiveEvents();
        List<Event> eventsRequiringApproval = eventService.getEventsRequiringApproval();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalEvents", allEvents.size());
        model.addAttribute("eventsRequiringApproval", eventsRequiringApproval.size());
        model.addAttribute("recentEvents", allEvents.stream().limit(5).toList());
        model.addAttribute("recentUsers", allUsers.stream().limit(5).toList());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = (User) authentication.getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/";
        }
        
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        
        return "admin/users";
    }
    
    @GetMapping("/events")
    public String manageEvents(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = (User) authentication.getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/";
        }
        
        List<Event> events = eventService.getAllActiveEvents();
        model.addAttribute("events", events);
        
        return "admin/events";
    }
    
    @PostMapping("/users/{userId}/enable")
    public String enableUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.enableUser(userId);
            redirectAttributes.addFlashAttribute("success", "User enabled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to enable user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{userId}/disable")
    public String disableUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.disableUser(userId);
            redirectAttributes.addFlashAttribute("success", "User disabled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to disable user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{userId}/role")
    public String changeUserRole(@PathVariable Long userId, @RequestParam String role, RedirectAttributes redirectAttributes) {
        try {
            User.Role newRole = User.Role.valueOf(role.toUpperCase());
            userService.changeUserRole(userId, newRole);
            redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/events/{eventId}/deactivate")
    public String deactivateEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        try {
            eventService.deactivateEvent(eventId);
            redirectAttributes.addFlashAttribute("success", "Event deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to deactivate event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }
}
