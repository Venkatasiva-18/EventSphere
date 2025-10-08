package com.example.EventSphere.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.EventSphere.model.PasswordResetToken;
import com.example.EventSphere.model.User;
import com.example.EventSphere.service.EmailService;
import com.example.EventSphere.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordResetController {

    private final UserService userService;
    private final EmailService emailService;

    public PasswordResetController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            PasswordResetToken token = userService.createPasswordResetToken(user);

            String applicationUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

            String resetLink = UriComponentsBuilder.fromHttpUrl(applicationUrl)
                .path("/reset-password")
                .queryParam("token", token.getToken())
                .toUriString();

            emailService.sendPasswordResetEmail(user, resetLink);
        }

        redirectAttributes.addFlashAttribute("success",
            "If an account with that email exists, a reset link has been sent.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> passwordResetToken = userService.findToken(token);

        if (passwordResetToken.isEmpty() || passwordResetToken.get().isExpired()) {
            passwordResetToken.ifPresent(t -> userService.deleteToken(t.getToken()));
            redirectAttributes.addFlashAttribute("error",
                "The reset link is invalid or has expired. Please request a new one.");
            return "redirect:/forgot-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        Optional<PasswordResetToken> passwordResetToken = userService.findToken(token);

        if (passwordResetToken.isEmpty() || passwordResetToken.get().isExpired()) {
            passwordResetToken.ifPresent(t -> userService.deleteToken(t.getToken()));
            redirectAttributes.addFlashAttribute("error",
                "The reset link is invalid or has expired. Please request a new one.");
            return "redirect:/forgot-password";
        }

        User user = passwordResetToken.get().getUser();
        userService.updatePassword(user, password);
        userService.deleteToken(token);

        redirectAttributes.addFlashAttribute("success",
            "Password updated successfully. You can now log in with your new password.");
        return "redirect:/login";
    }
}