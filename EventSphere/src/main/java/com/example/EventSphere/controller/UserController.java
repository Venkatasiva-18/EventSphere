package com.example.EventSphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.EventSphere.model.User;
import com.example.EventSphere.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Attempting to register user: " + user.getEmail());
            User registeredUser = userService.registerUser(user);
            System.out.println("User registration completed successfully for: " + registeredUser.getEmail());
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            System.err.println("Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @PostMapping("/register-organizer")
    public String registerOrganizer(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerOrganizer(user);
            redirectAttributes.addFlashAttribute("success", "Organizer registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register-organizer";
        }
    }
    
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                model.addAttribute("user", user);
                return "user/profile";
            } else {
                // Admin trying to access user profile - redirect to admin dashboard
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/login";
    }
    
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User currentUser = (User) principal;
                currentUser.setName(user.getName());
                currentUser.setPhone(user.getPhone());
                
                userService.updateUser(currentUser);
                redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
                return "redirect:/user/profile";
            } else {
                // Admin trying to update profile
                redirectAttributes.addFlashAttribute("error", "Admins cannot update user profiles.");
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/login";
    }
}
