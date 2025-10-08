package com.example.EventSphere.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.service.EventService;
import com.example.EventSphere.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final EventService eventService;
    
    public AdminController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }
    
    @GetMapping("/login")
    public String showLoginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/login";
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        List<User> allUsers = userService.getAllUsers();
        List<Event> allEvents = eventService.getAllEvents();

        List<Event> activeUpcomingEvents = allEvents.stream()
            .filter(event -> Boolean.TRUE.equals(event.getActive()))
            .filter(event -> event.getEndDateTime() == null || !event.getEndDateTime().isBefore(currentTime))
            .sorted(Comparator.comparing(Event::getDateTime).reversed())
            .toList();

        long totalActiveEvents = allEvents.stream().filter(Event::getActive).count();
        long totalInactiveEvents = allEvents.size() - totalActiveEvents;
        long pendingEventsCount = eventService.getPendingEvents().size();

        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalEvents", allEvents.size());
        model.addAttribute("activeEvents", totalActiveEvents);
        model.addAttribute("inactiveEvents", totalInactiveEvents);
        model.addAttribute("pendingEvents", pendingEventsCount);
        model.addAttribute("recentEvents", activeUpcomingEvents.stream().limit(5).toList());
        model.addAttribute("recentUsers", allUsers.stream().limit(5).toList());

        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/login";
        }
        
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        
        return "admin/users";
    }
    
    @GetMapping("/events")
    public String manageEvents(Model model, Authentication authentication, @RequestParam(name = "filter", defaultValue = "active") String filter) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/login";
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        List<Event> allEvents = eventService.getAllEvents();

        List<Event> filteredEvents = switch (filter) {
            case "pending" -> eventService.getPendingEvents();
            case "inactive" -> allEvents.stream()
                .filter(event -> !Boolean.TRUE.equals(event.getActive()))
                .sorted(Comparator.comparing(Event::getDateTime).reversed())
                .toList();
            case "upcoming" -> allEvents.stream()
                .filter(event -> Boolean.TRUE.equals(event.getActive()))
                .filter(event -> event.getDateTime().isAfter(currentTime))
                .sorted(Comparator.comparing(Event::getDateTime).reversed())
                .toList();
            default -> allEvents.stream()
                .filter(event -> Boolean.TRUE.equals(event.getActive()))
                .filter(event -> event.getEndDateTime() == null || !event.getEndDateTime().isBefore(currentTime))
                .sorted(Comparator.comparing(Event::getDateTime).reversed())
                .toList();
        };

        model.addAttribute("events", filteredEvents);
        model.addAttribute("selectedFilter", filter);
        model.addAttribute("pendingCount", eventService.getPendingEvents().size());
        model.addAttribute("inactiveCount", allEvents.stream().filter(event -> !Boolean.TRUE.equals(event.getActive())).count());
        model.addAttribute("upcomingCount", allEvents.stream().filter(event -> Boolean.TRUE.equals(event.getActive()) && event.getDateTime().isAfter(currentTime)).count());

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

    @PostMapping("/events/{eventId}/activate")
    public String activateEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        try {
            eventService.activateEvent(eventId);
            redirectAttributes.addFlashAttribute("success", "Event reactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reactivate event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/events/{eventId}/delete")
    public String deleteEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(eventId);
            redirectAttributes.addFlashAttribute("success", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/settings")
    public String settings(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/login";
        }
        
        return "admin/settings";
    }

    @GetMapping("/reports")
    public String reports(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/login";
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        List<Event> allEvents = eventService.getAllEvents();
        List<User> allUsers = userService.getAllUsers();
        
        // Calculate statistics
        long totalUsers = allUsers.size();
        long totalOrganizers = allUsers.stream().filter(u -> u.getRole() == User.Role.ORGANIZER).count();
        long totalAdmins = allUsers.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        
        long totalEvents = allEvents.size();
        long activeEvents = allEvents.stream().filter(Event::getActive).count();
        long upcomingEvents = allEvents.stream()
            .filter(event -> Boolean.TRUE.equals(event.getActive()))
            .filter(event -> event.getDateTime().isAfter(currentTime))
            .count();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrganizers", totalOrganizers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("activeEvents", activeEvents);
        model.addAttribute("upcomingEvents", upcomingEvents);
        
        return "admin/reports";
    }

    @GetMapping("/public-site")
    public String redirectToPublicSite() {
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String redirectToProfile() {
        return "redirect:/user/profile";
    }

    @GetMapping("/events/{eventId}/view")
    public String viewEvent(@PathVariable Long eventId) {
        return "redirect:/events/" + eventId;
    }

}
